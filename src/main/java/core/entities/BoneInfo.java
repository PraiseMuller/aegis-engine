package core.entities;

import org.joml.Matrix4f;

public class BoneInfo {

    protected Matrix4f offsetMatrix;
    protected Matrix4f finalTransformation;

    public BoneInfo(Matrix4f offsetMatrix){

        this.offsetMatrix = new Matrix4f().set(offsetMatrix);
        this.finalTransformation = new Matrix4f().zero();
    }
}
