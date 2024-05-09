package core.renderer.environment;

import core.engine.Scene;
import core.renderer.ShaderProgram;
import core.utils.AssetPool;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class CubeMap {

    private int vao = 0;
    private IntBuffer width, height, channel;
    private final int textureId;
    private final ShaderProgram shaderProgram;
    private final int BIND_LOCATION = 4;

    public CubeMap(){

        this.textureId = glGenTextures();
        glActiveTexture( GL_TEXTURE0 + BIND_LOCATION);
        glBindTexture(GL_TEXTURE_CUBE_MAP, this.textureId);

        //create the cube map's faces
        String[] faces = {
                "assets/images/skyboxes/sea/right.jpg",
                "assets/images/skyboxes/sea/left.jpg",
                "assets/images/skyboxes/sea/top.jpg",
                "assets/images/skyboxes/sea/bottom.jpg",
                "assets/images/skyboxes/sea/front.jpg",
                "assets/images/skyboxes/sea/back.jpg",
        };

        for (int i = 0; i < faces.length; i++){

            //load image texture
            ByteBuffer image = _loadImage(faces[i]);
            if(channel.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, this.width.get(0), this.height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }
            else if(channel.get(0)== 4) {
                glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, this.width.get(0), this.height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }

            stbi_image_free(image);
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

        //shader stuff
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/environment/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/environment/cubemap_fragment.glsl"));
        this.shaderProgram.link();
        this.shaderProgram.createUniform("cubemapTexture");
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.createUniform("camPos");
    }

    public void render(Scene scene){

        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", scene.camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix", scene.camera.viewMatrix());
        this.shaderProgram.uploadVec3fUniform("camPos", scene.camera.getPosition());

        glBindTexture(GL_TEXTURE_CUBE_MAP, this.textureId);
        this.shaderProgram.uploadIntUniform("cubemapTexture", BIND_LOCATION);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        _renderCube();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
        this.shaderProgram.unbind();
    }

    private void _renderCube(){

        if(this.vao == 0){
            float[] vertices = {
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,

                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,

                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,

                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,

                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
            };

            this.vao = glGenVertexArrays();
            glBindVertexArray(this.vao);

            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }

        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);

        glDrawArrays(GL_TRIANGLES, 0, 36);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    private ByteBuffer _loadImage(String texLocation){
        //LOAD IMAGE -> get rgb(a) data
        this.width = BufferUtils.createIntBuffer(1);
        this.height = BufferUtils.createIntBuffer(1);
        this.channel = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(false);
        ByteBuffer image = stbi_load(texLocation, width, height, channel, 0);

        if(image != null){
            return image;
        }
        else{
            throw new RuntimeException("Error:\nCould not load cube map image: " + texLocation);
        }
    }

    public void dispose(){

        glDeleteVertexArrays(this.vao);
        glDeleteTextures(this.textureId);
        this.shaderProgram.dispose();
        memFree(width);
        memFree(height);
        memFree(channel);
    }

}
