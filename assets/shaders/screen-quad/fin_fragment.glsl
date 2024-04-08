#version 440 core

out vec4 color;
in vec2 fTextCoords;

uniform sampler2D screenTexture;
uniform sampler2D bloomTexture;
uniform int bloomOn;
uniform int blackAndWhiteOn;
uniform int colorInvert;
uniform int gammaCorrect;
uniform int hdrToneMap;

//f(n) declarations
vec3 aces(vec3 aColor);

void main(){
    vec4 hdrColor = texture(screenTexture, fTextCoords);
    vec4 bloomColor = texture(bloomTexture, fTextCoords);

    //bloom is turned on
    if(bloomOn == 1)
        hdrColor.rgb += bloomColor.rgb;

    //also gamma correct while we’re at it
    if(gammaCorrect == 1){
        const float gamma = 2.2;
        hdrColor.rgb = pow(hdrColor.rgb, vec3(1.0 / gamma));
    }

    vec3 result = hdrColor.rgb;

    //tone mapping
    if(hdrToneMap == 1)
        result = aces(result.rgb);

    if(blackAndWhiteOn == 1){
        float avg = (result.r + result.g + result.b) / 3.0f;
        result.rgb = vec3(avg, avg, avg);
    }

    if(colorInvert == 1){
        result = 1 - result;
    }

    color = vec4(result, hdrColor.w);
}


//ACES Filmic Tone Mapping
vec3 aces(vec3 aColor){
    const float a = 2.51f;
    const float b = 0.03f;
    const float c = 2.43f;
    const float d = 0.59f;
    const float e = 0.14f;

    return clamp((aColor * (a * aColor + b)) / (aColor * (c * aColor + d) + e), 0.0f, 1.0f);
}