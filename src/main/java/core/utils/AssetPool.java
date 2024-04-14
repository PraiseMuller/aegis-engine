package core.utils;

import core.entities.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
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

        GameObject gt3_rs = new GameObject(GAME_OBJ_INIT_POSITION, new Vector3f(0,0,0), new Vector3f(25f), "assets/models/GT3 RS/GT3 RS.obj");

//        GameObject mon_infinian = new GameObject(GAME_OBJ_INIT_POSITION, new Vector3f(90,0,0), new Vector3f(0.07f), "D:\\Models\\Infinian lineage series\\source\\Mon_Infinian_001_Skeleton.FBX");
//        GameObject gwyn_ = new GameObject(new Vector3f(-30,0,0), new Vector3f(0,0,0), new Vector3f(14.0f), "assets/models/gwyn.obj");
//        GameObject tarisland_dragon = new GameObject(new Vector3f(100,0,10), new Vector3f(90,0,80), new Vector3f(0.09f), "D:\\Models\\Tarisland dragon high poly\\source\\M_B_44_Qishilong_skin_Skeleton.FBX");
//        GameObject dragonkin_mir = new GameObject(new Vector3f(-30,0,90), new Vector3f(90,0,120), new Vector3f(0.1f), "D:\\Models\\Silver dragonkin Mir\\source\\Mon_BlackDragon31_Skeleton.FBX");
        GameObject _floor = new GameObject(new Vector3f(0,-30,0), new Vector3f(0,0,0), new Vector3f(5000, 1, 5000), "assets/models/default_cube.obj");
        _floor.getMaterial().setColor(0.3f,0.3f,0.3f);
        _floor.getMaterial().setRoughnessVal(0.4f);
        _floor.getMaterial().setMetallicVal(0.0f);

        gameObjects.add(gt3_rs);
//        gameObjects.add(mon_infinian);
//        gameObjects.add(gwyn_);
//        gameObjects.add(tarisland_dragon);
//        gameObjects.add(dragonkin_mir);
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
