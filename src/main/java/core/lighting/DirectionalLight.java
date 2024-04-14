package core.lighting;

import core.entities.GameObject;
import org.joml.Vector3f;

public class DirectionalLight extends GameObject {
    private Vector3f direction;
    private float intensity;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity){
        super(new Vector3f(direction.x * 500, direction.y * 500, direction.z * 500), color, 50.0f);

        this.direction = direction.normalize();
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
