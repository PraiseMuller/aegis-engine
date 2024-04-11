package core.engine._3D;

import core.engine.Camera;
import core.engine.Scene;
import core.entities.GameObject;
import core.inputs.Input;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.renderer.LightsRenderer;
import core.renderer.Renderer;
import core.utils.AssetPool;
import org.joml.Vector3f;

import java.util.ArrayList;

import static core.utils.SETTINGS.*;

public class Scene3D extends Scene {
    private DirectionalLight directionalLight = null;
    private ArrayList<PointLight> pointLights = null;
    private ArrayList<GameObject> gameObjects = null;
    private LightsRenderer lightsRenderer = null;

    public Scene3D(){
        super();
        this.init();
    }

    @Override
    public void init() {
        this.sceneRenderer = new Renderer(this);
        this.camera = new Camera3D(FOV, WIN_WIDTH, WIN_HEIGHT, Z_NEAR, Z_FAR);

        this.pointLights = new ArrayList<>();
        this.directionalLight = new DirectionalLight(new Vector3f(0.1f, 0.1f, 0.2f), new Vector3f(1, 1, -1), D_LIGHT_INTENSITY);

        //INITIALIZE AND ADD GAME-OBJECTS TO SCENE
        this.gameObjects = new ArrayList<>();
        AssetPool.initializeAllEngineStuff(this.gameObjects, this.pointLights);

        this.lightsRenderer = new LightsRenderer(this.pointLights, this.directionalLight);
    }

    @Override
    public void updateInputs(float dt) {
        Input.update(dt, this);
    }

    @Override
    public void update(float dt) {

        for (GameObject gameObject : this.gameObjects){
            gameObject.update(dt);
        }
    }

    @Override
    public void render(float dt) {

        //FIND A WAY TO BATCH TOGETHER ALL THE VERTICES IN A SCENE AND SEND THIS TO THE RENDERER
        this.sceneRenderer.render(this, dt);
    }

    @Override
    public void dispose() {
        this.sceneRenderer.dispose();
        this.lightsRenderer.dispose();
        for (GameObject gameObject : this.gameObjects){
            gameObject.dispose();
        }
    }

    @Override
    public Camera getCamera(){
        return this.camera;
    }
    @Override
    public DirectionalLight getDirectionalLight(){
        return this.directionalLight;
    }
    @Override
    public ArrayList<GameObject> getGameObjects(){
        return this.gameObjects;
    }
    @Override
    public ArrayList<PointLight> getPointLights(){
        return this.pointLights;
    }
    public void addGameObject(GameObject obj){
        this.gameObjects.add(obj);
    }
    @Override
    public LightsRenderer getLightsRenderer(){
        return this.lightsRenderer;
    }
}
