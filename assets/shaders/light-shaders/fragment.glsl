#version 440 core

out vec4 color;

uniform vec3 fColor;
uniform float intensity;
uniform int isDirLight;

void main(){

    vec4 temp;
    if(isDirLight == 1)
        temp = vec4(fColor, 1.0f) * intensity * 20;
    else
        temp = vec4(fColor, 1.0f) * intensity / 150;

    color = temp;
}