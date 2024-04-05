package core.postprocess;

import core.engine.BatchManager;
import core.engine.Scene;
import core.renderer.Texture;
import core.utils.SETTINGS;

import static core.utils.SETTINGS.*;
import static core.utils.SETTINGS.BLACK;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class PostProcessingPipeline {
    private final FrameBuffer fistPassframeBuffer;
    private final BloomRenderer bloomRenderer;
    private final ScreenQuad fScreenQuad;    // <-----the final screen;

    public PostProcessingPipeline(){
        this.fistPassframeBuffer = new FrameBuffer();
        this.bloomRenderer = new BloomRenderer(WIN_WIDTH, WIN_HEIGHT);
        this.fScreenQuad = new ScreenQuad();
    }

    public void drawScene(Scene scene, BatchManager batchManager){

        //draw scene into a fbo to extract the texture.
        this.fistPassframeBuffer.bind();
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        batchManager.render();
        scene.getPlayer().render();
        //glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);


        //Process: 1. Bloom
        Texture sceneTexture = this.fistPassframeBuffer.getColorAttachment();
        this.bloomRenderer.renderBloomTexture(sceneTexture, 0.0056f);

        //Apply: 1. Bloom
        defaultFramebuffer();
        Texture bloomTexture = this.bloomRenderer.bloomTexture();
        this.fScreenQuad.render(sceneTexture, bloomTexture);
    }

    public void dispose(){
        this.fistPassframeBuffer.dispose();
        this.bloomRenderer.dispose();
        this.fScreenQuad.dispose();
    }


    private void defaultFramebuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(BLACK.x, BLACK.y, BLACK.z, BLACK.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }
}
