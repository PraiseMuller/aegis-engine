package core.render;

import core.utils.AssetPool;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class ShaderBatcher {
    private Map<String, Shader> shaders;

    public ShaderBatcher(){
        this.shaders = new HashMap<>();
    }
    public void addShader(String shaderName, String vertexShaderLocation, String fragmentShaderLocation){
        Shader shaderProgram = new Shader();
        shaderProgram.createVertexShader(AssetPool.getShader(vertexShaderLocation));
        shaderProgram.createFragmentShader(AssetPool.getShader(fragmentShaderLocation));
        shaderProgram.link();
        this.shaders.put(shaderName, shaderProgram);
    }

    public void createShaderUniform(String shaderName, String uniformName){
        Shader shader = this.shaders.get(shaderName);
        if(shader == null){
            throw new RuntimeException("The shader ("+shaderName+") you want to create the uniform ("+uniformName+") to wasn't found!");
        }
        shader.createUniform(uniformName);
    }

    public void createPointLightUniform(String shaderName, String pointLightName) {
        Shader shader = this.shaders.get(shaderName);
        if(shader == null){
            throw new RuntimeException("The shader ("+shaderName+") you want to create the point light ("+pointLightName+") to wasn't found!");
        }
        shader.createPointLightUniform(pointLightName);
    }

    public void createMaterialUniform(String shaderName, String materialName) {
        Shader shader = this.shaders.get(shaderName);
        if(shader == null){
            throw new RuntimeException("The shader ("+shaderName+") you want to create the material ("+materialName+") to wasn't found!");
        }
        shader.createMaterialUniform(materialName);
    }

    public void bindShaders(){
        for (Shader shader : this.shaders.values()){
            shader.bind();
        }
    }

    public void uploadMat4fUniform(String shaderName, String uniformName, Matrix4f uniformMatrix){
        Shader shader = this.shaders.get(shaderName);
        if(shader == null){
            throw new RuntimeException("The shader ("+shaderName+") you want to upload the uniform ("+uniformName+") to wasn't found!");
        }
        shader.uploadMat4fUniform(uniformName, uniformMatrix);
    }
}
