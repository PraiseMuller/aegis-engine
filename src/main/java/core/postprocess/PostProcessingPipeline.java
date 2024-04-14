package core.postprocess;

import core.renderer.Renderer;
import core.renderer.Texture;

import static core.utils.SETTINGS.*;
import static core.utils.SETTINGS.BLACK;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class PostProcessingPipeline {

    private final BloomRenderer bloomRenderer;
    private final ScreenQuad finalScreenQuad;    // <-----the final screen;

    public PostProcessingPipeline(){
        this.bloomRenderer = new BloomRenderer(WIN_WIDTH, WIN_HEIGHT);
        this.finalScreenQuad = new ScreenQuad();
    }

    public void render(Texture sceneTexture){

        this.bloomRenderer.render(sceneTexture, 0.0001f);

        Renderer.defaultFramebuffer();
        this.finalScreenQuad.render(sceneTexture, this.bloomRenderer.bloomTexture());
    }

    public void dispose(){
        this.bloomRenderer.dispose();
        this.finalScreenQuad.dispose();
    }
}
