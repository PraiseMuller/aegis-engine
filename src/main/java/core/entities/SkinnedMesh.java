package core.entities;

import core.utils.AssetPool;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glDrawElementsBaseVertex;

public class SkinnedMesh {

    private final int vao;
    private final AIScene aiScene;

    //temp storage for stuff before being sent to the gpu
    private final ArrayList<Vector3f> m_Positions = new ArrayList<>();
    private final ArrayList<Vector3f> m_Normals = new ArrayList<>();
    private final ArrayList<Vector2f> m_TexCords = new ArrayList<>();
    private final ArrayList<Integer> m_Indices = new ArrayList<>();
    private final ArrayList<VertexBoneData> m_Bones = new ArrayList<>();

    private final Map<String, Integer> boneNameToIndexMap = new HashMap<>();
    private final ArrayList<BasicMeshEntry> m_Meshes = new ArrayList<>();
    private final ArrayList<BoneInfo> m_BoneInfo = new ArrayList<>();

    private enum BUFFER_TYPE  {
        POSITIONS,
        TEX_CORDS,
        NORMALS,
        BONE_IDS,
        BONE_WEIGHTS,
        INDICES
    }
    private final int[] attributeBuffers  = new int[BUFFER_TYPE.values().length];


    public SkinnedMesh(String fileLocation){

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        for(int i = 0; i < this.attributeBuffers.length; i++){
            this.attributeBuffers[i] = glGenBuffers();
        }

        int pFlags = aiProcess_Triangulate | aiProcess_GenSmoothNormals | aiProcess_FlipUVs | aiProcess_JoinIdenticalVertices;
        this.aiScene = aiImportFile(fileLocation, pFlags);
        assert aiScene != null;
        _initAiScene();

        glBindVertexArray(0);
    }

    private void _initAiScene(){

        int num_vertices = 0, num_indices = 0;   //count vertices
        _countVerticesAndIndices(num_vertices, num_indices);
        _initAllMeshes();
        _populateBuffers();
    }

    private void _countVerticesAndIndices(int num_vertices, int num_indices){

        PointerBuffer meshesPBuffer = aiScene.mMeshes();
        for(int i = 0; i < aiScene.mNumMeshes(); i++){

            AIMesh aiMesh = AIMesh.create(meshesPBuffer.get(i));

            this.m_Meshes.add(new BasicMeshEntry());
            this.m_Meshes.get(i).materialIndex = aiMesh.mMaterialIndex();
            this.m_Meshes.get(i).numIndices = aiMesh.mNumFaces() * 3;
            this.m_Meshes.get(i).baseVertex = num_vertices;
            this.m_Meshes.get(i).baseIndex = num_indices;

            num_vertices += aiMesh.mNumVertices();   //vertices
            num_indices += this.m_Meshes.get(i).numIndices; //indices

            //resize
            if(aiMesh.mNumBones() > 0){
                this.m_Bones.clear();
                for(int idx = 0; idx < num_vertices; idx++){
                    this.m_Bones.add(new VertexBoneData());
                }
            }
        }
    }

    private void _initAllMeshes(){

        PointerBuffer meshesPBuffer = aiScene.mMeshes();
        for(int i = 0; i < this.m_Meshes.size(); i++){

            assert meshesPBuffer != null;
            AIMesh aiMesh = AIMesh.create(meshesPBuffer.get(i));
            _initSingleMesh(aiMesh, i);
        }
    }

