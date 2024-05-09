package core.renderer;

import core.postprocess.BloomRenderer;
import core.postprocess.LensFlare;

import static core.utils.SETTINGS.*;

public class PostProcessingPipeline {

    private final BloomRenderer bloomRenderer;
    private final LensFlare lensFlare;

    public PostProcessingPipeline(){
        this.bloomRenderer = new BloomRenderer(WIN_WIDTH, WIN_HEIGHT);
        this.lensFlare = new LensFlare();
    }

    public void render(Texture sceneTexture){

        this.bloomRenderer.render(sceneTexture, 0.0001f);
        this.lensFlare.render(this.bloomRenderer.brightFragments(), 0.00001f);
    }

    public Texture getBloomTexture(){
        return this.bloomRenderer.getTexture();
    }

    public Texture getLensFlareTexture(){
        return this.lensFlare.getTexture();
    }

    public void dispose(){
        this.bloomRenderer.dispose();
        this.lensFlare.dispose();
    }
}
