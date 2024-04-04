package core.engine;

import core.utils.MathUtils;
import org.joml.*;
import org.joml.Math;

public class Particle {
    private float size;
    private final Vector2f position;
    private final Vector4f color;
    private final float vx, vy;
    private float lifetime = 500.0f;

    public Particle(Vector2f pos){
        this.size = (float) Math.random() * 100.0f;
        this.position = pos;
        this.color = new Vector4f(MathUtils.getRandom(1.0f,0.0f), MathUtils.getRandom(1.0f,0.0f), MathUtils.getRandom(1.0f,0.0f), MathUtils.getRandom(1.0f, 0.0f));
        this.vx = 0;

        if(this.size > 70.0f)
            this.vy = (float) - (Math.random() - 1.0f) * 200.0f;

        else
            this.vy = (float) - (Math.random() - 1.0f) * 500.0f;

    }

    public Vector4f getColor(){
        return this.color;
    }

    public Vector2f getPosition(){
        return this.position;
    }

    public void update(float dt){
        this.position.add(vx * dt, vy * dt);
        this.lifetime -= 0.5f;
        this.size -= 0.1f;
    }

    public float getSize(){
        return this.size;
    }
    public float getLifetime(){
        return this.lifetime;
    }
}
