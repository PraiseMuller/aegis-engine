package core.engine;

import core.entities.Player;
import core.renderer.Renderer;

public abstract class Scene {
    protected Renderer sceneRenderer = null;
    protected Player player = null;
    public float timeElapsed = 0.0f;
    public Camera camera;

    public abstract void init();
    public abstract void updateInputs(float dt);
    public abstract void update(float dt);
    public abstract void render(float dt);
    public abstract void dispose();

    public Player getPlayer(){
        return this.player;
    }

    public Camera getCamera(){
        return this.camera;
    }
}
