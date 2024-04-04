#version 440 core

in vec2 fTextCoords;
out vec4 color;

uniform sampler2D mipTexture;
uniform sampler2D mipTexture01;

void main(){
    vec4 hdrCol = texture(mipTexture, fTextCoords);
    hdrCol += texture(mipTexture01, fTextCoords);

    color = hdrCol;
}