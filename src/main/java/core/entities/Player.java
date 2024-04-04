package core.entities;

import core.utils.Transform;
import org.joml.Vector3f;

public class Player {
    private final float MOVE_SPEED = 300.0f;
    private final Transform pTransform;
    private final Mesh mesh;

    public Player(Vector3f position){
        this.pTransform = new Transform(position, 20.0f);
        this.mesh = new Mesh();
    }

    public void addPos(Vector3f value){
        this.pTransform.position.add(value);
        this.mesh.update(this.pTransform.getModelMatrix());
    }

    public void follow(float x, float y) {

        Vector3f dist = new Vector3f(x - this.pTransform.position.x, y - this.pTransform.position.y, this.pTransform.position.z);
        dist.normalize();

        this.pTransform.position.sub(dist);

//        float worldX = c.x * Camera.getPosition().x / WIN_WIDTH;
//        float worldY = c.y * Camera.getPosition().y / WIN_HEIGHT;
//        float worldZ = c.z;
    }

    public void render(){
        this.mesh.draw();
    }
    public void dispose(){
        this.mesh.dispose();
    }
    public Vector3f getPosition(){
        return this.pTransform.position;
    }
    public float getMovePower(){
        return this.MOVE_SPEED;
    }
}
