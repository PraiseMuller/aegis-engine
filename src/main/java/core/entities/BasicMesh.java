package core.entities;

import core.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.ArrayList;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glDrawElementsBaseVertex;

public class BasicMesh {

    private int vao;
    private final int[] attributeBuffers  = new int[4];
    private final ArrayList<BasicMeshEntry> basicMeshEntries;
    private final ArrayList<Vector3f> positions;
    private final ArrayList<Vector3f> normals;
    private final ArrayList<Vector2f> texCords;
    private final ArrayList<Integer> indices;

    public BasicMesh(String modelFileLocation){

        this.basicMeshEntries = new ArrayList<>();
        this.positions = new ArrayList<>();
        this.normals = new ArrayList<>();
        this.texCords = new ArrayList<>();
        this.indices = new ArrayList<>();

        _loadMesh(modelFileLocation);
    }

    private void _loadMesh(String fileLocation){

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        for(int i = 0; i < this.attributeBuffers.length; i++)
            this.attributeBuffers[i] = glGenBuffers();

        AIScene aiScene = aiImportFile(fileLocation, aiProcess_Triangulate | aiProcess_GenSmoothNormals | aiProcess_FlipUVs | aiProcess_JoinIdenticalVertices);
        assert aiScene != null;
        _countVerticesAndIndices(aiScene);
        _initAllMeshes(aiScene);
        _populateBuffers();

        glBindVertexArray(0);
    }

    private void _countVerticesAndIndices(AIScene aiScene){

        int numVertices = 0, numIndices = 0;
        PointerBuffer pointerBuffer = aiScene.mMeshes();
        for(int i = 0; i < pointerBuffer.limit(); i++){

            AIMesh aiMesh = AIMesh.create(pointerBuffer.get(i));

            BasicMeshEntry meshEntry = new BasicMeshEntry();
            meshEntry.materialIndex = aiMesh.mMaterialIndex();
            meshEntry.numIndices = aiMesh.mNumFaces() * 3;
            meshEntry.baseVertex = numVertices;
            meshEntry.baseIndex = numIndices;

            basicMeshEntries.add(meshEntry);

            numVertices += aiMesh.mNumVertices();
            numIndices += basicMeshEntries.get(i).numIndices;
        }
    }

    private void _initAllMeshes(AIScene aiScene){

        PointerBuffer pointerBuffer = aiScene.mMeshes();
        for(int i = 0; i < pointerBuffer.limit(); i++) {
            AIMesh aiMesh = AIMesh.create(pointerBuffer.get(i));
            _initSingleMesh(aiMesh, i);
        }
    }

    private void _initSingleMesh(AIMesh aiMesh, int meshIndex){

        //populate vertex attributes
        for(int i = 0; i < aiMesh.mVertices().limit(); i++){
            Vector3f pos = new Vector3f(aiMesh.mVertices().get(i).x(), aiMesh.mVertices().get(i).y(), aiMesh.mVertices().get(i).z());
            Vector3f normal = new Vector3f(aiMesh.mNormals().get(i).x(), aiMesh.mNormals().get(i).y(), aiMesh.mNormals().get(i).z());
            Vector2f texCords =  new Vector2f(aiMesh.mTextureCoords(0).get(i).x(), aiMesh.mTextureCoords(0).get(i).y());

            this.positions.add(pos);
            this.normals.add(normal);
            this.texCords.add(texCords);
        }

        //populate index buffer
        for(int i = 0; i < aiMesh.mNumFaces(); i++){
            AIFace aiFace = aiMesh.mFaces().get(i);
            if(aiFace.mNumIndices() == 3) {
                this.indices.add(aiFace.mIndices().get(0));
                this.indices.add(aiFace.mIndices().get(1));
                this.indices.add(aiFace.mIndices().get(2));
            }
        }
    }

    private void _populateBuffers(){

        glBindBuffer(GL_ARRAY_BUFFER, this.attributeBuffers[0]);
        glBufferData(GL_ARRAY_BUFFER, AssetPool.toFloatArr(this.positions), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, this.attributeBuffers[1]);
        glBufferData(GL_ARRAY_BUFFER, AssetPool.toFloatArrTx(this.texCords), GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, this.attributeBuffers[2]);
        glBufferData(GL_ARRAY_BUFFER, AssetPool.toFloatArr(this.normals), GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.attributeBuffers[3]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, AssetPool.toIntArr(this.indices), GL_STATIC_DRAW);
    }

    public void render(){

        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        for (BasicMeshEntry mesh : this.basicMeshEntries) {
            glDrawElementsBaseVertex(GL_TRIANGLES, mesh.numIndices, GL_UNSIGNED_INT, (long) Integer.BYTES * mesh.baseIndex, mesh.baseVertex);
        }

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    public void dispose(){
        glDeleteVertexArrays(this.vao);
        for(int i = 0; i < 4; i++)
            glDeleteBuffers(this.attributeBuffers[i]);
    }
}
