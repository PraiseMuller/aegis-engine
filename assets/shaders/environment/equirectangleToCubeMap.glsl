#version 440 core

out vec4 color;
in vec3 fPos;

uniform sampler2D equirectangularMap;
const vec2 invAtan = vec2(0.1591, 0.3183);

//f(n) dfns
vec2 sampleSphericalMap(vec3 v);

void main()
{
    vec2 uv = sampleSphericalMap(normalize(fPos));
    vec3 color = texture(equirectangularMap, uv).rgb;
    color = vec4(color, 1.0);
}

vec2 sampleSphericalMap(vec3 v) {
    vec2 uv = vec2(atan(v.z, v.x), asin(v.y));
    uv *= invAtan;
    uv += 0.5;
    return uv;
}