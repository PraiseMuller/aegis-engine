#version 440 core

//  This fragment shader was implemented as written and explained
//  by John-Chapman on his blog on generating Pseudo Lens Flare image artifacts.
//  copyrights john-chapman-graphics 22/02/2013

in vec2 fTextCoords;
layout (location = 0) out vec3 color;

uniform sampler2D srcTexture;

const int uGhosts = 3;
const float uGhostDispersal = 0.3f;
const float uDistortion = 8.0f;

//fn definitions
vec3 chromatic_aberration(sampler2D tex, vec2 texCord, vec2 direction, vec3 distortion);

void main(){

    vec2 textCord = -fTextCoords + vec2(1.0f);
    vec2 texelSize = 1.0f / vec2(textureSize(srcTexture, 0));
    vec3 distortion = vec3(-texelSize.x * uDistortion, 0.0f, texelSize.x * uDistortion);

    //ghost vector to image center
    vec2 ghostVec = (vec2(0.5f) - textCord) * uGhostDispersal;
    vec2 direction = normalize(ghostVec);

    //sample ghosts
    vec3 result = vec3(0.0f);
    for(int i = 0; i < uGhosts; ++i){

        vec2 offset = fract(textCord + ghostVec * float(i));
        float weight = length(vec2(0.5f) - offset) / length(vec2(0.5f));
        weight = pow(1.0f - weight, 10.0f) * 0.002f;

        result += chromatic_aberration(srcTexture, offset, direction, distortion) * weight;
    }

    //sample halo ring(s)
    vec2 haloVec = normalize(ghostVec) * 0.6f; // -> Halo width
    float weight = length(vec2(0.5f) - fract(textCord + haloVec)) / length(vec2(0.5f));
    weight = pow(1.0f - weight, 10.0f) * 0.0003;
    result += chromatic_aberration(srcTexture, textCord + haloVec, direction, distortion) * weight;

    color = result;
}

vec3 chromatic_aberration(sampler2D tex, vec2 texCord, vec2 direction, vec3 distortion){

    return vec3(
        texture(tex, texCord + direction * distortion.r).r,
        texture(tex, texCord + direction * distortion.g).g,
        texture(tex, texCord + direction * distortion.b).b
    );
}