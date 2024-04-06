#version 440 core

in vec2 fTexCords;
in vec3 fNormal;
in vec3 fPos;

out vec4 color;

struct Material {
    vec3 color;
    float metallic;
    float roughness;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
};

const int NUM_P_LIGHTS = 8;
const float PI = 3.14159265359f;

uniform Material material;
uniform DirectionalLight directionalLight;
uniform PointLight pointLight[NUM_P_LIGHTS];

//f(n) prototypes
vec4 getDlComponent(vec3 F0, vec4 albedo, float roughness);
vec3 frenelSchlickApprox(float vDotH, vec3 F0);
float ndfGGX(float nDotH, float alpha);
float schlickGGX(float nDotV,float nDotL, float alpha);

void main(){
    vec4 albedo = vec4(material.color, 1.0f);
    vec4 ao = vec4(0.01);
    float roughness = material.roughness;

    //base reflactivity
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo.rgb, material.metallic);

    //output luminance
    vec4 Lo = getDlComponent(F0, albedo, roughness);

    for(int i = 0; i < NUM_P_LIGHTS; i++){
        vec3 l = normalize(pointLight[i].position);//Z - fPos);
        vec3 n = normalize(fNormal);
        vec3 v = normalize(-fPos);
        vec3 h = normalize(v + l);

        float distance = length(pointLight[i].position - fPos);
        float attenuation = 1.0 / (distance * distance);
        vec3 radiance = pointLight[i].color * pointLight[i].intensity * attenuation;

        float nDotH = max(dot(n, h), 0.0f);
        float vDotH = max(dot(v, h), 0.0f);
        float nDotL = max(dot(n, l), 0.0f);
        float nDotV = max(dot(n, v), 0.0f);

        vec3 F = frenelSchlickApprox(vDotH, F0); // F == kS

        vec3 _nom = ndfGGX(nDotH, roughness) * F * schlickGGX(nDotV, nDotL, roughness);
        float _denom = 4 * nDotL * nDotV + 0.0001f;
        vec3 specularBRDF = _nom / _denom;

        vec3 kD = vec3(1.0) - F;
        kD *= vec3(1 - material.metallic);
        vec3 diffuseBRDF = kD * albedo.xyz / PI ;

        Lo += vec4((diffuseBRDF + specularBRDF) * radiance * nDotL, 1.0f);
    }

    //ambient lighting
    vec4 ambient = vec4(0.03) * ao * albedo;
    color = Lo + ambient;
}

vec4 getDlComponent(vec3 F0, vec4 albedo, float roughness){
    vec3 d_light_radiance = directionalLight.color * directionalLight.intensity;
    vec3 l = normalize(directionalLight.direction);

    vec3 n = normalize(fNormal);
    vec3 v = normalize(-fPos);
    vec3 h = normalize(v + l);

    float nDotH = max(dot(n, h), 0.0f);
    float vDotH = max(dot(v, h), 0.0f);
    float nDotL = max(dot(n, l), 0.0f);
    float nDotV = max(dot(n, v), 0.0f);

    vec3 F = frenelSchlickApprox(vDotH, F0); // F == kS

    vec3 _nom = ndfGGX(nDotH, roughness) * F * schlickGGX(nDotV, nDotL, roughness);
    float _denom = 4 * nDotL * nDotV + 0.0001f;
    vec3 specularBRDF = _nom / _denom;

    vec3 kD = vec3(1.0) - F;
    kD *= vec3(1 - material.metallic);
    vec3 diffuseBRDF = kD * albedo.xyz / PI ;

    return vec4((diffuseBRDF + specularBRDF) * d_light_radiance * nDotL, 1.0f);
}

vec3 frenelSchlickApprox(float vDotH, vec3 F0){
    return F0 + (1.0 - F0) * pow((1.0 - vDotH), 5.0);
}

float ndfGGX(float nDotH, float alpha){
    float a = alpha * alpha;
    float a2 = a * a;
    float denom = nDotH * nDotH * (a2 - 1.0) + 1.0;
    denom = PI * denom * denom;
    return a2 / max(denom, 0.00000001f);
}

float schlickGGX(float nDotV,float nDotL, float alpha){
    float r = alpha + 1;
    float k = (r * r) / 8.0;

    float ggx1 = nDotL / (nDotL * (1.0 - k) + k);
    float ggx2 = nDotV / (nDotV * (1.0 - k) + k);

    return ggx1 * ggx2;
}