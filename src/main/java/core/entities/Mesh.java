package core.entities;

import core.engine.Camera;
import core.renderer.ShaderProgram;
import core.utils.AssetPool;
import core.utils.Primitives;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int vao, ebo, vbo;
    private final ShaderProgram shaderProgram;

    public Mesh(){

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/player/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/player/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.bind();
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.createUniform("modelMatrix");
        this.shaderProgram.createUniform("fColor");
        this.shaderProgram.unbind();

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        //get shape
        FloatBuffer vertices = null;
        IntBuffer indices = null;
        for(Map.Entry<FloatBuffer, IntBuffer> element : Primitives.rectV().entrySet()){
            vertices = element.getKey();
            indices = element.getValue();
        }

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        //EBO
        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //Attrib pointer(s)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        MemoryUtil.memFree(vertices);
        MemoryUtil.memFree(indices);
    }

    public void update(Matrix4f modelMatrix) {
        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("modelMatrix", modelMatrix);
        this.shaderProgram.unbind();
    }

    public void draw(){

        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", Camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix", Camera.viewMatrix());
        this.shaderProgram.uploadVec4fUniform("fColor", new Vector4f(0.2f,0.6f,0.4f,0.4f));

        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        this.shaderProgram.unbind();
    }

    public void dispose(){
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vbo);
        glDeleteBuffers(this.ebo);

        this.shaderProgram.dispose();
    }
}
