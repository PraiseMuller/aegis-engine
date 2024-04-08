package core.postprocess;

import core.renderer.ShaderProgram;
import core.renderer.Texture;
import core.utils.AssetPool;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static core.utils.SETTINGS.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ScreenQuad {
    private final int vao, vbo, ebo;
    private final ShaderProgram shaderProgram;

    public ScreenQuad(){
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/screen-quad/fin_vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/screen-quad/fin_fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.createUniform("screenTexture");
        this.shaderProgram.createUniform("bloomTexture");
        this.shaderProgram.createUniform("bloomOn");
        this.shaderProgram.createUniform("blackAndWhiteOn");
        this.shaderProgram.createUniform("colorInvert");
        this.shaderProgram.createUniform("gammaCorrect");
        this.shaderProgram.createUniform("hdrToneMap");

        float[] quadVertices = {
             1.0f, 1.0f,     1.0f, 1.0f,
            -1.0f, 1.0f,     0.0f, 1.0f,
            -1.0f,-1.0f,     0.0f, 0.0f,
             1.0f,-1.0f,     1.0f, 0.0f
        };
        FloatBuffer vertBuffer = MemoryUtil.memAllocFloat(quadVertices.length);
        vertBuffer.put(quadVertices).flip();

        int[] indices = {
                0, 3, 2,
                0, 2, 1
        };
        IntBuffer indBuffer = MemoryUtil.memAllocInt(indices.length);
        indBuffer.put(indices).flip();

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);   //pos
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES); //tex cords

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        MemoryUtil.memFree(indBuffer);
        MemoryUtil.memFree(vertBuffer);
    }

    public void render(Texture sceneTexture, Texture bloomTexture){
        this.shaderProgram.bind();

        sceneTexture.bind();
        this.shaderProgram.uploadIntUniform("screenTexture", sceneTexture.getBindLocation());

        bloomTexture.bind();
        this.shaderProgram.uploadIntUniform("bloomTexture", bloomTexture.getBindLocation());
        this.shaderProgram.uploadIntUniform("bloomOn", BLOOM_ON ? 1 : 0);
        this.shaderProgram.uploadIntUniform("blackAndWhiteOn", BLACK_AND_WHITE_ON ? 1 : 0);
        this.shaderProgram.uploadIntUniform("colorInvert", COLOR_INVERT ? 1 : 0);
        this.shaderProgram.uploadIntUniform("gammaCorrect", GAMMA_CORRECT ? 1 : 0);
        this.shaderProgram.uploadIntUniform("hdrToneMap", HDR_TONE_MAP ? 1 : 0);

        //Enable additive blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glBlendEquation(GL_FUNC_ADD);

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        sceneTexture.unbind();
        bloomTexture.unbind();

        // Disable additive blending
        glDisable(GL_BLEND);

        this.shaderProgram.unbind();
    }

    public void dispose(){
        this.shaderProgram.dispose();

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vbo);
        glDeleteBuffers(this.ebo);
    }
}
