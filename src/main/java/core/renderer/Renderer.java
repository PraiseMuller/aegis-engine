package core.renderer;

import core.engine.*;
import core.postprocess.PostProcessingPipeline;

public class Renderer {
    private final ImGuiLayer imGuiLayer;
    private final BatchManager batchManager;
    private final PostProcessingPipeline postProcessing;

    public Renderer(){
        this.imGuiLayer = new ImGuiLayer(Window.getWindow());
        this.imGuiLayer.initImGui();
        this.batchManager = new BatchManager();
        this.postProcessing = new PostProcessingPipeline();
    }

    public void addVertex(Particle particle){
        this.batchManager.addVertex(particle);
    }

    public void updateVertex(Particle particle, int index){
        this.batchManager.updateVertex(particle, index);
    }

    public void removeVertex(Particle particle, int index){
        this.batchManager.removeVertex(particle, index);
    }

    public void render(Scene scene, float dt){
        this.postProcessing.drawScene(scene, this.batchManager);
        imGuiLayer.update(dt, scene);
    }

    public void dispose(){
        this.imGuiLayer.destroyImGui();
        this.batchManager.dispose();
        this.postProcessing.dispose();
    }
}
