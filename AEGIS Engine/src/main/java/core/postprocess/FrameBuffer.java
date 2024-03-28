package core.postprocess;

import core.engine.Camera;
import core.renderer.ShaderProgram;
import core.renderer.Texture;
import core.utils.AssetPool;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static core.utils.SETTINGS.WIN_HEIGHT;
import static core.utils.SETTINGS.WIN_WIDTH;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class FrameBuffer {
//    private final int fbo_id;
//    private final int rbo_id;
//    private final Texture texture;
    private final int q_vao, q_vbo;
    private final ShaderProgram shaderProgram;

    public FrameBuffer(){

        //SETUP FRAME BUFFER STUFF
//        this.texture = new Texture(WIN_WIDTH, WIN_HEIGHT, 0);
//        this.texture.bind();
//
//        this.fbo_id = glGenFramebuffers();
//        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo_id);
//
//        this.rbo_id = glGenRenderbuffers();
//        glBindRenderbuffer(GL_RENDERBUFFER, rbo_id);
//        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, WIN_WIDTH, WIN_HEIGHT);
//
//        //attach frame buffer attachments
//        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);
//        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo_id);
//
//        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
//            throw new RuntimeException("ERROR::FRAME-BUFFER::Frame buffer is not complete!");
//
//        this.texture.unbind();
//
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        //SETUP DRAWING & SHADER STUFF
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/post-processing/bw_vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/post-processing/bw_fragment.glsl"));
        this.shaderProgram.link();

        //this.shaderProgram.createUniform("textureSampler");

        float[] quadVertices = {
                // positions    // texCoords
                -1.0f, 1.0f,    0.0f, 1.0f,
                -1.0f, -1.0f,   0.0f, 0.0f,
                1.0f, -1.0f,    1.0f, 0.0f,
                -1.0f, 1.0f,    0.0f, 1.0f,
                1.0f, -1.0f,    1.0f, 0.0f,
                1.0f, 1.0f,     1.0f, 1.0f
        };

        FloatBuffer f_quadVertices = MemoryUtil.memAllocFloat(quadVertices.length);
        f_quadVertices.put(quadVertices).flip();

        this.q_vao = glGenVertexArrays();
        glBindVertexArray(this.q_vao);

        //vertices positions
        this.q_vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.q_vbo);
        glBufferData(GL_ARRAY_BUFFER, f_quadVertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);   //pos
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES); //tex cords

        memFree(f_quadVertices);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void bind(){
//        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo_id);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        //glEnable(GL_DEPTH_TEST);
    }
    public void unbind(){
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        //glDisable(GL_DEPTH_TEST);   //disable depth to make sure tex quad is drawn on top of everything
    }

    public void renderColorAttachment(){
        this.shaderProgram.bind();

        //this.texture.bind();
        //this.shaderProgram.uploadIntUniform("textureSampler", this.texture.getBindLocation());

        glBindVertexArray(this.q_vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_TRIANGLES, 0, 6);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        //this.texture.unbind();
        this.shaderProgram.unbind();
    }

    public void dispose(){
//        this.shaderProgram.dispose();
//        this.texture.dispose();
//
//        glDisableVertexAttribArray(0);
//        glDisableVertexAttribArray(1);
//        glBindVertexArray(0);
//
//        glDeleteVertexArrays(this.q_vao);
//        glDeleteBuffers(this.q_vbo);
//        glDeleteFramebuffers(this.fbo_id);
//        glDeleteRenderbuffers(this.rbo_id);
    }
}
