package core.entities;

import org.joml.Vector3f;

public class GameObject {

    protected final Vector3f position;
    protected final Vector3f rotation;
    protected final Vector3f scale;
    private final BasicMesh mesh;
    private final Material material;

    public GameObject(){
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
        this.mesh = new BasicMesh("assets/models/default_cube.obj");
        this.material = new Material();
    }

    public GameObject(Vector3f position, Vector3f color, float scale){
        this.position = position;
        this.rotation = new Vector3f();
        this.scale = new Vector3f(scale);
        this.mesh = new BasicMesh("assets/models/ico_sphere.fbx");
        this.material = new Material(color);
    }

    public GameObject(Vector3f position, Vector3f rotation, Vector3f scale, String modelFileLocation){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.mesh = new BasicMesh(modelFileLocation);
        this.material = new Material();
    }

    public void update(float dt){
//        this.rotation.z += 15f * dt;
    }

    public void render(){
        this.mesh.render();
    }

    public void setPosition(Vector3f pos){
        this.position.x = pos.x;
        this.position.y = pos.y;
        this.position.z = pos.z;
    }

    public Vector3f getPosition(){
        return this.position;
    }
    public Vector3f getRotation(){
        return this.rotation;
    }
    public Vector3f getScale(){
        return this.scale;
    }
    public void setScale(float x, float y, float z) {
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
    }
    public Material getMaterial(){
        return this.material;
    }

    public BasicMesh getMesh() {
        return mesh;
    }

    public void dispose(){
        this.mesh.dispose();
        this.material.dispose();
    }
}
