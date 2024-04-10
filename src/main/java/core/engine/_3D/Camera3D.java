package core.engine._3D;

import core.engine.Camera;
import core.entities.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera3D extends Camera {

    public Camera3D(float fov, float width, float height, float zNear, float zFar){
        super();
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
            this.position.x += (float)Math.sin(Math.toRadians(this.rotation.y)) * -1.0f * offsetZ;
            this.position.z += (float)Math.cos(Math.toRadians(this.rotation.y)) * offsetZ;
        }

        if ( offsetX != 0) {
            this.position.x += (float)Math.sin(Math.toRadians(this.rotation.y - 90)) * -1.0f * offsetX;
            this.position.z += (float)Math.cos(Math.toRadians(this.rotation.y - 90)) * offsetX;
        }
        this.position.y += offsetY;
    }

    @Override
    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        this.rotation.x += offsetX;
        this.rotation.y += offsetY;
        this.rotation.z += offsetZ;

        float maxAngle = (float) Math.toDegrees(90);
        if(this.rotation.x > maxAngle) this.rotation.x = maxAngle;
        if(this.rotation.x < -maxAngle) this.rotation.x = -maxAngle;
    }

    @Override
    public Matrix4f modelMatrix(GameObject obj){
        this.model.identity();
        this.model.translate(obj.getPosition())
                   .rotateX((float)Math.toRadians(-obj.getRotation().x))
                   .rotateY((float)Math.toRadians(-obj.getRotation().y))
                   .rotateZ((float)Math.toRadians(-obj.getRotation().z))
                   .scale(obj.getScale());

        return this.model;  // this.view.mul(this.model);
    }

    @Override
    public Matrix4f viewMatrix() {
        this.view.identity();

        // First do the rotation so camera rotates over its position
        this.view.rotate((float)Math.toRadians(this.rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(this.rotation.y), new Vector3f(0, 1, 0));

        // Then do the translation
        this.view.translate(-this.position.x, -this.position.y, -this.position.z);
        return this.view;
    }
}