    private void _initSingleMesh(AIMesh aiMesh, int meshIndex){

        //populate vertex attributes
        for(int i = 0; i < aiMesh.mNumVertices(); i++){

            AIVector3D pos = aiMesh.mVertices().get(i);
            this.m_Positions.add(new Vector3f(pos.x(), pos.y(), pos.z()));  //can make this better by just making the mPositions a float array(list?).

            AIVector3D normal = aiMesh.mNormals().get(i);
            this.m_Normals.add(new Vector3f(normal.x(), normal.y(), normal.z()));

            AIVector3D tc = aiMesh.mTextureCoords(0).get(i);
            this.m_TexCords.add(new Vector2f(tc.x(), tc.y()));
        }

        //populate index buffer
        for(int idxBuff = 0; idxBuff < aiMesh.mNumFaces(); idxBuff++){

            AIFace aiFace = aiMesh.mFaces().get(idxBuff);
            if(aiFace.mNumIndices() == 3) {
                this.m_Indices.add(aiFace.mIndices().get(0));
                this.m_Indices.add(aiFace.mIndices().get(1));
                this.m_Indices.add(aiFace.mIndices().get(2));
            }
            else
                System.out.println("wtf is this face, son?");
        }

        //load bones
        if(aiMesh.mNumBones() > 0) {
            _loadMeshBones(aiMesh, meshIndex);
        }
    }

    private void _loadMeshBones(AIMesh aiMesh, int meshIndex){

        PointerBuffer aiBonesPointerBuffer = aiMesh.mBones();
        for(int i = 0; i < aiMesh.mNumBones(); i++){

            AIBone aiBone = AIBone.create(aiBonesPointerBuffer.get(i));
            _loadSingleBone(aiBone, meshIndex);
        }
    }

    private void _loadSingleBone(AIBone aiBone, int meshIndex){

        int bone_id = _getBoneId(aiBone);

        if(bone_id == this.m_BoneInfo.size()){
            //new bone
            Matrix4f mat = AssetPool.aiMat4ToJomlMat4(aiBone.mOffsetMatrix());
            BoneInfo boneInfo = new BoneInfo(mat);
            this.m_BoneInfo.add(boneInfo);
        }

        for(int i = 0; i < aiBone.mNumWeights(); i++){

            AIVertexWeight vertex_weight = aiBone.mWeights().get(i);
            int global_vertex_id = this.m_Meshes.get(meshIndex).baseVertex + aiBone.mWeights().get(i).mVertexId();
            this.m_Bones.get(global_vertex_id).addBoneData(bone_id, vertex_weight.mWeight());

            //System.out.println("bone_id: "+bone_id+".  global_vertex_id: "+global_vertex_id+".  vertex_weight: "+vertex_weight.mWeight());
        }
    }

    private int _getBoneId(AIBone aiBone){

        int boneId = 0;
        String boneName = StandardCharsets.UTF_8.decode( aiBone.mName().data() ).toString();

        if(!boneNameToIndexMap.containsKey(boneName)){
            //allocate index for a new bone
            boneId = boneNameToIndexMap.size();
            boneNameToIndexMap.put(boneName, boneId);
        }
        else {
            boneId = boneNameToIndexMap.get(boneName);
        }

        return boneId;
    }

    public void getBoneTransforms(ArrayList<Matrix4f> transforms){

        //resize
        transforms.clear();
        for(int i = 0; i < this.m_BoneInfo.size(); i++){
            transforms.add(new Matrix4f());
        }

        Matrix4f identity = new Matrix4f().identity();
        _readNodeHierarchy(aiScene.mRootNode(), identity);

        for(int i = 0; i < this.m_BoneInfo.size(); i++){
            transforms.set(i, this.m_BoneInfo.get(i).finalTransformation);
        }
    }

