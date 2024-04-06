package core.lighting;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DirectionalLight {
    private Vector3f direction;
    private Vector3f color;
    private float intensity;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity){
        this.color = color;
        this.direction = direction;
        this.intensity =  intensity;
    }

    public DirectionalLight(DirectionalLight d){
        this(d.color, d.direction, d.intensity);
    }

    public DirectionalLight toViewSpace(Matrix4f viewMatrix){
        DirectionalLight dl = new DirectionalLight(this);
        Vector4f dir = new Vector4f(dl.getDirection(), 0);
        dir.mul(viewMatrix);
        dl.setDirection(new Vector3f(dir.x, dir.y, dir.z));

        return dl;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
