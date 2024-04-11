package core.lighting;

import core.entities.GameObject;
import org.joml.Vector3f;

public class PointLight extends GameObject {
    private float intensity;

    public PointLight(Vector3f color, Vector3f position, float intensity){
        super(position, color, 1.0f);
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return this.getMesh().getMaterial().getColor();
    }
    public void setColor(Vector3f color) {
        this.getMesh().getMaterial().setColor(color);
    }
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    public float getIntensity() {
        return this.intensity;
    }
}
