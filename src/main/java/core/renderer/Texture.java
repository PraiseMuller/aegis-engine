package core.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_R11F_G11F_B10F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Texture {
    private final int textureId, bindLocation;
    private IntBuffer width, height, channel;

    public Texture(int width, int height, int slot){
        this.bindLocation = slot;

        this.textureId = glGenTextures();
        glActiveTexture( GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, this.textureId);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Texture(int width, int height, int slot, boolean mipTexture){
        this.bindLocation = slot;

        this.textureId = glGenTextures();
        glActiveTexture( GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, this.textureId);

        // we are downscaling an HDR color buffer, so we need a float texture format
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R11F_G11F_B10F, width, height, 0, GL_RGB, GL_FLOAT, NULL);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Texture(String texLocation, int slot){
        this.bindLocation = slot;
        this.textureId = glGenTextures();
        glActiveTexture( GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, this.textureId);

        // set the texture wrapping/filtering options (on currently bound texture)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //load image texture
        ByteBuffer image = loadImage(texLocation);

        //generating a texture using the previously loaded image data
        if(channel.get(0) == 3) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
        }
        else if(channel.get(0) == 4) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        }
        else {
            throw new RuntimeException("Unsupported number of channels in image: \""+ texLocation +"\"");
        }

        //System.err.println("nChannels:  "+ channel.get(0));

        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(image);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind(){
        glActiveTexture( GL_TEXTURE0 + this.bindLocation);
        glBindTexture(GL_TEXTURE_2D, this.textureId);
    }

    public void unbind(){
        glActiveTexture( GL_TEXTURE0 + this.bindLocation);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getId(){
        return this.textureId;
    }

    public int getBindLocation(){
        return this.bindLocation;
    }

    public void dispose(){
        this.unbind();
        glDeleteTextures(this.textureId);
    }

    private ByteBuffer loadImage(String texLocation){
        //LOAD IMAGE -> get rgb(a) data
        width = BufferUtils.createIntBuffer(1);
        height = BufferUtils.createIntBuffer(1);
        channel = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(texLocation, width, height, channel, 0);

        if(image != null){
            return image;
        }
        else{
            throw new RuntimeException("Error, could not load image: '" + texLocation + "'\n");
        }
    }
}
