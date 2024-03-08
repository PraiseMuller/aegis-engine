package core.engine;

import core.inputs.Input;
import core.renderer.Renderer;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static core.utils.SETTINGS.WIN_HEIGHT;
import static core.utils.SETTINGS.WIN_WIDTH;

public class Scene {
    public static int pCount = 0;   // number of particle objects
    private final float pSize = 15.0f;
    private final List<Particle> particles = new ArrayList<>();
    private Renderer renderer;

    public Scene(){
        Camera.init();
        this.loadParticles();
    }

    public void loadParticles(){
        for(int i = 0; i < pCount; i++){
            this.particles.add(new Particle(new Vector2f((float) Math.random() * WIN_WIDTH, (float) Math.random() * WIN_HEIGHT), pSize));
        }

        this.renderer = new Renderer(this.particles);
    }

    public void updateInputs(float dt){
        Input.update(dt, this);

        //update if clicked, add particles
        //if(Input.isDragging()){
            Vector2f pos = Input.getMousePosition();

            int SELECTION_SIZE = 1;
            for(int i = -SELECTION_SIZE; i < SELECTION_SIZE; i++){
                for(int j = -SELECTION_SIZE; j < SELECTION_SIZE; j++) {

                    Particle particle = new Particle(pos, pSize);
                    this.particles.add(particle);
                    this.renderer.addVertex(particle);
                    pCount++;
                }
            }
        //}
    }

    public void update(float dt){
        //THE LOGIC
        for(int i = 0; i < this.particles.size(); i++) {

            Particle particle = this.particles.get(i);
            particle.update(dt);

            if(particle.getLifetime() <= 0){
                this.particles.remove(particle);
                this.renderer.removeVertex(particle, i);
                pCount--;
            }
            else
                this.renderer.updateVertex(particle, i);
        }
    }

    public void render(float dt){
        this.renderer.render(dt, this);
    }

    public void dispose(){
        this.renderer.dispose();
    }
}
