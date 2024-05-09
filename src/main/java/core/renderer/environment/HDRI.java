package core.renderer.environment;

import core.engine.Camera;
import core.renderer.ShaderProgram;
import core.renderer.Window;
import core.utils.AssetPool;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static core.utils.SETTINGS.WIN_HEIGHT;
import static core.utils.SETTINGS.WIN_WIDTH;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memFree;

public class HDRI {

    private final int hdrTexture;
    private int cube_vao = 0;
    private final ShaderProgram equiractangleToCubeMapShader;
    private final Matrix4f captureProjection;
    private final Matrix4f[] captureViews = {
            new Matrix4f().identity().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f( 1.0f, 0.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
            new Matrix4f().identity().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
            new Matrix4f().identity().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f( 0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f)),
            new Matrix4f().identity().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f( 0.0f, -1.0f, 0.0f), new Vector3f(0.0f, 0.0f, -1.0f)),
            new Matrix4f().identity().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f( 0.0f, 0.0f, 1.0f), new Vector3f(0.0f, -1.0f, 0.0f)),
            new Matrix4f().identity().lookAt(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f( 0.0f, 0.0f, -1.0f), new Vector3f(0.0f, -1.0f, 0.0f))
    };

    private final int captureFBO, envCubemap;

    public HDRI(String location){

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer nChannel = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);

        FloatBuffer data = stbi_loadf(location, width, height, nChannel, 0);

        if(data != null){
            hdrTexture = glGenTextures();
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, hdrTexture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB16F, width.get(0), height.get(0), 0, GL_RGB, GL_FLOAT, data);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glBindTexture(GL_TEXTURE_2D, 0);
            stbi_image_free(data);
        }
        else{
            throw new RuntimeException("Failed to load HDR Image: " + location);
        }


        //Create all the necessary shader programs here...
        equiractangleToCubeMapShader = new ShaderProgram();
        equiractangleToCubeMapShader.createVertexShader(AssetPool.getShader("assets/shaders/hdri/vertex.glsl"));
        equiractangleToCubeMapShader.createFragmentShader(AssetPool.getShader("assets/shaders/hdri/equirectangleToCubeMap.glsl"));
        equiractangleToCubeMapShader.link();
        equiractangleToCubeMapShader.createUniform("projectionMatrix");
        equiractangleToCubeMapShader.createUniform("viewMatrix");
        equiractangleToCubeMapShader.createUniform("equirectangularMap");

        //Create FBO to render to
        captureFBO = glGenFramebuffers();
        int captureRBO = glGenRenderbuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        glBindRenderbuffer(GL_RENDERBUFFER, captureRBO);

        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, (int)WIN_WIDTH, (int)WIN_HEIGHT);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, captureRBO);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("ERROR::FRAME-BUFFER:: Frame buffer is not complete!");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        //cube map to render to and attach to fbo
        envCubemap = glGenTextures();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, envCubemap);
        for (int i = 0; i < 6; ++i)
        {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB16F, (int)WIN_WIDTH, (int)WIN_HEIGHT, 0, GL_RGB, GL_FLOAT, NULL);
        }
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

        captureProjection = new Matrix4f().identity().perspective((float)Math.toRadians(90.0f), 1.0f, 0.1f, 10.0f);

        // pbr: convert HDR equirectangular environment map to cubemap equivalent
        // ----------------------------------------------------------------------
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, hdrTexture);

        equiractangleToCubeMapShader.bind();
        equiractangleToCubeMapShader.uploadIntUniform("equirectangularMap", GL_TEXTURE0);
        equiractangleToCubeMapShader.uploadMat4fUniform("uProjection", captureProjection);

        glViewport(0, 0, (int)WIN_WIDTH, (int)WIN_HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, captureFBO);
        for (int i = 0; i < 6; ++i)
        {
            equiractangleToCubeMapShader.uploadMat4fUniform("uView", captureViews[i]);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, envCubemap, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            _renderCube();
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        equiractangleToCubeMapShader.unbind();

        // then before rendering, configure the viewport to the original framebuffer's screen dimensions
        IntBuffer scrWidth = BufferUtils.createIntBuffer(1);
        IntBuffer scrHeight = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(Window.getWindow(), scrWidth, scrHeight);
        glViewport(0, 0, scrWidth.get(0), scrHeight.get(0));
    }

    public void draw(Camera camera){

        // render skybox (render as last to prevent overdraw)
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, envCubemap);

//        backgroundShader.bind();
//        backgroundShader.uploadMat4fUniform("uView", camera.viewMatrix());
//        backgroundShader.uploadMat4fUniform("uProjection", camera.projectionMatrix());
//        backgroundShader.uploadIntUniform("environmentMap", GL_TEXTURE1);
//        _renderCube();
//        backgroundShader.unbind();

        glBindTexture(GL_TEXTURE_CUBE_MAP, 0);

        /*                equiractangleShader.bind();
                equiractangleShader.uploadMat4fUniform("uView", view);
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, hdrTexture);
                drawCube();
                glBindTexture(GL_TEXTURE_2D, 0);
                equiractangleShader.bind();*/
    }

    public void dispose(){
        equiractangleToCubeMapShader.dispose();
        glDeleteVertexArrays(cube_vao);
        glDeleteFramebuffers(captureFBO);
        glDeleteTextures(hdrTexture);
    }

    private void _renderCube(){
        //initialize vao if necessary
        if(cube_vao == 0){
            float[] vertices = {
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, -0.5f,
                    0.5f, -0.5f, 0.5f,
                    0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, 0.5f,
                    -0.5f, -0.5f, -0.5f,
                    -0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, -0.5f,
                    0.5f, 0.5f, 0.5f,
                    0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, 0.5f,
                    -0.5f, 0.5f, -0.5f
            };

            FloatBuffer fvertices = MemoryUtil.memAllocFloat(vertices.length);
            fvertices.put(vertices).flip();

            cube_vao = glGenVertexArrays();
            glBindVertexArray(cube_vao);

            int vbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, fvertices, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            memFree(fvertices);
        }

        //the actual drawing
        glBindVertexArray(cube_vao);
        glEnableVertexAttribArray(0);

        glDrawArrays(GL_TRIANGLES, 0, 36);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}
