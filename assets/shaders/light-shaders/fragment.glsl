#version 440 core

out vec4 color;

uniform vec3 fColor;
uniform float intensity;
uniform float isDirLight;

void main(){
    vec4 temp;
    if(isDirLight == 1)
        temp = vec4(fColor, 0.0f) * (intensity * 10);
    else
        temp = vec4(fColor, 0.0f) * (intensity < 2500.0f ? 1 : (intensity/2500.0f));

    color = temp;
}