package core.engine;

import core.renderer.ShaderProgram;
import core.utils.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class BatchManager {
    private final List<DoubleBufferedBatch> batches;
    private final ShaderProgram shaderProgram;

    public BatchManager(){
        this.batches = new ArrayList<>();

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/batch/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/batch/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.bind();
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.unbind();
    }

    public void addVertex(Particle particle){
        boolean added = false;
        for(DoubleBufferedBatch batch : batches){
            if(batch.hasRoom()){
                batch.addVertex(particle);
                added = true;
            }
        }

        if(!added){
            DoubleBufferedBatch newBatch = new DoubleBufferedBatch();
            newBatch.addVertex(particle);
            this.batches.add(newBatch);
        }
    }

    public void updateVertex(Particle particle, int index){
        DoubleBufferedBatch batch = batches.get( index / DoubleBufferedBatch.MAX_SIZE );
        batch.updateVertex(particle, index % DoubleBufferedBatch.MAX_SIZE);
    }

    public void removeVertex(Particle particle, int index){
        DoubleBufferedBatch batch = batches.get( index / DoubleBufferedBatch.MAX_SIZE );
        batch.removeVertex(particle, index % DoubleBufferedBatch.MAX_SIZE);
    }

    public void render(){
        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", Camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix", Camera.viewMatrix());

        for(DoubleBufferedBatch batch : batches){
            batch.render();
        }

        this.shaderProgram.unbind();
    }

    public void dispose(){
        for(DoubleBufferedBatch batch : batches){
            batch.dispose();
        }

        this.shaderProgram.dispose();
    }
}
