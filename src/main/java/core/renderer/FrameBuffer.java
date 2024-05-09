package core.renderer;

import static core.utils.SETTINGS.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {

    private final int fboId;
    private int rboId = 0;
    private final boolean hasRenderBuffer;
    private final Texture texture;

    public FrameBuffer(boolean hasRenderBuffer){

        this.fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);

        this.hasRenderBuffer = hasRenderBuffer;
        if(this.hasRenderBuffer) {

            this.rboId = glGenRenderbuffers();
            glBindRenderbuffer(GL_RENDERBUFFER, this.rboId);
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH32F_STENCIL8, WIN_WIDTH, WIN_HEIGHT);
            this.texture = new Texture(WIN_WIDTH, WIN_HEIGHT, 2, true);
        }
        else {
            this.texture = new Texture(WIN_WIDTH, WIN_HEIGHT, 3, true);
        }

        //attach frame buffer attachments
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);
        if(this.hasRenderBuffer)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboId);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("ERROR::FRAME-BUFFER::Frame buffer is not complete!");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        if(this.hasRenderBuffer)
            glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    public void changeTexture(Texture nTexture){
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, nTexture.getId(), 0);
    }

    public void defaultTexture() {
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);
    }

    public void bind(){

        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);

        if(hasRenderBuffer) {
            glEnable(GL_STENCIL_TEST);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_BACK);
        }
        else {
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);
        }

        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(BLACK.x, BLACK.y, BLACK.z, BLACK.w);
        glClear(GL_COLOR_BUFFER_BIT);
        if(hasRenderBuffer)
            glClear(GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void unbind(){

        if(hasRenderBuffer) {
            glDisable(GL_STENCIL_TEST);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getColorAttachment(){
        return this.texture;
    }

    public void dispose(){
        this.texture.dispose();
        glDeleteFramebuffers(this.fboId);
        if(hasRenderBuffer)
            glDeleteRenderbuffers(this.rboId);
    }
}
