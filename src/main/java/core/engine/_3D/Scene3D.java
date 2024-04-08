package core.engine._3D;

import core.engine.Scene;
import core.entities.Player;
import core.inputs.Input;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.renderer.Renderer;
import org.joml.Vector3f;

import static core.utils.SETTINGS.*;

public class Scene3D extends Scene {
    private DirectionalLight directionalLight;
    private PointLight[] pointLights = new PointLight[8];

    public Scene3D(){
        this.init();
    }
    @Override
    public void init() {
        this.sceneRenderer = new Renderer(this);
        this.camera = new Camera3D(FOV, WIN_WIDTH, WIN_HEIGHT, Z_NEAR, Z_FAR);
        this.player = new Player(PLAYER_INIT_POSITION);

        int n = 20;
        this.pointLights[0] = new PointLight(new Vector3f(0.4f,0.7f,0.9f), new Vector3f(-n, n, -n), P_LIGHT_INTENSITY);
        this.pointLights[1] = new PointLight(new Vector3f(0.4f,0.7f,0.9f), new Vector3f(n, n, -n), P_LIGHT_INTENSITY);
        this.pointLights[2] = new PointLight(new Vector3f(0.4f,0.7f,0.9f), new Vector3f(-n,-n,-n), P_LIGHT_INTENSITY);
        this.pointLights[3] = new PointLight(new Vector3f(0.4f,0.7f,0.9f), new Vector3f(n,-n,-n), P_LIGHT_INTENSITY);
        this.pointLights[4] = new PointLight(new Vector3f(0.4f,0.7f,0.9f), new Vector3f(-n, n, n), P_LIGHT_INTENSITY);
        this.pointLights[5] = new PointLight(new Vector3f(0.4f,0.7f,0.9f), new Vector3f(n, n, n), P_LIGHT_INTENSITY);
        this.pointLights[6] = new PointLight(new Vector3f(0.4f,0.7f,0.9f), new Vector3f(-n,-n,n), P_LIGHT_INTENSITY);
        this.pointLights[7] = new PointLight(new Vector3f(0.4f,0.7f,0.9f), new Vector3f(n,-n,n), P_LIGHT_INTENSITY);

        this.directionalLight = new DirectionalLight(new Vector3f(0.1f, 0.1f, 0.2f), new Vector3f(1, 1, -1), D_LIGHT_INTENSITY);
    }

    @Override
    public void updateInputs(float dt) {
        Input.update(dt, this);
    }

    @Override
    public void update(float dt) {
        this.player.update(dt);
        this.timeElapsed += 0.01f;
    }

    @Override
    public void render(float dt) {
        this.sceneRenderer.render(this, dt);
    }

    @Override
    public void dispose() {
        this.sceneRenderer.dispose();
        this.player.dispose();
    }

    @Override
    public DirectionalLight getDirectionalLight(){
        return this.directionalLight;
    }

    @Override
    public PointLight[] getPointLights(){
        return this.pointLights;
    }
}
