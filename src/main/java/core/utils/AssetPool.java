package core.utils;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.stb.STBImage.stbi_load;

public class AssetPool {
    private static Map<String, String> shaders = new HashMap<>();

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

    // lerp(a,b,t) = a + (b - a) * t
    public static Vector3f lerp(Vector3f a, Vector3f b, float t){
        //ease in and ease out
        //t = Math.abs(t - Math.min(t, 1 - (float) Math.exp(-0.000001f * Math.pow(t - 1, 3))));
        //ease out
        t = Math.max(1 - (float) Math.exp(-1f * Math.pow(t - 1, 3)), 0);
        return new Vector3f().add(b.sub(a)).mul(t).add(a);
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
}
