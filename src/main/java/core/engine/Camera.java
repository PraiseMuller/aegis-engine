package core.engine;

import core.entities.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static core.utils.SETTINGS.CAMERA_INIT_POS;
import static core.utils.SETTINGS.CAMERA_INIT_ROT;

public abstract class Camera {
    protected Matrix4f projection = null;
    protected Matrix4f view = null;
    protected Matrix4f model = null;
    protected final Vector3f position = new Vector3f(CAMERA_INIT_POS);
    protected final Vector3f rotation = new Vector3f(CAMERA_INIT_ROT);;


    //CONSTR
    public Camera(){}


    //METHODS
    public void movePosition(float offsetX, float offsetY, float offsetZ){}
    public void moveRotation(float offsetX, float offsetY, float offsetZ){}
    public void updateProjection(float fov, float width, float height, float zNear, float zFar){}

    public Matrix4f projectionMatrix(){
        return this.projection;
    }
    public Matrix4f viewMatrix(){
        return this.view;
    }
    public Matrix4f modelMatrix(GameObject obj){
        return this.model;
    }

    public void smoothFollow(Vector3f position) {
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f pos) {
        this.position.x = pos.x;
        this.position.y = pos.y;
        this.position.z = pos.z;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector3f rot) {
        this.rotation.x = rot.x;
        this.rotation.y = rot.y;
        this.rotation.z = rot.z;
    }

    // lerp(a,b,t) = a + (b - a) * t
    public static Vector3f lerp(Vector3f a, Vector3f b, float t){
        //ease in and ease out
        t = Math.abs(t - Math.min(t, 1 - (float) Math.exp(-0.000001f * Math.pow(t - 1, 3))));
        //ease out
        //t = Math.max(1 - (float) Math.exp(-1f * Math.pow(t - 1, 3)), 0);
        return new Vector3f().add(b.sub(a)).mul(t).add(a);
    }
}

