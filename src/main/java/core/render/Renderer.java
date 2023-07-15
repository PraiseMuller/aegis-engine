package core.render;

import core.Window;
import core.c_spaces.Camera;
import core.c_spaces.Transformation;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.models.GameObject;
import core.utils.AssetPool;
import core.utils.Constants;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import static core.utils.Constants.CAMERA_INIT_POS;
import static core.utils.Constants.CAMERA_INIT_ROT;

public class Renderer {
    private Shader defaultShaderProgram, light_001_shaderProgram;
    private Matrix4f projectionMatrix;
    public Camera camera;
    private Transformation transformation;
    private PointLight pointLight;
    private DirectionalLight directionalLight;
    public Renderer(){
        init();
    }

    private void init(){
        this.camera = new Camera(CAMERA_INIT_POS, CAMERA_INIT_ROT);
        this.transformation = new Transformation();
        this.pointLight = new PointLight(Constants.POINT_LIGHT_01_COL, new Vector3f(0, 400, 300), 4);
        this.directionalLight = new DirectionalLight(new Vector3f(0.05f, 0.01f, 0.0f), new Vector3f(1,1,0), 1f);

        this.projectionMatrix = transformation.getProjectionMatrix(Constants.FOV, Window.getWidth(), Window.getHeight(), Constants.Z_NEAR, Constants.Z_FAR);

        //create shaders
        this.defaultShaderProgram = new Shader();
        this.light_001_shaderProgram = new Shader();

        this.defaultShaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/default_vertex_shader.glsl"));
        this.defaultShaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/default_fragment_shader.glsl"));
        this.light_001_shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/light-shaders/light_001_vertex_shader.glsl"));
        this.light_001_shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/light-shaders/light_001_fragment_shader.glsl"));

        this.defaultShaderProgram.link();
        this.light_001_shaderProgram.link();

        this.defaultShaderProgram.createUniform("uProjectionMatrix");
        this.defaultShaderProgram.createUniform("uModelMatrix");
        this.defaultShaderProgram.createUniform("textureSampler");
        this.defaultShaderProgram.createPointLightUniform("pointLight");
        this.defaultShaderProgram.createDirectionalLightUniform("directionalLight");
        this.defaultShaderProgram.createMaterialUniform("material");
        this.defaultShaderProgram.createUniform("ambientLight");
        this.defaultShaderProgram.createUniform("pointLightOn");

        this.light_001_shaderProgram.createUniform("uProjectionMatrix");
        this.light_001_shaderProgram.createUniform("uModelMatrix");
    }

    float x = 0f;
    public void update(List<GameObject> objects, List<GameObject> lights){
        this.defaultShaderProgram.bind();
        this.defaultShaderProgram.uploadMat4fUniform("uProjectionMatrix", this.projectionMatrix);
        this.defaultShaderProgram.uploadIntUniform("textureSampler", 0);
        this.defaultShaderProgram.uploadVec4fUniform("ambientLight", Constants.AMBIENT_LIGHT_COL);
        this.defaultShaderProgram.uploadFloatUniform("pointLightOn", 1);

        Matrix4f viewMatrix = transformation.getViewMatrix(this.camera);

        //change light pos to view space coordinates
        defaultShaderProgram.setUniform("pointLight", pointLightToViewSpace(viewMatrix));
        defaultShaderProgram.setUniform("directionalLight", directionalLightToViewSpace(viewMatrix));

        x += 1.3f; if(x > 360) x = 0;
        for(GameObject obj : objects){
            obj.setRotation(x, x, 0);
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(obj, viewMatrix);
            this.defaultShaderProgram.uploadMat4fUniform("uModelMatrix", modelViewMatrix);
            this.defaultShaderProgram.setUniform("material", obj.getMaterial());

            if(obj.hasTexture())    obj.bindMaterials();
            obj.render();
            if(obj.hasTexture())    obj.unbindMaterials();
        }

        this.defaultShaderProgram.unbind();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                                                                                        //
        //               SEPARATE LIGHT SHADER LOGIC -> Figure out a better way to do this later                  //
        //                                                                                                        //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        this.light_001_shaderProgram.bind();
        this.light_001_shaderProgram.uploadMat4fUniform("uProjectionMatrix", this.projectionMatrix);
        for(GameObject light : lights){
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(light, viewMatrix);
            this.light_001_shaderProgram.uploadMat4fUniform("uModelMatrix", modelViewMatrix);

            light.render();
        }
        this.light_001_shaderProgram.unbind();
    }

    public void cleanup(List<GameObject> objects, List<GameObject> lights){
        this.defaultShaderProgram.cleanup();
        this.light_001_shaderProgram.cleanup();
        for(GameObject obj : objects){
            if(obj.hasTexture())    obj.unbindMaterials();
            obj.cleanup();
        }
        for(GameObject obj : lights){
            obj.cleanup();
        }
    }

    private PointLight pointLightToViewSpace(Matrix4f viewMatrix){
        // Get a copy of the point light object and transform its position to view coordinates
        PointLight currPLight = new PointLight(this.pointLight);
        Vector4f aux = new Vector4f(currPLight.getPosition(), 1);
        aux.mul(viewMatrix);
        currPLight.setPosition(new Vector3f(aux.x, aux.y, aux.z));
        return currPLight;
    }
    private DirectionalLight directionalLightToViewSpace(Matrix4f viewMatrix){
        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));

        return currDirLight;
    }

}