#version 440 core

in vec3 fPos; // direction vector: a 3D texture coordinate
out vec4 color;

uniform samplerCube cubemapTexture; // cubemap texture sampler

void main() {

    color = pow(texture(cubemapTexture, fPos), vec4(2.2f));
}