package core.engine._3D;

import core.engine.Camera;
import core.engine.Scene;
import core.entities.GameObject;
import core.inputs.Input;
import core.renderer.ImGuiLayer;
import core.renderer.LightsRenderer;
import core.renderer.Renderer;
import core.renderer.Window;
import core.utils.AssetPool;

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
        this.sceneRenderer = new Renderer(this);
        this.camera = new Camera3D(FOV, WIN_WIDTH, WIN_HEIGHT, Z_NEAR, Z_FAR);
        this.lightsRenderer = new LightsRenderer();

        this.imGuiLayer = new ImGuiLayer(Window.getWindow());
        this.imGuiLayer.initImGui();

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

//        float x = (float) Math.cos(Time.get() + dt);
//        float y = (float) Math.sin(Time.get() + dt);
//        this.camera.movePosition(x, y, 0.0f);

        for (GameObject gameObject : this.gameObjects){
            gameObject.update(dt);
        }
    }

    @Override
    public void render(float dt) {

        //FIND A WAY TO BATCH TOGETHER ALL THE VERTICES IN A SCENE AND SEND THIS TO THE RENDERER
        this.sceneRenderer.render(this, this.lightsRenderer, dt);
        imGuiLayer.render(dt, this);
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