    private void _readNodeHierarchy(AINode aiNode, Matrix4f parentTransform){

        String nodeName = StandardCharsets.UTF_8.decode(aiNode.mName().data()).toString();
        Matrix4f nodeTransformation = AssetPool.aiMat4ToJomlMat4(aiNode.mTransformation());
        //System.out.println("nodeTransformation:  "+nodeTransformation);

        Matrix4f globalTransformation = AssetPool.jomlMat4Mul(parentTransform, nodeTransformation);//       parentTransform.mul(nodeTransformation);
        if(boneNameToIndexMap.containsKey(nodeName)){

            int boneIndex = boneNameToIndexMap.get(nodeName);
            this.m_BoneInfo.get(boneIndex).finalTransformation = AssetPool.jomlMat4Mul(globalTransformation, this.m_BoneInfo.get(boneIndex).offsetMatrix);//    globalTransformation.mul(this.m_BoneInfo.get(boneIndex).offsetMatrix);
        }
        //else throw new RuntimeException("Error. Bone not found!");

        PointerBuffer aiNodePBuffer = aiNode.mChildren();
        for(int i = 0; i < aiNode.mNumChildren(); i++){

            assert aiNodePBuffer != null;
            AINode childNode = AINode.create(aiNodePBuffer.get(i));
            _readNodeHierarchy(childNode, globalTransformation);
        }
    }

    private void _populateBuffers(){

        //Positions buffer -> layout(location = 0)
        glBindBuffer(GL_ARRAY_BUFFER, this.attributeBuffers[BUFFER_TYPE.POSITIONS.ordinal()]);
        glBufferData(GL_ARRAY_BUFFER, AssetPool.toFloatArr(this.m_Positions), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        //Texture coordinates buffer -> layout(location = 1)
        glBindBuffer(GL_ARRAY_BUFFER, this.attributeBuffers[BUFFER_TYPE.TEX_CORDS.ordinal()]);
        glBufferData(GL_ARRAY_BUFFER, AssetPool.toFloatArrTx(this.m_TexCords), GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        //Normals buffer -> layout(location = 2)
        glBindBuffer(GL_ARRAY_BUFFER, this.attributeBuffers[BUFFER_TYPE.NORMALS.ordinal()]);
        glBufferData(GL_ARRAY_BUFFER, AssetPool.toFloatArr(this.m_Normals), GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        //Bone ids buffer -> layout(location = 3)
        glBindBuffer(GL_ARRAY_BUFFER, this.attributeBuffers[BUFFER_TYPE.BONE_IDS.ordinal()]);
        glBufferData(GL_ARRAY_BUFFER, AssetPool.toFloatArrBoneId(this.m_Bones), GL_STATIC_DRAW);
        glVertexAttribPointer(3, VertexBoneData.MAX_BONES_PER_VERTEX, GL_INT, false, VertexBoneData.MAX_BONES_PER_VERTEX * Integer.BYTES, 0);

        //Bone weights buffer -> layout(location = 4)
        glBindBuffer(GL_ARRAY_BUFFER, this.attributeBuffers[BUFFER_TYPE.BONE_WEIGHTS.ordinal()]);
        glBufferData(GL_ARRAY_BUFFER, AssetPool.toFloatArrBoneWeights(this.m_Bones), GL_STATIC_DRAW);
        glVertexAttribPointer(4, VertexBoneData.MAX_BONES_PER_VERTEX, GL_FLOAT, false, VertexBoneData.MAX_BONES_PER_VERTEX * Float.BYTES, 0);

        //Indices buffer -> No layout location
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.attributeBuffers[BUFFER_TYPE.INDICES.ordinal()]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, AssetPool.toIntArr(this.m_Indices), GL_STATIC_DRAW);
    }


    public void render(){

        glBindVertexArray(this.vao);

        for(int i = 0; i < this.attributeBuffers.length - 1; i++)   // minus 1 for the indices buffer!
            glEnableVertexAttribArray(i);

        for (BasicMeshEntry mesh : this.m_Meshes)
            glDrawElementsBaseVertex(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_INT, (long) Integer.BYTES * mesh.baseIndex, mesh.baseVertex);

        for(int i = 0; i < this.attributeBuffers.length - 1; i++)
            glDisableVertexAttribArray(i);

        glBindVertexArray(0);
    }

    public void dispose(){

        glDeleteVertexArrays(this.vao);
        for (int attributeBuffer : this.attributeBuffers)
            glDeleteBuffers(attributeBuffer);
    }
}
