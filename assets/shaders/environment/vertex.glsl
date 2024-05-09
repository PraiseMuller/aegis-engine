#version 440 core

layout (location = 0) in vec3 aPos;

out vec3 fPos;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 camPos;        //to make the cube's position move with the camera so the illusion isnt broken.

void main()
{
    fPos = aPos;
    gl_Position = projectionMatrix * viewMatrix * vec4(aPos + camPos, 1.0);
}