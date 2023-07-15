#version 330 core

layout (location = 0) in vec3 aPos;

uniform mat4 uProjectionMatrix;
uniform mat4 uModelMatrix;

void main(){
    gl_Position = uProjectionMatrix * uModelMatrix * vec4(aPos, 1.0f);
}