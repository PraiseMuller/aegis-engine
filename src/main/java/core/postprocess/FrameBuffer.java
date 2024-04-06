package core.postprocess;

import core.renderer.Texture;

import static core.utils.SETTINGS.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private final int fbo_id, rbo_id;
    private final Texture texture;

    public FrameBuffer(){
        this.fbo_id = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo_id);

        this.rbo_id = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, this.rbo_id);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32F, WIN_WIDTH, WIN_HEIGHT);

        //attach frame buffer attachments
        this.texture = new Texture(WIN_WIDTH, WIN_HEIGHT, 2, true);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo_id);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("ERROR::FRAME-BUFFER::Frame buffer is not complete!");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind(){
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo_id);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(BLACK.x, BLACK.y, BLACK.z, BLACK.w);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public Texture getColorAttachment(){
        return this.texture;
    }

    public void dispose(){
        this.texture.dispose();
        glDeleteFramebuffers(this.fbo_id);
        glDeleteRenderbuffers(this.rbo_id);
    }
}
