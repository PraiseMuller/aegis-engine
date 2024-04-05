package core.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {
    public final Vector3f position;
    public final float size;
    private final Matrix4f modelMatrix;

    public Transform(Vector3f pos, float size){
        this.position = pos;
        this.size = size;
        this.modelMatrix = new Matrix4f();
    }

    public void recalculate(){
        this.modelMatrix.identity();
        this.modelMatrix.translate(position);
        this.modelMatrix.scale(size);
    }

    public Matrix4f getModelMatrix(){
        recalculate();
        return this.modelMatrix;
    }
}
