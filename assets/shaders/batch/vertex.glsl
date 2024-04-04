#version 440 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aCol;

out vec4 fCol;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){
    fCol = aCol;
    gl_Position = projectionMatrix * viewMatrix * vec4(aPos, 1.0f);
}