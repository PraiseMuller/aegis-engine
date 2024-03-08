package core.engine;

import org.joml.*;
import org.joml.Math;

public class Particle {
    private final float size;
    private final Vector2f position;
    private final Vector4f color;
    private final float vx, vy;
    private float lifetime = 100.0f;

    public Particle(Vector2f pos, float size){
        this.size = size;
        this.position = pos;
        this.color = new Vector4f(0.9f, 0.75f, 0.2f, (float) Math.random());
        this.vx = (float) (Math.random() - 0.5f) * 50.0f;
        this.vy = (float) (Math.random() - 0.5f) * 50.0f;
    }

    public Vector4f getColor(){
        //return new Vector4f(0.2f, 0.15f, 0.3f,  1);
        //return new Vector4f(0.0f, 9.0f, 0.3f, 1.0f);
        //return new Vector4f((float) Math.random(), (float) Math.random(),(float) Math.random(), 1.0f);
        return this.color;
    }

    public Vector2f getPosition(){
        return this.position;
    }

    public void update(float dt){
        this.position.add(vx * dt, vy * dt);
        this.lifetime -= 0.5f;
    }

    public float getSize(){
        return this.size;
    }
    public float getLifetime(){
        return this.lifetime;
    }
}
