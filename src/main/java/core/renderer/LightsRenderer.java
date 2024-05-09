package core.renderer;

import core.engine.Scene;
import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.utils.AssetPool;
import org.joml.Vector3f;

import java.util.ArrayList;

import static core.utils.SETTINGS.D_LIGHT_INTENSITY;
import static core.utils.SETTINGS.P_LIGHT_INTENSITY;

public class LightsRenderer {

    private final ShaderProgram shaderProgram;
    public final ArrayList<PointLight> pointLights;
    public final DirectionalLight directionalLight;

    public LightsRenderer(){

        int n = 100;
        this.pointLights = new ArrayList<>();
        this.pointLights.add(new PointLight(new Vector3f(1.0f, 0.5f, 0.5f), new Vector3f(n, n, n), P_LIGHT_INTENSITY));
        this.pointLights.add(new PointLight(new Vector3f(0.5f, 0.5f, 1.0f), new Vector3f(-n, n, -n), P_LIGHT_INTENSITY));
        this.pointLights.add(new PointLight(new Vector3f(1.0f), new Vector3f(n, n, -n), P_LIGHT_INTENSITY));
        this.pointLights.add(new PointLight(new Vector3f(1.0f), new Vector3f(-n, n, n), P_LIGHT_INTENSITY));

        this.directionalLight = new DirectionalLight(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1, 1, -1), D_LIGHT_INTENSITY);

        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(AssetPool.getShader("assets/shaders/light-shaders/vertex.glsl"));
        this.shaderProgram.createFragmentShader(AssetPool.getShader("assets/shaders/light-shaders/fragment.glsl"));
        this.shaderProgram.link();

        this.shaderProgram.createUniform("uProjection");
        this.shaderProgram.createUniform("uView");
        this.shaderProgram.createUniform("uModel");
        this.shaderProgram.createUniform("fColor");
        this.shaderProgram.createUniform("intensity");
        this.shaderProgram.createUniform("isDirLight");
    }

    public void render(Scene scene) {

        this.shaderProgram.bind();
        this.shaderProgram.uploadMat4fUniform("uProjection",scene.camera.projectionMatrix());
        this.shaderProgram.uploadMat4fUniform("uView", scene.camera.viewMatrix() );

        //draw point lights
        for (PointLight pointLight : this.pointLights) {
            this.shaderProgram.uploadMat4fUniform("uModel", scene.camera.modelMatrix(  pointLight  ));
            this.shaderProgram.uploadVec3fUniform("fColor", pointLight.getColor());
            this.shaderProgram.uploadFloatUniform("intensity", pointLight.getIntensity());
            this.shaderProgram.uploadIntUniform("isDirLight", 0);

            pointLight.render();
        }

        //draw directional light
        this.shaderProgram.uploadMat4fUniform("uModel",scene.camera.modelMatrix(  directionalLight ));
        this.shaderProgram.uploadVec3fUniform("fColor", directionalLight.getColor());
        this.shaderProgram.uploadFloatUniform("intensity", directionalLight.getIntensity());
        this.shaderProgram.uploadIntUniform("isDirLight", 1);

        this.directionalLight.render();


        this.shaderProgram.unbind();
    }

    public void dispose(){
        this.shaderProgram.dispose();
        this.directionalLight.dispose();
        for(PointLight pointLight : this.pointLights){
            pointLight.dispose();
        }
    }
}
