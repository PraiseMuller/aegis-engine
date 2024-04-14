package core.lighting;

import core.entities.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DirectionalLight extends GameObject {
    private Vector3f direction;
    private float intensity;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity){
        super(direction.mul(500), color, 50.0f);

        this.direction = direction;
        this.intensity =  intensity;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getColor() {
        return this.getMaterial().getColor();
    }

    public void setColor(Vector3f color) {
        this.getMaterial().setColor(color);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
