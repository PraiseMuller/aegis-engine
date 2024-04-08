package core.entities;

import core.engine.Scene;
import core.renderer.ShaderProgram;
import core.renderer.Window;
import core.utils.AssetPool;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Mesh {
    private int indicesSize = 0;
    private final int vao, vbo;
    private final ShaderProgram shaderProgram;
    private GameObject parentObject = null;
    private Material material = null;

    public Mesh(GameObject gameObject){

        this.parentObject = gameObject;
        this.material = new Material();

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/player/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/player/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("viewMatrix");
        this.shaderProgram.createUniform("modelMatrix");
        this.shaderProgram.createMaterialUniform("material");

        this.shaderProgram.createDirectionalLightUniform("directionalLight");
        for(int i = 0; i < 8; i++)
            this.shaderProgram.createPointLightUniform("pointLight["+i+"]");


        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        //Load model
        float[] floats = AssetPool.loadAiScece("D:\\Models\\Infinian lineage series\\source\\Mon_Infinian_001_Skeleton.FBX");
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

    public void draw(Scene scene){

        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("projectionMatrix", Window.currentCamera().projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("viewMatrix", Window.currentCamera().viewMatrix());
        this.shaderProgram.uploadMat4fUniform("modelMatrix", Window.currentCamera().modelMatrix(this.parentObject));
        this.shaderProgram.setUniform("material", this.material);
        this.shaderProgram.setUniform("directionalLight", scene.getDirectionalLight().toViewSpace( Window.currentCamera().viewMatrix()));

        for(int i = 0; i < 8; i++)
            this.shaderProgram.setUniform("pointLight["+i+"]", scene.getPointLights()[i].toViewSpace( Window.currentCamera().viewMatrix() ));

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
