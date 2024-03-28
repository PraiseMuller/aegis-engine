package core.entities;

import core.engine.Camera;
import core.renderer.ShaderProgram;
import core.utils.AssetPool;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int vao, ebo, vbo;
    private final int elementsCount;
    private final ShaderProgram shaderProgram;

    public Mesh(Vector3f pos, Vector4f col, float size){

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/defaults/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/defaults/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.bind();
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.unbind();

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        float[] vertices = this.getVertices(pos, col, size);
        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        //EBO
        int[] indices = generateIndices(1);
        this.elementsCount = indices.length;

        this.ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //Attrib pointer(s)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 7 * Float.BYTES, 0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 7 * Float.BYTES, 3 * Float.BYTES);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void update(Vector3f pos, Vector4f col, float size) {
        // Update the VBO data for the specified cell
        float[] vertices = this.getVertices(pos, col, size);

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }


    private int[] generateIndices(int len){
        int[] elements = new int[6 * len];
        for(int i = 0; i < len; i++){
            int offSetArrayIndex = 6 * i;
            int offset = 4 * i;

            //triangle 1
            elements[offSetArrayIndex]     = offset + 3;
            elements[offSetArrayIndex + 1] = offset + 2;
            elements[offSetArrayIndex + 2] = offset + 0;

            //triangle 2
            elements[offSetArrayIndex + 3] = offset + 0;
            elements[offSetArrayIndex + 4] = offset + 2;
            elements[offSetArrayIndex + 5] = offset + 1;
        }
        return elements;
    }

    private float[] getVertices(Vector3f pos, Vector4f color, float size){
        return new float[]{
            pos.x,         pos.y,        -1.0f,     color.x, color.y, color.z, color.w,
            pos.x + size,  pos.y,        -1.0f,     color.x, color.y, color.z, color.w,
            pos.x + size,  pos.y + size, -1.0f,     color.x, color.y, color.z, color.w,
            pos.x,         pos.y + size, -1.0f,     color.x, color.y, color.z, color.w,
        };
    }

    public void draw(){

        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", Camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix", Camera.viewMatrix());

        glBindVertexArray(this.vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.elementsCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
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
