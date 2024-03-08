package core.renderer;

import core.engine.*;
import core.utils.AssetPool;

import java.util.List;

public class Renderer {
    private final ImGuiLayer imGuiLayer;
    private final ShaderProgram shaderProgram;
    private final BatchManager batchManager;

    public Renderer(List<Particle> particles){
        this.imGuiLayer = new ImGuiLayer(Window.getWindow());
        this.imGuiLayer.initImGui();

        this.batchManager = new BatchManager();
        for(Particle particle : particles){
            this.addVertex(particle);
        }

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.bind();
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.unbind();
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

    public void render(float dt, Scene scene){
        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", Camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix", Camera.viewMatrix());

        this.batchManager.render();

        this.shaderProgram.unbind();

        imGuiLayer.update(dt, scene);
    }

    public void dispose(){
        this.imGuiLayer.destroyImGui();
        this.shaderProgram.unbind();
        this.shaderProgram.dispose();
        this.batchManager.dispose();
    }
}
