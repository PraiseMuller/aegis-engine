package core.renderer;

import core.engine.Scene;
import core.engine._2D.BatchManager;
import core.engine._2D.Particle;
import core.engine._2D.Scene2D;
import core.postprocess.PostProcessingPipeline;

public class Renderer {
    private ImGuiLayer imGuiLayer = null;
    private BatchManager batchManager = null;
    private PostProcessingPipeline postProcessing = null;

    public Renderer(Scene scene){
        this.imGuiLayer = new ImGuiLayer(Window.getWindow());
        this.imGuiLayer.initImGui();
        this.postProcessing = new PostProcessingPipeline();

        if(scene.getClass().isAssignableFrom(Scene2D.class))
            this.batchManager = new BatchManager();
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
        if(this.batchManager != null)   this.batchManager.dispose();
        this.postProcessing.dispose();
    }
}
