package core.engine._3D;

import core.engine.Camera;
import core.engine.Scene;
import core.entities.GameObject;
import core.inputs.Input;
import core.renderer.*;
import core.renderer.environment.CubeMap;
import core.utils.AssetPool;
import core.utils.Time;

import java.util.ArrayList;

import static core.utils.SETTINGS.*;

public class Scene3D extends Scene {

    private ArrayList<GameObject> gameObjects = null;
    private LightsRenderer lightsRenderer = null;
    private ImGuiLayer imGuiLayer = null;

    public Scene3D(){
        super();
        this.init();
    }

    @Override
    public void init() {

        this.camera = new Camera3D(FOV, WIN_WIDTH, WIN_HEIGHT, Z_NEAR, Z_FAR);

        this.imGuiLayer = new ImGuiLayer(Window.getWindow());
        this.imGuiLayer.initImGui();

        this.sceneRenderer = new Renderer();
        this.lightsRenderer = new LightsRenderer();

        //INITIALIZE AND ADD GAME-OBJECTS TO SCENE
        this.gameObjects = new ArrayList<>();
        AssetPool.initializeAllEngineStuff(this.gameObjects);
    }

    @Override
    public void updateInputs(float dt) {
        Input.update(dt, this);
    }

    @Override
    public void update(float dt) {

//        float x = (float) Math.cos(Time.get()) * dt * 5.0f;
        float y = (float) Math.cos(Time.get()) * dt * 5.0f;
//        float z = (float) Math.cos(Time.get()) * dt * 5.0f;
        this.camera.movePosition(0, y, 0);

        for (GameObject gameObject : this.gameObjects){
            gameObject.update(dt);
        }
    }

    @Override
    public void render(float dt) {

        //Find a way to batch all vertices and send these to the renderer instead.
        this.sceneRenderer.render(this);
        imGuiLayer.render(this, dt);
    }

    @Override
    public void dispose() {
        this.sceneRenderer.dispose();
        this.lightsRenderer.dispose();
        this.imGuiLayer.destroyImGui();
        for (GameObject gameObject : this.gameObjects){
            gameObject.dispose();
        }
    }

    @Override
    public Camera getCamera(){
        return this.camera;
    }
    @Override
    public ArrayList<GameObject> getGameObjects(){
        return this.gameObjects;
    }
    @Override
    public LightsRenderer getLightsRenderer(){ return this.lightsRenderer; }
    public void addGameObject(GameObject obj){
        this.gameObjects.add(obj);
    }
}
