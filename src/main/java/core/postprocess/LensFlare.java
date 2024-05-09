package core.postprocess;

import core.renderer.FrameBuffer;
import core.renderer.ShaderProgram;
import core.renderer.Texture;
import core.utils.AssetPool;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static core.utils.SETTINGS.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class LensFlare {

    private int vao;
    private final FrameBuffer framebuffer;
    private final Texture[] mips = new Texture[3]; //   1 = downsampled... 2 = feature gen... 3 = blur + upsample.
    private final ShaderProgram downsampleShader;
    private final ShaderProgram upsampleShader;
    private final ShaderProgram lensFlareFeatureGenShader;
    private final ShaderProgram gaussianBlurShader;

    public LensFlare(){

        this.downsampleShader = new ShaderProgram();
        this.downsampleShader.createVertexShader(AssetPool.getShader("assets/shaders/post-processing/vertex.glsl"));
        this.downsampleShader.createFragmentShader(AssetPool.getShader("assets/shaders/post-processing/downsample_fragment.glsl"));
        this.downsampleShader.link();
        this.downsampleShader.createUniform("srcTexture");
        this.downsampleShader.createUniform("srcResolution");

        this.upsampleShader = new ShaderProgram();
        this.upsampleShader.createVertexShader(AssetPool.getShader("assets/shaders/post-processing/vertex.glsl"));
        this.upsampleShader.createFragmentShader(AssetPool.getShader("assets/shaders/post-processing/upsample_fragment.glsl"));
        this.upsampleShader.link();
        this.upsampleShader.createUniform("srcTexture");
        this.upsampleShader.createUniform("filterRadius");

        this.lensFlareFeatureGenShader = new ShaderProgram();
        this.lensFlareFeatureGenShader.createVertexShader(AssetPool.getShader("assets/shaders/post-processing/vertex.glsl"));
        this.lensFlareFeatureGenShader.createFragmentShader(AssetPool.getShader("assets/shaders/post-processing/lens_flare_feature_fragment.glsl"));
        this.lensFlareFeatureGenShader.link();
        this.lensFlareFeatureGenShader.createUniform("srcTexture");

        this.gaussianBlurShader = new ShaderProgram();
        this.gaussianBlurShader.createVertexShader(AssetPool.getShader("assets/shaders/post-processing/vertex.glsl"));
        this.gaussianBlurShader.createFragmentShader(AssetPool.getShader("assets/shaders/post-processing/gaussian_blur_fragment.glsl"));
        this.gaussianBlurShader.link();
        this.gaussianBlurShader.createUniform("srcTexture");
        this.gaussianBlurShader.createUniform("horizontal");

        this.framebuffer = new FrameBuffer(false);
        for(int i = 0; i < this.mips.length; i++)
            this.mips[i] = new Texture(WIN_WIDTH, WIN_HEIGHT, 2, true);
    }

    public void render(Texture sceneBrightFrags, float filterRadius){

        this.framebuffer.bind();

        //down-sample
        this.framebuffer.changeTexture(this.mips[0]);   //change texture bound for the following draw call
        this.downsampleShader.bind();
        this.downsampleShader.uploadVec2fUniform("srcResolution", new Vector2f(WIN_WIDTH/4.0f, WIN_HEIGHT/4.0f));
        sceneBrightFrags.bind();
        this.downsampleShader.uploadIntUniform("srcTexture", sceneBrightFrags.getBindLocation());
        _renderQuad();
        sceneBrightFrags.unbind();
        this.downsampleShader.unbind();

        //feature generation
        this.framebuffer.changeTexture(this.mips[1]);  //change texture bound for the following draw call
        Texture downsampledMip = this.mips[0];      //the input for this pass (the downsampled texture) is the previously bound texture.
        downsampledMip.bind();
        this.lensFlareFeatureGenShader.bind();
        this.lensFlareFeatureGenShader.uploadIntUniform("srcTexture", downsampledMip.getBindLocation());
        _renderQuad();
        downsampledMip.unbind();
        this.lensFlareFeatureGenShader.unbind();

        //blur (Expensive asf tf?)
        this.framebuffer.changeTexture(this.mips[2]);
        Texture toBlur = this.mips[1];
        int amount = 2;
        this.gaussianBlurShader.bind();
        for (int i = 0; i < amount; i++) {

            for(int iteration = 0; iteration < 2; iteration++) {

                toBlur.bind();
                this.gaussianBlurShader.uploadIntUniform("srcTexture", toBlur.getBindLocation());
                this.gaussianBlurShader.uploadIntUniform("horizontal", iteration);
                _renderQuad();
                toBlur.unbind();
            }
            toBlur = this.mips[2];
        }
        this.gaussianBlurShader.unbind();

        //up-sample.
        this.framebuffer.defaultTexture();
        Texture blurredFeatureMip = toBlur;
        blurredFeatureMip.bind();
        this.upsampleShader.bind();
        this.upsampleShader.uploadFloatUniform("filterRadius", filterRadius);
        this.upsampleShader.uploadIntUniform("srcTexture", blurredFeatureMip.getBindLocation());
        _renderQuad();
        blurredFeatureMip.unbind();
        this.upsampleShader.unbind();

        this.framebuffer.unbind();
    }

    public Texture getTexture(){
        return this.framebuffer.getColorAttachment();
    }

    private void _renderQuad() {

        if (this.vao == 0) {
            this.vao = glGenVertexArrays();
            glBindVertexArray(this.vao);

            float[] quadVertices = {
                    1.0f,  1.0f,   1.0f, 1.0f,
                    -1.0f,  1.0f,   0.0f, 1.0f,
                    -1.0f, -1.0f,   0.0f, 0.0f,
                    1.0f, -1.0f,   1.0f, 0.0f
            };
            FloatBuffer vertBuffer = MemoryUtil.memAllocFloat(quadVertices.length);
            vertBuffer.put(quadVertices).flip();

            int[] indices = {
                    0, 3, 2,
                    0, 2, 1
            };
            IntBuffer indBuffer = MemoryUtil.memAllocInt(indices.length);
            indBuffer.put(indices).flip();

            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

            int ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indBuffer, GL_STATIC_DRAW);

            glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);   //pos
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES); //tex cords

            MemoryUtil.memFree(indBuffer);
            MemoryUtil.memFree(vertBuffer);
        }

        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    public void dispose() {

        glDeleteVertexArrays(this.vao);
        this.framebuffer.dispose();
        this.downsampleShader.dispose();
        this.upsampleShader.dispose();
        this.lensFlareFeatureGenShader.dispose();
        for (Texture mip : this.mips)
            mip.dispose();
    }
}
