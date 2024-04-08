#version 330 core

out vec4 color;

struct Material {
    vec3 color;
    float metallic;
    float roughness;
};

uniform Material material;
uniform float intensity;

void main(){
    color = vec4(material.color * intensity, 1.0f);
}