package core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<String> readAllLines(String location){
        File file = new File(location);
        StringBuilder builder;
        List<String> temp = new ArrayList<>();

        try (BufferedReader buffer = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String str;
            while ((str = buffer.readLine()) != null) {
                builder  = new StringBuilder();
                builder.append(str).append("\n");
                temp.add(builder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }
}
