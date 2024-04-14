package core.engine._2D;

import core.engine.Scene;
import core.engine.StateMachine;
import core.entities.GameObject;
import core.inputs.Input;
import core.renderer.Renderer;
import core.utils.AssetPool;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static core.utils.SETTINGS.*;

public class Scene2D extends Scene {
    public static int pCount = 0;   // number of particle objects
    private final List<Particle> particles = new ArrayList<>();

    public Scene2D(){
        this.init();
    }

    @Override
    public void init(){
        this.camera = new Camera2D();
        this.sceneRenderer = new Renderer(this);

        //INITIALIZE AND ADD GAME-OBJECTS TO SCENE
        this.gameObjects = new ArrayList<>();
        AssetPool.initializeAllEngineStuff(this.gameObjects);
    }

    @Override
    public void updateInputs(float dt){
        Input.update(dt, this);
        //this.camera.smoothFollow(player.getPosition());
    }

    @Override
    public void update(float dt){

        //update if clicked, add particles
        if(StateMachine.play()){// && Input.isDragging()){
            Particle particle = new Particle(  new Vector2f((float) Math.random() * 2 * WIN_WIDTH, 0) );
            this.particles.add(particle);
            this.sceneRenderer.addVertex(particle);
            pCount++;
        }

        //THE LOGIC
        for(int i = 0; i < this.particles.size(); i++) {

            Particle particle = this.particles.get(i);
            particle.update(dt);

            if(particle.getLifetime() <= 0){
                this.particles.remove(particle);
                this.sceneRenderer.removeVertex(particle, i);
                pCount--;
            }
            else
                this.sceneRenderer.updateVertex(particle, i);
        }
    }
    @Override
    public void render(float dt){
        //this.sceneRenderer.render(this, dt);
    }

    @Override
    public void dispose(){
        this.sceneRenderer.dispose();

        for (GameObject gameObject : this.gameObjects){
            gameObject.dispose();
        }
    }
}
