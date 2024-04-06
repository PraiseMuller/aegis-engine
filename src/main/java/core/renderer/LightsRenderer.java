//package core.renderer;
//
//import core.entities.GameObject;
//import core.entities.Material;
//import core.lighting.DirectionalLight;
//import core.lighting.PointLight;
//import core.utils.AssetPool;
//import org.joml.Vector3f;
//import org.lwjgl.system.MemoryUtil;
//
//import java.nio.FloatBuffer;
//
//import static core.utils.SETTINGS.P_LIGHT_INTENSITY;
//import static org.lwjgl.opengl.GL11.GL_FLOAT;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
//import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
//import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.system.MemoryUtil.memFree;
//
//public class LightsRenderer {
//    private final int vao, vbo;
//    private int indicesSize = 0;
//    private final ShaderProgram shaderProgram;
//    PointLight[] pointLights;
//    DirectionalLight directionalLight;
//
//    public LightsRenderer(PointLight[] pointLights, DirectionalLight directionalLight){
//
//        this.pointLights = pointLights;
//        this.directionalLight = directionalLight;
//
//        this.shaderProgram = new ShaderProgram();
//        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/light-shaders/vertex.glsl"));
//        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/light-shaders/fragment.glsl"));
//        this.shaderProgram.link();
//
//        this.shaderProgram.createUniform("uProjection");
//        this.shaderProgram.createUniform("uView");
//        this.shaderProgram.createUniform("uModel");
//        this.shaderProgram.createMaterialUniform("material");
//        this.shaderProgram.createUniform("intensity");
//
//        this.vao = glGenVertexArrays();
//        glBindVertexArray(this.vao);
//
//        //get shape
//        float[] floats = AssetPool.loadAiScece("D:\\LEET-CODE\\RAY-TRACING-ENGINE\\assets\\3d-models\\light.obj");
//        FloatBuffer modelVertices = MemoryUtil.memAllocFloat(floats.length);
//        modelVertices.put(floats).flip();
//
//        this.indicesSize = floats.length / 8;
//
//        this.vbo = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
//        glBufferData(GL_ARRAY_BUFFER, modelVertices, GL_STATIC_DRAW);
//
//        //Attrib pointer(s)
//        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
//
//        glBindVertexArray(0);
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
//
//        memFree(modelVertices);
//    }
//
//    public void render() {
//        this.shaderProgram.bind();
//        this.shaderProgram.uploadMat4fUniform("uProjection", Window.currentCamera().projectionMatrix());
//        this.shaderProgram.uploadMat4fUniform("uView", Window.currentCamera().viewMatrix());
//
//        for (int i = 0; i < this.pointLights.length; i++){
//            GameObject obj = new GameObject(pointLights[i].getPosition(), new Vector3f(0), new Vector3f(0.2f));
//            Material material = new Material(pointLights[i].getColor());
//
//            this.shaderProgram.uploadMat4fUniform("uModel", Window.currentCamera().modelMatrix(obj));
//            this.shaderProgram.setUniform("material", material);
//            this.shaderProgram.uploadFloatUniform("intensity", P_LIGHT_INTENSITY);
//
//            glBindVertexArray(this.vao);
//            glEnableVertexAttribArray(0);
//            glEnableVertexAttribArray(1);
//            glEnableVertexAttribArray(2);
//
//            glDrawArrays(GL_TRIANGLES, 0, this.indicesSize);
//
//            glDisableVertexAttribArray(0);
//            glDisableVertexAttribArray(1);
//            glDisableVertexAttribArray(2);
//            glBindVertexArray(0);
//        }
//
//
//
//        this.shaderProgram.unbind();
//    }
//
//    public void dispose(){
//        this.shaderProgram.dispose();
//        glDeleteVertexArrays(this.vao);
//        glDeleteBuffers(this.vbo);
//    }
//}
