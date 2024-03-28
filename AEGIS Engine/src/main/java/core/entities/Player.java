package core.entities;

import core.engine.Camera;
import core.engine.Window;
import core.utils.MathUtils;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static core.utils.SETTINGS.WIN_HEIGHT;
import static core.utils.SETTINGS.WIN_WIDTH;

public class Player {
    private final float MOVE_SPEED = 300.0f;
    private final Vector3f position;
    private final Vector4f color;
    private float size;
    private final Mesh mesh;

    public Player(Vector3f position){
        this.position = position;
        this.color = new Vector4f(0.9f,0.0f,0.5f,1.0f);
        this.size = 30f;
        this.mesh = new Mesh(this.position, this.color, this.size);
    }

    public void addPos(Vector3f value){
        this.position.add(value);
        this.mesh.update(this.position, this.color, this.size);
    }

    public void follow(float x, float y) {

        Vector3f dist = new Vector3f(x - position.x, y - position.y, position.z);
        dist.normalize();

        this.position.sub(dist);

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
        return this.position;
    }
    public float getMovePower(){
        return this.MOVE_SPEED;
    }
}
