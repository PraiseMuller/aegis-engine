package core.engine._3D;

import core.engine.Scene;
import core.engine._2D.Camera2D;
import core.entities.Player;
import core.inputs.Input;
import core.renderer.Renderer;
import org.joml.Vector3f;

import static core.utils.SETTINGS.*;

public class Scene3D extends Scene {

    public Scene3D(){
        this.init();
    }
    @Override
    public void init() {
        this.sceneRenderer = new Renderer();
        this.camera = new Camera3D(FOV, WIN_WIDTH, WIN_HEIGHT, Z_NEAR, Z_FAR);
        this.player = new Player(PLAYER_INIT_POSITION);
    }

    @Override
    public void updateInputs(float dt) {
        Input.update(dt, this);
    }

    @Override
    public void update(float dt) {

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
}
