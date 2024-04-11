package core.postprocess;

import core.engine.Scene;
import core.engine._2D.BatchManager;
import core.engine._3D.Scene3D;
import core.entities.GameObject;
import core.renderer.Texture;

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
        this.fistPassframeBuffer = new FrameBuffer(true);
        this.bloomRenderer = new BloomRenderer(WIN_WIDTH, WIN_HEIGHT);
        this.fScreenQuad = new ScreenQuad();
    }

    public void drawScene(Scene scene, BatchManager batchManager){

        //draw scene into a fbo to extract the texture.
        this.fistPassframeBuffer.bind();
        if(WIRE_FRAME_MODE) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        if(batchManager != null) batchManager.render();

        for(GameObject gameObject : scene.getGameObjects()) {
            gameObject.render(scene);
        }

        scene.getLightsRenderer().render();

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        this.fistPassframeBuffer.unbind();

        ///////////////////////////////////////////////////////////////////////////////////
        //Process: 1. Bloom
        Texture sceneTexture = this.fistPassframeBuffer.getColorAttachment();
        this.bloomRenderer.renderBloomTexture(sceneTexture, 0.0001f);

        ////////////////////////////////////////////////////////////////////////////////////
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
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(BLACK.x, BLACK.y, BLACK.z, BLACK.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }
}
