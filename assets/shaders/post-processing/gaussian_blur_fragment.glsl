#version 440 core

in vec2 fTextCoords;
out vec3 color;

uniform sampler2D srcTexture;
uniform int horizontal;

const float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
//const float weight[9] = float[] (0.045, 0.062, 0.045, 0.062, 0.087, 0.062, 0.045, 0.062, 0.045);

void main(){

    vec2 tex_offset = 1.0 / textureSize(srcTexture, 0); // size of single texel
    vec3 result = texture(srcTexture, fTextCoords).rgb * weight[0]; // this fragment

    if(horizontal == 1) {

        for(int i = 1; i < 5; ++i) {
            result += texture(srcTexture, fTextCoords + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
            result += texture(srcTexture, fTextCoords - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
        }
    }
    if(horizontal == 0) {

        for(int i = 1; i < 5; ++i) {
            result += texture(srcTexture, fTextCoords + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
            result += texture(srcTexture, fTextCoords - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
        }
    }

    color = result;
}
