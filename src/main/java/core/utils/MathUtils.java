package core.utils;

import org.joml.Math;
import org.joml.Vector3f;

import java.util.ArrayList;

public class MathUtils {

    private MathUtils(){}

    public static float dist(Vector3f a, Vector3f b){
        return (float) Math.sqrt(a.x * b.x + a.y * b.y + a.z * b.z);
    }
    public static float lerp(float start, float end, float amt) {
        return (1 - amt) * start + amt * end;
    }
    public static float getRandom(float max, float min){
        return (float) Math.random() * (max - min) + min;
    }
    private static float[] toFloatArray(ArrayList<Float> arrayList){
        float[] fArr = new float[arrayList.size()];
        for(int i = 0; i < fArr.length; i++){
            fArr[i] = arrayList.get(i);
        }
        return fArr;
    }
}
