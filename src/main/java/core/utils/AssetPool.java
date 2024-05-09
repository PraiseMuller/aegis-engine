package core.utils;

import core.entities.GameObject;
import core.entities.VertexBoneData;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static core.utils.SETTINGS.GAME_OBJ_INIT_POSITION;
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


    public static void initializeAllEngineStuff(ArrayList<GameObject> gameObjects){

        GameObject gt3_rs = new GameObject(GAME_OBJ_INIT_POSITION, new Vector3f(90,0,0), new Vector3f(15f), "assets/models/Aston Martin One-77.3ds");

//        GameObject tempObj0 = new GameObject(new Vector3f(75,-40,150), new Vector3f(0,30,0), new Vector3f(35f), "assets/models/GT3 RS/GT3 RS.obj");
//        GameObject mon_infinian = new GameObject(GAME_OBJ_INIT_POSITION, new Vector3f(90,0,0), new Vector3f(0.07f), "D:\\Models\\Infinian lineage series\\source\\Mon_Infinian_001_Skeleton.FBX");
//        GameObject gwyn_ = new GameObject(new Vector3f(-30,0,0), new Vector3f(0,0,0), new Vector3f(14.0f), "assets/models/gwyn.obj");
//        GameObject tarisland_dragon = new GameObject(new Vector3f(100,0,10), new Vector3f(90,0,80), new Vector3f(0.09f), "D:\\Models\\Tarisland dragon high poly\\source\\M_B_44_Qishilong_skin_Skeleton.FBX");
//        GameObject dragonkin_mir = new GameObject(new Vector3f(-30,0,90), new Vector3f(90,0,120), new Vector3f(0.1f), "D:\\Models\\Silver dragonkin Mir\\source\\Mon_BlackDragon31_Skeleton.FBX");
//        GameObject _floor = new GameObject(new Vector3f(0,-50,0), new Vector3f(0,0,0), new Vector3f(500, 0.5f, 500), "assets/models/default_cube.obj");
//        _floor.getMaterial().setColor(0.75f,0.6f,0.9f);
//        _floor.getMaterial().setRoughnessVal(0.2f);
//        _floor.getMaterial().setMetallicVal(0.4f);

        gameObjects.add(gt3_rs);
//        gameObjects.add(tempObj0);
//        gameObjects.add(mon_infinian);
//        gameObjects.add(gwyn_);
//        gameObjects.add(tarisland_dragon);
//        gameObjects.add(dragonkin_mir);
//        gameObjects.add(_floor);
    }


    public static float[] toFloatArr(ArrayList<Vector3f> arrayList){
        float[] fArr = new float[arrayList.size() * 3];
        int idx = 0;
        for(int i = 0; i < arrayList.size(); i++){
            fArr[idx] = arrayList.get(i).x;
            fArr[idx + 1] = arrayList.get(i).y;
            fArr[idx + 2] = arrayList.get(i).z;
            idx +=3;
        }
        return fArr;
    }
    public static float[] toFloatArrTx(ArrayList<Vector2f> arrayList){
        float[] fArr = new float[arrayList.size() * 2];
        int idx = 0;
        for(int i = 0; i < arrayList.size(); i++){
            fArr[idx] = arrayList.get(i).x;
            fArr[idx + 1] = arrayList.get(i).y;
            idx += 2;
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

    public static int[] toFloatArrBoneId(ArrayList<VertexBoneData> m_bones) {

        int arr_idx = 0;
        int[] arr = new int[m_bones.size() * VertexBoneData.MAX_BONES_PER_VERTEX];

        for(int i = 0; i < m_bones.size(); i++){

            VertexBoneData v = m_bones.get(i);
            for(int idx = 0; idx < v.boneIds.length; idx++){
                arr[arr_idx] = v.boneIds[idx];
                arr_idx++;
            }
        }

        return arr;
    }

    public static float[] toFloatArrBoneWeights(ArrayList<VertexBoneData> m_bones) {

        int arr_idx = 0;
        float[] arr = new float[m_bones.size() * VertexBoneData.MAX_BONES_PER_VERTEX];

        for(int i = 0; i < m_bones.size(); i++){

            VertexBoneData v = m_bones.get(i);
            for(int idx = 0; idx < v.weights.length; idx++){
                arr[arr_idx] = v.weights[idx];
                arr_idx++;
            }
        }

        return arr;
    }

    public static Matrix4f aiMat4ToJomlMat4(AIMatrix4x4 aiMat){

        Matrix4f mat = new Matrix4f();
        mat.m00(aiMat.a1()).m10(aiMat.b1()).m20(aiMat.c1()).m30(aiMat.d1());
        mat.m01(aiMat.a2()).m11(aiMat.b2()).m21(aiMat.c2()).m31(aiMat.d2());
        mat.m02(aiMat.a3()).m12(aiMat.b3()).m22(aiMat.c3()).m32(aiMat.d3());
        mat.m03(aiMat.a4()).m13(aiMat.b3()).m23(aiMat.c4()).m33(aiMat.d4());

        return mat;
    }

    public static Matrix4f jomlMat4Mul(Matrix4f a, Matrix4f b){

        Matrix4f ans = new Matrix4f().identity();
        ans.mul(a);
        ans.mul(b);
        return ans;
        //return new Matrix4f().identity().mul(a).mul(b);
    }
}
