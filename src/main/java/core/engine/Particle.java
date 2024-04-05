package core.engine;

import core.utils.MathUtils;
import core.utils.PerlinNoise;
import org.joml.*;
import org.joml.Math;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Particle {
    private float size;
    private final Vector2f position;
    private final Vector4f color;
    private final float vx, vy;
    private float lifetime = 200.0f;

    public Particle(Vector2f pos){
        this.size = (float) Math.random() * 20.0f;
        this.position = pos;
        this.color = new Vector4f(MathUtils.getRandom(1.0f,0.0f), MathUtils.getRandom(1.0f,0.0f), MathUtils.getRandom(1.0f,0.0f), 0.1f);
        this.vx = MathUtils.getRandom(1.0f,0.0f);

        if(this.size > 15.0f)
            this.vy = (float) - (Math.random() - 1.0f) * 300.0f;

        else
            this.vy = (float) - (Math.random() - 1.0f) * 800.0f;

    }

    public Vector4f getColor(){
        return this.color;
    }

    public Vector2f getPosition(){
        return this.position;
    }

    public void update(float dt){
        this.position.add((float) (vx * dt  + Math.cos(glfwGetTime())), (float) (vy * dt + Math.sin(glfwGetTime())));
        this.lifetime -= 0.5f;
        this.size -= 0.01f;
    }

    public float getSize(){
        return this.size;
    }
    public float getLifetime(){
        return this.lifetime;
    }
}
