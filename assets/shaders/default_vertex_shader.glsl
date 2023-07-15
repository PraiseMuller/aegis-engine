#version 330 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aNormal;

out vec2 fTexCoords;
out vec3 vertexNormal;
out vec3 vertexPos;

uniform mat4 uProjectionMatrix;
uniform mat4 uModelMatrix;

void main(){

    fTexCoords = aTexCoords;
    vertexNormal = normalize(uModelMatrix * vec4(aNormal, 0.0)).xyz;

    vec4 temp  = uModelMatrix * vec4(aPos, 1.0);
    vertexPos = temp.xyz;

    gl_Position = uProjectionMatrix * temp;
}