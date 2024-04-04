#version 440 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTextCoords;

out vec2 fTextCoords;

void main(){
    fTextCoords = aTextCoords;
    gl_Position = vec4(aPos, -1.0f, 1.0f);
}