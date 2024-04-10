package core.entities;

import core.engine.Scene;
import core.renderer.ShaderProgram;
import core.utils.AssetPool;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Mesh {

    private int indicesSize = 0;
    private int vao;
    private int vbo;
    private ShaderProgram shaderProgram;
    private Material material = null;

    public Mesh(String modelFileLocation, Vector3f color){
        this.material = new Material(color);
        this.init(modelFileLocation);
    }

    public Mesh(String modelFileLocation){
        this.material = new Material();
        this.init(modelFileLocation);
    }

    private void init(String modelFileLocation){
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/base/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/base/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.createUniform("modelMatrix");
        this.shaderProgram.createMaterialUniform("material");
        this.shaderProgram.createDirectionalLightUniform("directionalLight");
        for(int i = 0; i < 8; i++) {
            this.shaderProgram.createPointLightUniform("pointLight[" + i + "]");
        }

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        //Load model
        float[] floats = AssetPool.loadAiScece(modelFileLocation);
        FloatBuffer modelVertices = MemoryUtil.memAllocFloat(floats.length);
        modelVertices.put(floats).flip();

        this.indicesSize = floats.length / 8;

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, modelVertices, GL_STATIC_DRAW);

        //Attrib pointer(s)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        memFree(modelVertices);
    }

    public void render(Scene scene, GameObject gameObject){

        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", scene.camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix",scene.camera.viewMatrix());
        this.shaderProgram.uploadMat4fUniform("modelMatrix", scene.camera.modelMatrix(gameObject));
        this.shaderProgram.setUniform("material", this.material);
        this.shaderProgram.setUniform("directionalLight", scene.getDirectionalLight().toViewSpace( scene.camera.viewMatrix()));
        for(int i = 0; i < 8; i++)
            this.shaderProgram.setUniform("pointLight["+ i +"]", scene.getPointLights().get(i));

        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawArrays(GL_TRIANGLES, 0, this.indicesSize);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);

        this.shaderProgram.unbind();
    }

    public void dispose(){
        this.material.dispose();

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glDeleteVertexArrays(this.vao);
        glDeleteBuffers(this.vbo);
        //glDeleteBuffers(this.ebo);

        this.shaderProgram.dispose();
    }

    public Material getMaterial(){
        return this.material;
    }
}
