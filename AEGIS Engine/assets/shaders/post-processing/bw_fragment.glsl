#version 440 core

in vec2 fTextCoords;
out vec4 color;

//uniform sampler2D textureSampler;

void main(){
//    vec4 tc = texture(textureSampler, fTextCoords);
//
//    //float avg = (0.2126 * tc.r + 0.7152 * tc.g + 0.0722 * tc.b) / 3;
//    float avg = (tc.r + tc.g + tc.b) / 3;
//    color = vec4(avg, avg, avg, 1.0f);

    color = vec4(1,0,0,0);
}