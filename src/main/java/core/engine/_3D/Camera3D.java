package core.engine._3D;

import core.engine.Camera;
import core.entities.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera3D extends Camera {

    public Camera3D(float fov, float width, float height, float zNear, float zFar){
        this.view = new Matrix4f();
        this.model = new Matrix4f();
        this.projection = new Matrix4f();
        this.projection.identity();
        this.projection.perspective(fov,width / height, zNear, zFar);
    }

    @Override
    public void updateProjection(float fov, float width, float height, float zNear, float zFar){
        this.projection.identity();
        this.projection.perspective(fov,width / height, zNear, zFar);
    }

    @Override
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }

        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    @Override
    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;

        float maxAngle = (float) Math.toDegrees(90);
        if(rotation.x > maxAngle) rotation.x = maxAngle;
        if(rotation.x < -maxAngle) rotation.x = -maxAngle;
    }

    @Override
    public Matrix4f modelMatrix(GameObject obj){
        this.model.identity();

        this.model.identity().translate(obj.position)
                .rotateX((float)Math.toRadians(-obj.rotation.x))
                .rotateY((float)Math.toRadians(-obj.rotation.y))
                .rotateZ((float)Math.toRadians(-obj.rotation.z))
                .scale(obj.scale);

        return this.model;  // this.view.mul(this.model);
    }

    @Override
    public Matrix4f viewMatrix() {
        this.view.identity();

        // First do the rotation so camera rotates over its position
        view.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));

        // Then do the translation
        view.translate(-position.x, -position.y, -position.z);
        return view;
    }
}
