#version 440 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCords;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in ivec4 aBoneId;
layout (location = 4) in vec4 aWeight;

const int MAX_NUM_BONES = 200;

out vec2 fTexCords;
out vec3 fNormal;
out vec3 fPos;
//flat out ivec4 fBoneId;
//out vec4 fWeight;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
//uniform mat4 gBones[MAX_NUM_BONES];

void main(){

//    mat4 boneTransform = gBones[aBoneId[0]] * aWeight[0];
//    boneTransform     += gBones[aBoneId[1]] * aWeight[1];
//    boneTransform     += gBones[aBoneId[2]] * aWeight[2];
//    boneTransform     += gBones[aBoneId[3]] * aWeight[3];
//
//    vec4 posL = boneTransform * vec4(aPos, 1.0f);

//    fBoneId = aBoneId;
//    fWeight = aWeight;
    fNormal = vec4(viewMatrix * modelMatrix * vec4(aNormal, 0.0f)).xyz;
    fPos = vec4(viewMatrix * modelMatrix * vec4(aPos, 1.0f)).xyz;

    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0f);
}