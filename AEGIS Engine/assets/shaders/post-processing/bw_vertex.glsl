#version 440 core

layout (location = 0) vec2 aPos;
layout (location = 1) vec2 texCoords;

out vec2 fTextCoords;

void main(){
    fTextCoords = texCoords;
    gl_Position = vec4(aPos.x, aPos.y, 0.0f, 1.0f);
}