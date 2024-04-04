#version 440 core

layout (location = 0) in vec2 aPos;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main(){
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, -1.0f, 1.0f);
}