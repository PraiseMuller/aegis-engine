package core.postprocess;

import core.renderer.Texture;

import static core.utils.SETTINGS.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private final int fbo_id;
    private final Texture texture;

    public FrameBuffer(){
        this.fbo_id = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo_id);

        //attach frame buffer attachments
        this.texture = new Texture(WIN_WIDTH, WIN_HEIGHT, 2);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("ERROR::FRAME-BUFFER::Frame buffer is not complete!");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo_id);
        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(BLACK.x, BLACK.y, BLACK.z, BLACK.w);
        glClear(GL_COLOR_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public Texture getColorAttachment(){
        return this.texture;
    }

    public void dispose(){
        this.texture.dispose();
        glDeleteFramebuffers(this.fbo_id);
    }
}
