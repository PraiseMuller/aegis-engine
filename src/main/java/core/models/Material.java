package core.models;

import org.joml.Vector4f;

public class Material {
    private Texture texture;
    private float reflectance;
    private float specularPower;
    private Vector4f color;

    public Material(Texture texture, float reflectance){
        this.texture = texture;
        this.reflectance = reflectance;
        this.color = new Vector4f(0.2f, 0.3f, 0.5f, 1.0f);
        this.specularPower = 32f;
    }

    public void bindTexture(){
        this.texture.bind();
    }
    public void unbindTexture(){
        this.texture.unbind();
    }
    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    public float getReflectance() {
        return this.reflectance;
    }
    public void setColor(Vector4f color){
        this.color = color;
    }
    public Vector4f getColor(){
        return this.color;
    }

    public float getSpecularPower() {
        return this.specularPower;
    }
    public void setSpecularPower(float power) {
        this.specularPower = power;
    }
}
