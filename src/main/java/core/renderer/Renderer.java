package core.renderer;

import core.engine.Scene;
import core.engine._2D.BatchManager;
import core.engine._2D.Particle;
import core.engine._2D.Scene2D;
import core.entities.GameObject;
import core.postprocess.FrameBuffer;
import core.postprocess.PostProcessingPipeline;
import core.utils.AssetPool;

import static core.utils.SETTINGS.*;
import static core.utils.SETTINGS.BLACK;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {

    private BatchManager batchManager = null;
    private PostProcessingPipeline postProcessing = null;
    private final FrameBuffer fistPassframeBuffer;
    private final ShaderProgram shaderProgram;

    public Renderer(Scene scene){

        this.postProcessing = new PostProcessingPipeline();
        if(scene.getClass().isAssignableFrom(Scene2D.class))
            this.batchManager = new BatchManager();

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/base/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/base/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.createUniform("modelMatrix");
        this.shaderProgram.createMaterialUniform("material");
        this.shaderProgram.createDirectionalLightUniform("directionalLight");
        for(int i = 0; i < NUM_P_LIGHTS; i++) {
            this.shaderProgram.createPointLightUniform("pointLight[" + i + "]");
        }

        this.fistPassframeBuffer = new FrameBuffer(true);
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

    public void render(Scene scene, LightsRenderer lightsRenderer, float dt){

        defaultFramebuffer();

        //-----draw scene into a fbo to extract the texture.-----------------------//
        this.fistPassframeBuffer.bind();
        if(WIRE_FRAME_MODE) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        if(batchManager != null) batchManager.render();

        lightsRenderer.render();

        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", scene.camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix", scene.camera.viewMatrix());
        this.shaderProgram.setUniform("directionalLight", scene.getLightsRenderer().directionalLight);
        for(int i = 0; i < NUM_P_LIGHTS; i++)
            this.shaderProgram.setUniform("pointLight["+ i +"]", scene.getLightsRenderer().pointLights.get(i));

        for(GameObject gameObject : scene.getGameObjects()) {

            this.shaderProgram.uploadMat4fUniform("modelMatrix", scene.camera.modelMatrix(gameObject));
            this.shaderProgram.setUniform("material", gameObject.getMaterial());
            gameObject.render();
        }
        this.shaderProgram.unbind();

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        this.fistPassframeBuffer.unbind();
        //-------------------------------------------------------------------//

        this.postProcessing.render(this.fistPassframeBuffer.getColorAttachment());
    }

    public static void defaultFramebuffer(){
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(BLACK.x, BLACK.y, BLACK.z, BLACK.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void dispose(){
        if(this.batchManager != null)   this.batchManager.dispose();
        this.postProcessing.dispose();
        this.fistPassframeBuffer.dispose();
        this.shaderProgram.dispose();
    }
}
