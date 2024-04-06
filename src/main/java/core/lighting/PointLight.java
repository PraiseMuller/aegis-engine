package core.lighting;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PointLight {
    private Vector3f color;
    private Vector3f position;
    private float intensity;

    public PointLight(Vector3f color, Vector3f position, float intensity){
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public PointLight(PointLight pointLight){
        this.color = pointLight.color;
        this.position = pointLight.position;
        this.intensity = pointLight.intensity;
    }

    public PointLight toViewSpace(Matrix4f viewMatrix){
        PointLight pl = new PointLight(this);
        Vector4f pos = new Vector4f(pl.getPosition(), 0);
        pos.mul(viewMatrix);
        pl.setPosition(new Vector3f(pos.x, pos.y, pos.z));

        return pl;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
