#version 440 core

out vec4 color;
in vec2 fTextCoords;

uniform sampler2D screenTexture;
uniform sampler2D bloomTexture;
uniform int bloomOn;

//f(n) declarations
vec3 aces(vec3 aColor);

void main(){
    vec4 hdrColor = texture(screenTexture, fTextCoords);
    vec4 bloomColor = texture(bloomTexture, fTextCoords);

    //bloom is turned on
    if(bloomOn == 1)
        hdrColor += bloomColor;

    //tone mapping
    vec3 result = aces(hdrColor.rgb);

    //also gamma correct while we’re at it
    const float gamma = 2.2;
    result = pow(result, vec3(1.0 / gamma));

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