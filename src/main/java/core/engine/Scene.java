package core.engine;

import core.entities.Player;
import core.inputs.Input;
import core.renderer.Renderer;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static core.utils.SETTINGS.WIN_HEIGHT;
import static core.utils.SETTINGS.WIN_WIDTH;

public class Scene implements IGameLogic {
    public static int pCount = 0;   // number of particle objects
    private final List<Particle> particles = new ArrayList<>();
    private Renderer sceneRenderer;
    private Player player;
    public float timeElapsed = 0.0f;

    public Scene(){
        this.init();
    }

    @Override
    public void init(){
        Camera.init();
        this.player = new Player(new Vector3f(WIN_WIDTH / 2f, WIN_HEIGHT / 2f, 0));
        this.sceneRenderer = new Renderer();
    }

    @Override
    public void updateInputs(float dt){
        Input.update(dt, this);
        Camera.smoothFollow(player.getPosition());
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

        this.timeElapsed += 0.01f;
    }
    @Override
    public void render(float dt){
        this.sceneRenderer.render(this, dt);
    }

    @Override
    public void dispose(){
        this.sceneRenderer.dispose();
        this.player.dispose();
    }

    public Player getPlayer(){
        return this.player;
    }
}
