package core.render;

import core.lighting.DirectionalLight;
import core.lighting.PointLight;
import core.models.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private final Map<String, Integer> uniformLocations;

    public Shader(){
        this.uniformLocations = new HashMap<>();

        this.programId = glCreateProgram();
        if(this.programId == 0){
            throw new RuntimeException("Failed to create Shader program.");
        }
    }

    public void createVertexShader(String shaderSource){
        this.vertexShaderId = glCreateShader(GL_VERTEX_SHADER);
        compileShader("Vertex_Shader", this.vertexShaderId, shaderSource);
    }
    public void createFragmentShader(String shaderSource){
        this.fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER);
        compileShader("Fragment_Shader", this.fragmentShaderId, shaderSource);
    }
    private void compileShader(String shaderName, int shaderId, String shaderSource){
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);

        int success = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int length = glGetShaderi(shaderId, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("Failed to compile "+shaderName+" Shader: \n" + glGetShaderInfoLog(shaderId, length) + "\n" + shaderSource);
        }
    }
    public void link(){
        glAttachShader(this.programId, this.vertexShaderId);
        glAttachShader(this.programId, this.fragmentShaderId);
        glLinkProgram(this.programId);

        int success = glGetProgrami(this.programId, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int length = glGetShaderi(this.programId, GL_INFO_LOG_LENGTH);
            throw new RuntimeException("Shader linking failed: \n" + glGetProgramInfoLog(this.programId, length));
        }

        if (this.vertexShaderId != 0) {
            glDetachShader(programId, this.vertexShaderId);
        }

        if (this.fragmentShaderId != 0) {
            glDetachShader(programId, this.fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning: Validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }
    public void bind(){
        glUseProgram(this.programId);
    }
    public void unbind(){
        glUseProgram(0);
    }
    public void cleanup(){
        unbind();
        if(this.programId != 0){
            glDeleteProgram(this.programId);
        }
    }

    public void createUniform(String uniformName){
        int uniformLocation = glGetUniformLocation(this.programId, uniformName);

        if(uniformLocation < 0){
            throw new RuntimeException("Could not find uniform: " + uniformName);
        }

        this.uniformLocations.put(uniformName, uniformLocation);
    }

    public void createPointLightUniform(String uniformName){
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".attenuation.constant");
        createUniform(uniformName + ".attenuation.linear");
        createUniform(uniformName + ".attenuation.exponent");
    }

    public void createDirectionalLightUniform(String uniformName){
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createMaterialUniform(String uniformName){
        createUniform(uniformName + ".color");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
        createUniform(uniformName + ".specularPower");
    }

    public void setUniform(String uniformName, PointLight pointLight){
        uploadVec3fUniform(uniformName + ".color", pointLight.getColor());
        uploadVec3fUniform(uniformName + ".position", pointLight.getPosition());
        uploadFloatUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        uploadFloatUniform(uniformName + ".attenuation.constant", att.getConstant());
        uploadFloatUniform(uniformName + ".attenuation.linear", att.getLinear());
        uploadFloatUniform(uniformName + ".attenuation.exponent", att.getExponent());
    }

    public void setUniform(String uniformName, DirectionalLight directionalLight){
        uploadVec3fUniform(uniformName + ".color", directionalLight.getColor());
        uploadVec3fUniform(uniformName + ".direction", directionalLight.getDirection());
        uploadFloatUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    public void setUniform(String uniformName, Material material){
        uploadVec4fUniform(uniformName + ".color", material.getColor());
        uploadIntUniform(uniformName + ".hasTexture", material.getTexture() != null ? 1 : 0);
        uploadFloatUniform(uniformName + ".reflectance", material.getReflectance());
        uploadFloatUniform(uniformName + ".specularPower", material.getSpecularPower());
    }

    public void uploadMat4fUniform(String uniformName, Matrix4f value){
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer matBuffer = stack.mallocFloat(16);
            value.get(matBuffer);
            glUniformMatrix4fv(this.uniformLocations.get(uniformName), false, matBuffer);
        }
    }

    public void uploadVec4fUniform(String uniformName, Vector4f vec4){
        glUniform4f(this.uniformLocations.get(uniformName), vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void uploadVec3fUniform(String uniformName, Vector3f vec3){
        glUniform3f(this.uniformLocations.get(uniformName), vec3.x, vec3.y, vec3.z);
    }

    public void uploadIntUniform(String uniformName, int value) {
        glUniform1i(this.uniformLocations.get(uniformName), value);
    }

    public void uploadFloatUniform(String uniformName, float value) {
        glUniform1f(this.uniformLocations.get(uniformName), value);
    }
}
