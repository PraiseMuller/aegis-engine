package core.engine;

import core.entities.GameObject;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.renderer.LightsRenderer;
import core.renderer.Renderer;

import java.util.ArrayList;

public abstract class Scene {
    protected Renderer sceneRenderer = null;
    protected ArrayList<GameObject> gameObjects = null;
    public Camera camera;

    public Scene(){

    }

    public abstract void init();
    public abstract void updateInputs(float dt);
    public abstract void update(float dt);
    public abstract void render(float dt);
    public abstract void dispose();
    public Camera getCamera(){
        return null;
    }
    public DirectionalLight getDirectionalLight(){
        return null;
    }
    public ArrayList<GameObject> getGameObjects(){
        return null;
    }
    public  ArrayList<PointLight> getPointLights(){return null;}
    public LightsRenderer getLightsRenderer(){ return null; };
}
