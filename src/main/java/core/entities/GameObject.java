package core.entities;

import core.engine.Scene;
import org.joml.Vector3f;

public class GameObject {

    protected final Vector3f position;
    protected final Vector3f rotation;
    protected final Vector3f scale;
    private Mesh mesh;

    public GameObject(){
        this.position = null;
        this.rotation = null;
        this.scale = null;
        this.mesh = null;
        throw new RuntimeException("What the fuck?");
    }

    public GameObject(Vector3f position, Vector3f color){
        this.position = position;
        this.rotation = new Vector3f();
        this.scale = new Vector3f(2.0f);
        this.mesh = new Mesh("assets/models/default_cube.obj", color);
    }

    public GameObject(Vector3f position, Vector3f rotation, Vector3f scale, String modelFileLocation){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.mesh = new Mesh(modelFileLocation);
    }

    public void update(float dt){
        //Zthis.rotation.y += 15f * dt;
    }

    public void render(Scene scene){
        this.mesh.render(scene, this);
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
    public Mesh getMesh(){ return this.mesh; }
    public void dispose(){
        this.mesh.dispose();
    }
}
