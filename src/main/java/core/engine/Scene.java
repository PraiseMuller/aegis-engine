package core.engine;

import core.entities.GameObject;
import core.renderer.LightsRenderer;
import core.renderer.Renderer;

import java.util.ArrayList;

public abstract class Scene {
    protected Renderer sceneRenderer = null;
    protected ArrayList<GameObject> gameObjects = null;
    public Camera camera;
    public StateMachine stateMachine;

    public Scene(){
        this.stateMachine = new StateMachine();
    }

    public abstract void init();
    public abstract void updateInputs(float dt);
    public abstract void update(float dt);
    public abstract void render(float dt);
    public abstract void dispose();
    public Camera getCamera(){
        return null;
    }
    public StateMachine stateMachine(){
        return this.stateMachine;
    }
    public ArrayList<GameObject> getGameObjects(){
        return null;
    }
    public LightsRenderer getLightsRenderer(){ return null; }
}
