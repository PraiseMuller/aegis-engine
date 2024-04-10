package core.lighting;

import core.entities.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PointLight extends GameObject {
    private float intensity;

    public PointLight(Vector3f color, Vector3f position, float intensity){
        super(position, color);
        this.intensity = intensity;
    }

    public PointLight(PointLight pointLight){
        super(pointLight.getPosition(), pointLight.getMesh().getMaterial().getColor());
        this.intensity = pointLight.intensity;
    }

    public PointLight toViewSpace(Matrix4f viewMatrix){
        PointLight pointLight = new PointLight(this);
        Vector4f pos = new Vector4f(pointLight.getPosition(), 0);
        pos.mul(viewMatrix);
        pointLight.setPosition(new Vector3f(pos.x, pos.y, pos.z));

        return pointLight;
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
        return intensity;
    }
}
