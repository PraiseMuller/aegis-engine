package core.engine;

import java.util.ArrayList;
import java.util.List;

public class BatchManager {
    private final List<DoubleBufferedBatch> batches;

    public BatchManager(){
        this.batches = new ArrayList<>();
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
        for(DoubleBufferedBatch batch : batches){
            batch.render();
        }
    }

    public void dispose(){
        for(DoubleBufferedBatch batch : batches){
            batch.dispose();
        }
    }
}
