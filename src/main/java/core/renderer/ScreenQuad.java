package core.renderer;

import core.utils.AssetPool;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static core.utils.SETTINGS.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ScreenQuad {

    private int vao = 0;
    private final ShaderProgram shaderProgram;

    public ScreenQuad(){

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/screen-quad/fin_vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/screen-quad/fin_fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.createUniform("screenTexture");
        this.shaderProgram.createUniform("bloomTexture");
        this.shaderProgram.createUniform("lFlareTexture");
        this.shaderProgram.createUniform("blackAndWhiteOn");
        this.shaderProgram.createUniform("colorInvert");
        this.shaderProgram.createUniform("gammaCorrect");
        this.shaderProgram.createUniform("hdrToneMap");
    }

    public void render(Texture sceneTexture, Texture bloomTexture, Texture lFlareTexture){

        _defaultFramebuffer();
        this.shaderProgram.bind();

        sceneTexture.bind();
        this.shaderProgram.uploadIntUniform("screenTexture", sceneTexture.getBindLocation());
        bloomTexture.bind();
        this.shaderProgram.uploadIntUniform("bloomTexture", bloomTexture.getBindLocation());
        lFlareTexture.bind();
        this.shaderProgram.uploadIntUniform("lFlareTexture", lFlareTexture.getBindLocation());

        this.shaderProgram.uploadIntUniform("blackAndWhiteOn", BLACK_AND_WHITE_ON ? 1 : 0);
        this.shaderProgram.uploadIntUniform("colorInvert", COLOR_INVERT ? 1 : 0);
        this.shaderProgram.uploadIntUniform("gammaCorrect", GAMMA_CORRECT ? 1 : 0);
        this.shaderProgram.uploadIntUniform("hdrToneMap", HDR_TONE_MAP ? 1 : 0);

        glEnable(GL_BLEND);     //Enable additive blending
        glBlendFunc(GL_ONE, GL_ONE);
        glBlendEquation(GL_FUNC_ADD);
        _renderQuad();
        glDisable(GL_BLEND);    // Disable additive blending

        sceneTexture.unbind();
        bloomTexture.unbind();
        lFlareTexture.unbind();
        this.shaderProgram.unbind();
    }

    private void _renderQuad() {
        if (this.vao == 0) {
            this.vao = glGenVertexArrays();
            glBindVertexArray(this.vao);

            float[] quadVertices = {
                    1.0f,  1.0f,   1.0f, 1.0f,
                    -1.0f,  1.0f,   0.0f, 1.0f,
                    -1.0f, -1.0f,   0.0f, 0.0f,
                    1.0f, -1.0f,   1.0f, 0.0f
            };
            FloatBuffer vertBuffer = MemoryUtil.memAllocFloat(quadVertices.length);
            vertBuffer.put(quadVertices).flip();

            int[] indices = {
                    0, 3, 2,
                    0, 2, 1
            };
            IntBuffer indBuffer = MemoryUtil.memAllocInt(indices.length);
            indBuffer.put(indices).flip();

            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

            int ebo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indBuffer, GL_STATIC_DRAW);

            glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);   //pos
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES); //tex cords

            MemoryUtil.memFree(indBuffer);
            MemoryUtil.memFree(vertBuffer);
        }

        glBindVertexArray(this.vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
    private static void _defaultFramebuffer(){

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glViewport(0, 0, WIN_WIDTH, WIN_HEIGHT);
        glClearColor(BLACK.x, BLACK.y, BLACK.z, BLACK.w);
        glClear(GL_COLOR_BUFFER_BIT);
    }
    public void dispose(){
        this.shaderProgram.dispose();
        glDeleteVertexArrays(this.vao);
    }
}
