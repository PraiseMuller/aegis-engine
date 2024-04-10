#version 440 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCords;
layout (location = 2) in vec3 aNormal;

out vec2 fTexCords;
out vec3 fNormal;
out vec3 fPos;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main(){
    fNormal = vec4(viewMatrix * modelMatrix * vec4(aNormal, 0)).xyz;
    fPos = vec4(viewMatrix * modelMatrix * vec4(aPos, 1)).xyz;
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0f);
}