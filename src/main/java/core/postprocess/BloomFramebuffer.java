package core.postprocess;

import core.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;

import static core.utils.SETTINGS.BLACK;
import static org.lwjgl.opengl.GL30.*;

public class BloomFramebuffer {
    private int fbo;
    private final ArrayList<Mip> mipChain;

    public BloomFramebuffer(int windowWidth, int windowHeight, int mipChainLength){

        this.mipChain = new ArrayList<>();
        Vector2f mipSize = new Vector2f((float)windowWidth, (float)windowHeight);

        for (int i = 0; i < mipChainLength; i++) {

            Mip mip = new Mip();
            mipSize.mul(0.5f);
            mip.size = new Vector2f(mipSize);
            mip.texture = new Texture((int)mip.size.x, (int)mip.size.y, 1, true);

            this.mipChain.add(mip);
            //this.mipChain.add(0, mip);
        }

        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mipChain.get(0).texture.getId(), 0);

        // setup attachments
        int[] attachments = { GL_COLOR_ATTACHMENT0 };
        glDrawBuffers(attachments);

        // check completion status
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("ERROR::FRAME-BUFFER::Frame buffer is not complete!");


        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        glClearColor(BLACK.x, BLACK.y, BLACK.z, BLACK.w);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public ArrayList<Mip> getMipChain() {
        return this.mipChain;
    }

    public void dispose(){
        for(Mip mip : this.mipChain){
            mip.texture.dispose();
        }

        glDeleteFramebuffers(this.fbo);
    }
}
