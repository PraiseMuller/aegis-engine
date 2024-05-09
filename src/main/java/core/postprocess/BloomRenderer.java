package core.postprocess;

import core.renderer.FrameBuffer;
import core.renderer.ShaderProgram;
import core.renderer.Texture;
import core.utils.AssetPool;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static core.utils.SETTINGS.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class BloomRenderer {

    private int vao;
    private final BloomFramebuffer bloomFramebuffer;
    private final FrameBuffer utilityFramebuffer;
    private final ShaderProgram downsampleShader;
    private final ShaderProgram upsampleShader;
    private final ShaderProgram extractBrightFragsShader;
    private final ShaderProgram gaussianBlurShader;

    public BloomRenderer(int windowWidth, int windowHeight) {

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

        this.extractBrightFragsShader = new ShaderProgram();
        this.extractBrightFragsShader.createVertexShader(AssetPool.getShader("assets/shaders/post-processing/vertex.glsl"));
        this.extractBrightFragsShader.createFragmentShader(AssetPool.getShader("assets/shaders/post-processing/extract_bright_frags_fragment.glsl"));
        this.extractBrightFragsShader.link();
        this.extractBrightFragsShader.createUniform("srcTexture");

        this.gaussianBlurShader = new ShaderProgram();
        this.gaussianBlurShader.createVertexShader(AssetPool.getShader("assets/shaders/post-processing/vertex.glsl"));
        this.gaussianBlurShader.createFragmentShader(AssetPool.getShader("assets/shaders/post-processing/gaussian_blur_fragment.glsl"));
        this.gaussianBlurShader.link();
        this.gaussianBlurShader.createUniform("srcTexture");
        this.gaussianBlurShader.createUniform("horizontal");

        // Framebuffer
        int num_bloom_mips = 4; // Experiment with this value
        this.bloomFramebuffer = new BloomFramebuffer(windowWidth, windowHeight, num_bloom_mips);
        this.utilityFramebuffer = new FrameBuffer(false);
    }

    public void render(Texture srcTexture, float filterRadius) {

        //Process: 1. Extract the brightest fragments in 'srcTexture', discard all fragments below some threshold.
        //might be able to skip/combine this step in the shader it is needed?
        _extractBrightFrags(srcTexture);
        Texture sceneBrightFrags = this.utilityFramebuffer.getColorAttachment();

        //Process: 2. Bloom
        this.bloomFramebuffer.bind();
        _downsampleMips(sceneBrightFrags);
        _upsampleMips(filterRadius);
        this.bloomFramebuffer.unbind();
    }

    private void _extractBrightFrags(Texture srcTexture){

        this.utilityFramebuffer.bind();

        this.extractBrightFragsShader.bind();
        srcTexture.bind();
        this.extractBrightFragsShader.uploadIntUniform("srcTexture", srcTexture.getBindLocation());
        _renderQuad();
        this.extractBrightFragsShader.unbind();
        srcTexture.unbind();

        this.utilityFramebuffer.unbind();
    }
    private void _downsampleMips(Texture texture) {

        ArrayList<Mip> mipChain = this.bloomFramebuffer.getMipChain();

        this.downsampleShader.bind();
        this.downsampleShader.uploadVec2fUniform("srcResolution", new Vector2f(WIN_WIDTH, WIN_HEIGHT));
        texture.bind();
        this.downsampleShader.uploadIntUniform("srcTexture", texture.getBindLocation());

        // Progressively downsample through the mip chain
        for (int i = 0; i < mipChain.size(); i++) {

            Mip mip = mipChain.get(i);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mip.texture.getId(), 0);
            glViewport(0, 0, (int) mip.size.x, (int) mip.size.y);

            // Render screen-filled quad of resolution of current mip
            _renderQuad();

            // Set current mip resolution as srcResolution for next iteration
            this.downsampleShader.uploadVec2fUniform("srcResolution", mip.size);
            this.downsampleShader.uploadIntUniform("srcTexture", mip.texture.getBindLocation());

            // Set current mip as texture input for next iteration
            mip.texture.bind();
        }

        //----------------Might reduce performance / speed ?
        texture.unbind();
        for(Mip mip : mipChain)
            mip.texture.unbind();

        this.downsampleShader.unbind();
    }
    private void _upsampleMips(float filterRadius) {

        ArrayList<Mip> mipChain = this.bloomFramebuffer.getMipChain();

        //Smallest ---> Biggest, Mip Level.
        for (int i = mipChain.size() - 1; i > 0 ; i--) {

            Mip mip = mipChain.get(i);
            Mip nextMip = mipChain.get(i - 1);

            //blur mip
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mip.texture.getId(), 0);
            int amount = 3;
            this.gaussianBlurShader.bind();
            for (int idx = 0; idx < amount; idx++) {
                for(int iteration = 0; iteration < 2; iteration++) {    //make sure to bloom both vertically and horizontally hehe

                    mip.texture.bind();
                    this.gaussianBlurShader.uploadIntUniform("srcTexture", mip.texture.getBindLocation());
                    this.gaussianBlurShader.uploadIntUniform("horizontal", iteration);
                    glViewport(0, 0, (int) mip.size.x, (int) mip.size.y);
                    _renderQuad();
                    mip.texture.unbind();
                }
            }
            this.gaussianBlurShader.unbind();

            // up-sample and blend mip
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, nextMip.texture.getId(), 0);
            this.upsampleShader.bind();
            this.upsampleShader.uploadFloatUniform("filterRadius", filterRadius);
            mip.texture.bind();
            this.upsampleShader.uploadIntUniform("srcTexture", mip.texture.getBindLocation());
            glViewport(0, 0, (int) nextMip.size.x, (int) nextMip.size.y);
            glEnable(GL_BLEND);             // Enable additive blending
            glBlendFunc(GL_ONE, GL_ONE);
            glBlendEquation(GL_FUNC_ADD);
            _renderQuad();
            glDisable(GL_BLEND);           // Disable additive blending

            mip.texture.unbind();
            this.upsampleShader.unbind();
        }
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

    public Texture getTexture() {
        return this.bloomFramebuffer.getMipChain().get(0).texture;
    }
    public Texture brightFragments() {
        return this.utilityFramebuffer.getColorAttachment();
    }

    public void dispose() {
        glDeleteVertexArrays(this.vao);
        this.extractBrightFragsShader.dispose();
        this.downsampleShader.dispose();
        this.upsampleShader.dispose();
        this.gaussianBlurShader.dispose();
        this.bloomFramebuffer.dispose();
        this.utilityFramebuffer.dispose();
    }
}
