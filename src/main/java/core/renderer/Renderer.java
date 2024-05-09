package core.renderer;

import core.engine.Scene;
import core.engine._2D.BatchManager;
import core.engine._2D.Particle;
import core.entities.GameObject;
import core.utils.AssetPool;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static core.utils.SETTINGS.*;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final int MAX_NUM_BONES = 200;
    private final BatchManager batchManager = null;
    private final PostProcessingPipeline postProcessing;
    private final FrameBuffer fistPassFrameBuffer;
    private final ShaderProgram shaderProgram;
    private final ScreenQuad finalScreenQuad;    // <-----the final screen;

    public Renderer(){

        this.postProcessing = new PostProcessingPipeline();

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/base/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/base/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.createUniform("modelMatrix");
//        this.shaderProgram.createUniform("gDisplayBoneIndex");
        this.shaderProgram.createMaterialUniform("material");
        this.shaderProgram.createDirectionalLightUniform("directionalLight");
        for(int i = 0; i < NUM_P_LIGHTS; i++)
            this.shaderProgram.createPointLightUniform("pointLight[" + i + "]");

//        for(int i = 0; i < MAX_NUM_BONES; i++)
//            this.shaderProgram.createUniform("gBones[" + i + "]");

        this.fistPassFrameBuffer = new FrameBuffer(true);
        this.finalScreenQuad = new ScreenQuad();
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

    public void render(Scene scene){

        //-----draw scene into a fbo to extract the texture.----------------------------------------------------------//
        this.fistPassFrameBuffer.bind();

        if(WIRE_FRAME_MODE) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        //draw cubemap first
        //Scene3D scene3D = (Scene3D) scene;
        //scene3D.cubeMap().render(scene);

        if(batchManager != null) batchManager.render();
        scene.getLightsRenderer().render(scene);

        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", scene.camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix", scene.camera.viewMatrix());
//        this.shaderProgram.uploadIntUniform("gDisplayBoneIndex", G_DISPLAY_BONE_INDEX);
        this.shaderProgram.setUniform("directionalLight", scene.getLightsRenderer().directionalLight);
        for(int i = 0; i < NUM_P_LIGHTS; i++)
            this.shaderProgram.setUniform("pointLight["+ i +"]", scene.getLightsRenderer().pointLights.get(i));

        for(GameObject gameObject : scene.getGameObjects()) {

            //animation stuff
//            ArrayList<Matrix4f> transforms = new ArrayList<>();
//            gameObject.getMesh().getBoneTransforms(transforms);

//            for(int i = 0; i < transforms.size(); i++){
//                //System.out.println("\n"+transforms.get(i));
//                this.shaderProgram.uploadMat4fUniform("gBones["+i+"]", transforms.get(i));
//            }

            this.shaderProgram.uploadMat4fUniform("modelMatrix", scene.camera.modelMatrix(gameObject));
            this.shaderProgram.setUniform("material", gameObject.getMaterial());
            gameObject.render();

//            transforms.clear();
//            transforms = null;
        }
        this.shaderProgram.unbind();

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        this.fistPassFrameBuffer.unbind();
        //--------------------------------------------------------------------------------------------------------------//

        //Render and apply all post-processing effects, then draw final screen
        if(POST_PROCESSING) {

            Texture sceneMip = this.fistPassFrameBuffer.getColorAttachment();
            this.postProcessing.render(sceneMip);

            Texture bloomMip = this.postProcessing.getBloomTexture();
            Texture flareMip = this.postProcessing.getLensFlareTexture();
            this.finalScreenQuad.render(sceneMip, bloomMip, flareMip);                                //   100% NICE
        }
        else{

            //We don't get to do the cool stuff :-(
            Texture sceneMip = this.fistPassFrameBuffer.getColorAttachment();
            this.finalScreenQuad.render(sceneMip, sceneMip, sceneMip);// blending the same texture 3x ? Refactor this later.
        }
    }

    public void dispose(){
        if(this.batchManager != null)   this.batchManager.dispose();    // Hehe a tip ;-)
        this.postProcessing.dispose();
        this.fistPassFrameBuffer.dispose();
        this.shaderProgram.dispose();
        this.finalScreenQuad.dispose();
    }
}
