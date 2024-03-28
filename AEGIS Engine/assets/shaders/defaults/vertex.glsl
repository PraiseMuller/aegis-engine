#version 440 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;

out vec4 fColor;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){
    fColor = aColor;
    gl_Position = projectionMatrix * viewMatrix * vec4(aPos, 1.0f);
}