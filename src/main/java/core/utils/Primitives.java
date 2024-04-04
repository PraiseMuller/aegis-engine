package core.utils;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class Primitives {

    private Primitives(){}
    public static Map<FloatBuffer, IntBuffer> rectV(){
        float[] fArr = new float[]{
                 1.0f, 1.0f,    1.0f, 1.0f,
                -1.0f, 1.0f,    0.0f, 1.0f,
                -1.0f,-1.0f,    0.0f, 0.0f,
                 1.0f,-1.0f,    1.0f, 0.0f,
        };
        FloatBuffer fb = MemoryUtil.memAllocFloat(fArr.length);
        fb.put(fArr).flip();

        int[] iArr = new int[]{
                0, 3, 2,
                0, 2, 1
        };
        IntBuffer ib = MemoryUtil.memAllocInt(iArr.length);
        ib.put(iArr).flip();

        Map<FloatBuffer, IntBuffer> vertInf = new HashMap<>();
        vertInf.put(fb, ib);

        return vertInf;
    }




}
