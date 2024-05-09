#version 440 core

in vec2 fTextCoords;
layout (location = 0) out vec4 color;

uniform sampler2D screenTexture;
uniform sampler2D bloomTexture;
uniform sampler2D lFlareTexture;

uniform int blackAndWhiteOn;
uniform int colorInvert;
uniform int gammaCorrect;
uniform int hdrToneMap;

//f(n) declarations
vec3 aces_tone_map(vec3 aColor);
vec3 reinhard_tone_map(vec3 aColor);

void main(){

    vec4 hdrColor = texture(screenTexture, fTextCoords);
    vec4 bloomColor = texture(bloomTexture, fTextCoords);
    vec4 lensFlareColor = texture(lFlareTexture, fTextCoords);
    hdrColor = (hdrColor + bloomColor + lensFlareColor);

    if(blackAndWhiteOn == 1){
        float avg = (hdrColor.r + hdrColor.g + hdrColor.b) / 3.0f;
        hdrColor.rgb = vec3(avg, avg, avg);
    }

    if(colorInvert == 1){
        hdrColor = 1 - hdrColor;
    }

    //gamma correction
    if(gammaCorrect == 1){
        const float gamma = 2.2f;
        hdrColor.rgb = pow(hdrColor.rgb, vec3(1.0f / gamma));
    }

    //tone mapping
    if(hdrToneMap == 1)
        hdrColor.rgb = aces_tone_map(hdrColor.rgb);

    color = hdrColor;
}


//Aces-filmic tone mapping algorithm
vec3 aces_tone_map(vec3 aColor){

    const float a = 2.51f;
    const float b = 0.03f;
    const float c = 2.43f;
    const float d = 0.59f;
    const float e = 0.14f;

    return clamp((aColor * (a * aColor + b)) / (aColor * (c * aColor + d) + e), 0.0f, 1.0f);
}

//Reinhard tone mapping algorithm
vec3 reinhard_tone_map(vec3 aColor) {
    return aColor / (aColor + vec3(1.0f));
}