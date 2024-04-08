package core.entities;

import core.engine.Scene;
import core.utils.Transform;
import org.joml.Vector3f;

public class Player {
    private final GameObject gameObject;
    private final Mesh mesh;

    public Player(Vector3f position){
        this.gameObject = new GameObject(position, new Vector3f(90,0,0), new Vector3f(0.05f));
        this.mesh = new Mesh(this.gameObject);
    }

    public void addPos(Vector3f value){
        this.gameObject.position.add(value);
    }

    public void update(float dt){
        //this.gameObject.rotation.x += dt * 25.0f;
        this.gameObject.rotation.z += dt * 25.0f;
        //this.gameObject.rotation.z += dt * 25.0f;
    }
    public void follow(float x, float y) {
        Vector3f dist = new Vector3f(x - this.gameObject.position.x, y - this.gameObject.position.y, this.gameObject.position.z);
        dist.normalize();

        this.gameObject.position.sub(dist);

//        float worldX = c.x * Camera.getPosition().x / WIN_WIDTH;
//        float worldY = c.y * Camera.getPosition().y / WIN_HEIGHT;
//        float worldZ = c.z;
    }
    public void render(Scene scene){
        this.mesh.draw(scene);
    }
    public Vector3f getPosition(){
        return this.gameObject.position;
    }
    public float getMovePower(){
        return 10.0f;
    }
    public void dispose(){
        this.mesh.dispose();
    }
}

