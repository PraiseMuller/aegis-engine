#version 440 core

in vec2 fTextCoords;
out vec3 color;

uniform sampler2D srcTexture;

void main(){

    vec3 tColor = texture(srcTexture, fTextCoords).rgb;

    // if fragment output is higher than threshold, output brightness color
    float brightness = dot(tColor.rgb, vec3(0.2126, 0.7152, 0.0722));
    if(brightness > 10.0f ){
        color = tColor;
    }
    else{
        color = vec3(0.0f, 0.0f, 0.0f);
    }
}