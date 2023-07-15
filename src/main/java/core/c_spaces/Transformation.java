package core.c_spaces;

import core.models.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {
    private Matrix4f projectionMatrix;
    private Matrix4f modelViewMatrix;
    private Matrix4f viewMatrix;

    public Transformation(){
        this.modelViewMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar){
        float aspectRatio = width / height;
        this.projectionMatrix.identity();
        this.projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return this.projectionMatrix;
    }

    public Matrix4f getModelViewMatrix(GameObject obj, Matrix4f viewMatrix){
        Vector3f rotation = obj.getRotation();
        modelViewMatrix.identity().translate(obj.getPosition())
                .rotateX((float)Math.toRadians(-rotation.x))
                .rotateY((float)Math.toRadians(-rotation.y))
                .rotateZ((float)Math.toRadians(-rotation.z))
                .scale(obj.getScale());

        Matrix4f viewCurr = new Matrix4f(viewMatrix);
        return viewCurr.mul(modelViewMatrix);
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();
        viewMatrix.identity();

        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));

        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

}
