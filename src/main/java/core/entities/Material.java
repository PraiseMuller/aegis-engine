package core.entities;

import org.joml.Vector3f;

public class Material {
    private final Vector3f color;
    private float metallicVal, roughnessVal;

    public Material(){
        this.metallicVal = 0.8f;
        this.roughnessVal = 0.3f;
        this.color = new Vector3f(0.3f, 0.5f, 0.9f);
    }

    public Material(Vector3f color){
        this.metallicVal = 0.0f;
        this.roughnessVal = 0.4f;
        this.color = color;
    }

    public void setColor(Vector3f color){
        this.color.x = color.x;
        this.color.y = color.y;
        this.color.z = color.z;
    }

    public void setColor(float x, float y, float z){
        this.color.x = x;
        this.color.y = y;
        this.color.z = z;
    }

    public Vector3f getColor() {
        return color;
    }
    public float getMetallicVal(){
        return metallicVal;
    }
    public void setMetallicVal(float metallicVal){
        this.metallicVal = metallicVal;
    }
    public float getRoughnessVal(){
        return roughnessVal;
    }
    public void setRoughnessVal(float roughnessVal){
        this.roughnessVal = roughnessVal;
    }
    public void dispose(){
        //dispose of textures and stuff;
    }
}
