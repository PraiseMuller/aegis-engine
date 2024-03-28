package core.postprocess;

import core.renderer.ShaderProgram;
import core.utils.AssetPool;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Quad {
    private final int vao, vbo, ebo;
    private final ShaderProgram shaderProgram;

    public Quad(){
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/post-processing/bw_vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/post-processing/bw_fragment.glsl"));
        this.shaderProgram.link();

        //this.shaderProgram.createUniform("textureSampler");

        float[] quadVertices = {
             1.0f, 1.0f,     1.0f, 1.0f,
            -1.0f, 1.0f,     0.0f, 1.0f,
            -1.0f,-1.0f,     0.0f, 0.0f,
             1.0f,-1.0f,     1.0f, 0.0f
        };
        FloatBuffer vertBuffer = BufferUtils.createFloatBuffer(quadVertices.length);
        vertBuffer.put(quadVertices).flip();

        int[] indices = {
                0, 3, 2,
                0, 2, 1
        };
        IntBuffer indBuffer = BufferUtils.createIntBuffer(quadVertices.length);
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

        memFree(indBuffer);
        memFree(vertBuffer);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render(){
        this.shaderProgram.bind();

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

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
