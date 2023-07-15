package core.models;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String texLocation;
    private int textureId;
    IntBuffer width, height, channel;

    public Texture(String texLocation){

        this.texLocation = texLocation;

        this.textureId = glGenTextures();
        glActiveTexture(GL_TEXTURE0); // activate texture location first (0 = default)
        glBindTexture(GL_TEXTURE_2D, this.textureId);

        // set the texture wrapping/filtering options (on currently bound texture)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //load image texture
        ByteBuffer image = loadImage();

        //generating a texture using the previously loaded image data
        if(channel.get(0) == 3) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
        }
        else if(channel.get(0)== 4) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        }

        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(image);
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, this.textureId);
    }

    public void unbind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private ByteBuffer loadImage(){
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
            throw new RuntimeException("Error: '" + texLocation + "'\nCould not load image.");
        }
    }
}
