package core.scenes;

import core.Window;
import core.gui.ImGuiLayer;
import core.inputs.PRS_KYS;
import core.models.GameObject;
import core.render.Renderer;
import core.utils.CreateGameObjects;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class Scene_001 extends Scene{
    private List<GameObject> objects;
    private List<GameObject> lights;
    private Renderer renderer;
    private ImGuiLayer gui;

    public Scene_001(){
        this.init();
    }

    @Override
    public void init() {
        this.objects = new ArrayList<>();
        this.lights = new ArrayList<>();

        CreateGameObjects.get(this.objects, this.lights);
        this.renderer = new Renderer();

        this.gui = new ImGuiLayer(Window.getWindow());
        this.gui.initImGui();
    }

    @Override
    public void update(float dt) {
        PRS_KYS.update(dt, this.renderer);
        this.renderer.update(this.objects, this.lights);
        this.gui.update(dt, this);
    }

    @Override
    public void cleanup() {
        this.renderer.cleanup(this.objects, this.lights);
    }

    @Override
    public void imgui(float dt){
        ImGui.begin("Framerate");
        ImGui.text("FPS: " + 1f/dt);
        ImGui.end();
    }
}
