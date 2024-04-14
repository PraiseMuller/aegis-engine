package core.utils;

import core.entities.GameObject;
import core.lighting.PointLight;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static core.utils.SETTINGS.GAME_OBJ_INIT_POSITION;
import static core.utils.SETTINGS.P_LIGHT_INTENSITY;
import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.stb.STBImage.stbi_load;

public class AssetPool {

    private static final Map<String, String> shaders = new HashMap<>();

    private AssetPool(){}

    public static String getShader(String location){

        if(!AssetPool.shaders.containsKey(location)){
            File file = new File(location);
            StringBuilder builder = new StringBuilder();

            try (BufferedReader buffer = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                String str;
                while ((str = buffer.readLine()) != null) {
                    builder.append(str).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            AssetPool.shaders.put(location, builder.toString());
        }

        return AssetPool.shaders.get(location);
    }

    public static ByteBuffer loadImage(String path){
        ByteBuffer image;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer comp = stack.mallocInt(1);
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            image = stbi_load(path, w, h, comp, 4);
            if (image == null) {
               throw new RuntimeException("Could not load image resources.");
            }
        }
        return image;
    }

    public static float[] loadAiScece(String filePath){

        try (AIScene aiScene = aiImportFile(filePath, aiProcess_Triangulate)) {
            ArrayList<Float> verticesArr = new ArrayList<>();
            ArrayList<Float> texCordsArr = new ArrayList<>();
            ArrayList<Float> normalsArr = new ArrayList<>();

            assert aiScene != null;
            PointerBuffer pointerBuffer = aiScene.mMeshes();

            for(int i = 0; i < Objects.requireNonNull(pointerBuffer).limit(); i++){

                try (AIMesh aiMesh = AIMesh.create(pointerBuffer.get(i))) {
                    AIVector3D.Buffer vertices = aiMesh.mVertices();
                    AIVector3D.Buffer texCords = aiMesh.mTextureCoords(0);
                    AIVector3D.Buffer normals = aiMesh.mNormals();

                    for(int j = 0; j < vertices.limit(); j++) {
                        verticesArr.add(vertices.get(j).x());
                        verticesArr.add(vertices.get(j).y());
                        verticesArr.add(vertices.get(j).z());

                        texCordsArr.add(texCords.get(j).x());
                        texCordsArr.add(texCords.get(j).y());

                        normalsArr.add(normals.get(j).x());
                        normalsArr.add(normals.get(j).y());
                        normalsArr.add(normals.get(j).z());
                    }
                }
                catch (Exception e){
                    throw new RuntimeException(e);
                }
            }

            //COMBINE
            float[] fArr = new float[verticesArr.size() + texCordsArr.size() + normalsArr.size()];
            int vIdx = 0, tIdx = 0, nIdx = 0;
            for(int i = 0; i < fArr.length - 8; i += 8){

                fArr[i]     = verticesArr.get(vIdx++);
                fArr[i + 1] = verticesArr.get(vIdx++);
                fArr[i + 2] = verticesArr.get(vIdx++);

                fArr[i + 3] = texCordsArr.get(tIdx++);
                fArr[i + 4] = texCordsArr.get(tIdx++);

                fArr[i + 5] = normalsArr.get(nIdx++);
                fArr[i + 6] = normalsArr.get(nIdx++);
                fArr[i + 7] = normalsArr.get(nIdx++);
            }

            return fArr;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

//    const aiScene *tree = _importer.ReadFile(path, aiProcess_CalcTangentSpace | aiProcess_Triangulate | aiProcess_JoinIdenticalVertices | aiProcess_SortByPType);
//
//    // iterate over all meshes in this scene
//    for (unsigned int m = 0; m < tree->mNumMeshes; ++m) {
//        const aiMesh *mesh = tree->mMeshes[m];
//            // iterate over all faces in this mesh
//            for (unsigned int j = 0; j < mesh->mNumFaces; ++j) {
//                auto const &face = mesh->mFaces[j];
//                //normally you want just triangles, so iterate over all 3 vertices of the face:
//                for (int k = 0; k < 3; ++k) {
//                    // Now do the magic with 'face.mIndices[k]'
//                    auto const &vertex = mesh->mVertices[face.mIndices[k]];
//                    vertices.push_back(vertex.x);
//                    vertices.push_back(vertex.y);
//                    vertices.push_back(vertex.z);
//
//                    // Same for the normals.
//                    auto const &normal = mesh->mNormals[face.mIndices[k]];
//                    vertices.push_back(normal.x);
//                    vertices.push_back(normal.y);
//                    vertices.push_back(normal.z);
//
//                    // Color of material
//                    // ...
//
//                    // And FINALLY: The UV coordinates!
//                    if(mesh->HasTextureCoords(0)) {
//                        // The following line fixed the issue for me now:
//                        auto const &uv = mesh->mTextureCoords[0][face.mIndices[k]];
//                        vertices.push_back(uv.x);
//                        vertices.push_back(uv.y);
//                    }
//                }
//            }
//        }
//
//        as I can see here
//
//        glDrawElements(GL_TRIANGLES, this->indices.size(), GL_UNSIGNED_INT, 0);
//        you draw all vertexes as triangles. And that is how you load indices from assimp
//
//        for (GLuint i = 0; i < mesh->mNumFaces; i++)
//        {
//            aiFace face = mesh->mFaces[i];
//            for (GLuint j = 0; j < face.mNumIndices; j++)
//                indices.push_back(face.mIndices[j]);
//        }


    public static void initializeAllEngineStuff(ArrayList<GameObject> gameObjects){

//        GameObject gt3_rs = new GameObject(GAME_OBJ_INIT_POSITION, new Vector3f(0,0,0), new Vector3f(20f), "assets/models/GT3 RS/GT3 RS.obj");


        GameObject mon_infinian = new GameObject(GAME_OBJ_INIT_POSITION, new Vector3f(90,0,0), new Vector3f(0.07f), "D:\\Models\\Infinian lineage series\\source\\Mon_Infinian_001_Skeleton.FBX");
        GameObject gwyn_ = new GameObject(new Vector3f(-30,0,0), new Vector3f(0,0,0), new Vector3f(14.0f), "assets/models/gwyn.obj");
        GameObject tarisland_dragon = new GameObject(new Vector3f(100,0,10), new Vector3f(90,0,80), new Vector3f(0.09f), "D:\\Models\\Tarisland dragon high poly\\source\\M_B_44_Qishilong_skin_Skeleton.FBX");
        GameObject dragonkin_mir = new GameObject(new Vector3f(-30,0,90), new Vector3f(90,0,120), new Vector3f(0.1f), "D:\\Models\\Silver dragonkin Mir\\source\\Mon_BlackDragon31_Skeleton.FBX");
        GameObject _floor = new GameObject(new Vector3f(0,-30,0), new Vector3f(0,0,0), new Vector3f(5000, 1, 5000), "assets/models/default_cube.obj");
        _floor.getMaterial().setColor(0.3f,0.3f,0.3f);
        _floor.getMaterial().setRoughnessVal(0.4f);
        _floor.getMaterial().setMetallicVal(0.0f);

//        gameObjects.add(gt3_rs);
        gameObjects.add(mon_infinian);
        gameObjects.add(gwyn_);
        gameObjects.add(tarisland_dragon);
        gameObjects.add(dragonkin_mir);
        gameObjects.add(_floor);
    }


    public static float[] toFloatArr(ArrayList<Vector3f> arrayList){
        float[] fArr = new float[arrayList.size() * 3];
        int idx = 0;
        for(int i = 0; i < fArr.length - 3; i += 3){
            fArr[i] = arrayList.get(idx).x;
            fArr[i + 1] = arrayList.get(idx).y;
            fArr[i + 2] = arrayList.get(idx).z;
            idx++;
        }
        return fArr;
    }
    public static float[] toFloatArrTx(ArrayList<Vector2f> arrayList){
        float[] fArr = new float[arrayList.size() * 2];
        int idx = 0;
        for(int i = 0; i < fArr.length - 2; i += 2){
            fArr[i] = arrayList.get(idx).x;
            fArr[i + 1] = arrayList.get(idx).y;
            idx++;                                              //TODO: Remove idx. Make the function simpler.
        }
        return fArr;
    }
    public static int[] toIntArr(ArrayList<Integer> arrayList){
        int[] fArr = new int[arrayList.size()];
        for(int i = 0; i < fArr.length; i++){
            fArr[i] = arrayList.get(i);
        }
        return fArr;
    }
}
