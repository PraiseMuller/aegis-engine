#version 440 core

in vec2 fTextCoords;
layout(location = 0) out vec3 color;

uniform sampler2D mipTexture;
uniform sampler2D mipTexture01;

void main(){

    vec3 hdrCol = texture(mipTexture, fTextCoords).rgb;
         hdrCol += texture(mipTexture01, fTextCoords).rgb;

    color = hdrCol;
}