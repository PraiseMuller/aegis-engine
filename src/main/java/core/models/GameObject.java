package core.models;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class GameObject {
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;
    private Mesh mesh;
    private Material material;

    public GameObject(Vector3f pos, Vector3f rot, Vector3f scale, Mesh mesh, Material mat){
        this.position = pos;
        this.rotation = rot;
        this.scale = scale;
        this.mesh = mesh;
        this.material = mat;
    }

    public void render(){
        this.mesh.render();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
    public boolean hasTexture(){
        return this.material.getTexture() != null;
    }
    public void bindMaterials(){
        this.material.bindTexture();
    }
    public void unbindMaterials(){
        this.material.unbindTexture();
    }

    public void cleanup(){
        this.mesh.cleanup();
    }

    public Vector4f getColor() {
        return this.material.getColor();
    }
    public void setColor(Vector4f color) {
        this.material.setColor(color);
    }
}
