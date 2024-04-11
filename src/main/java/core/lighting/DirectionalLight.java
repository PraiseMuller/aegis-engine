package core.lighting;

import core.entities.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class DirectionalLight extends GameObject {
    private Vector3f direction;
    private float intensity;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity){
        super(new Vector3f(direction.mul(50.0f)), color, 500.0f);

        this.direction = direction;
        this.intensity =  intensity;
    }

    public DirectionalLight(DirectionalLight d){
        this(d.getMesh().getMaterial().getColor(), d.direction, d.intensity);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getColor() {
        return this.getMesh().getMaterial().getColor();
    }

    public void setColor(Vector3f color) {
        this.getMesh().getMaterial().setColor(color);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
