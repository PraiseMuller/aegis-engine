#version 330 core

in vec2 fTexCoords;
in vec3 vertexNormal;
in vec3 vertexPos;
out vec4 color;

struct Attenuation{
    float constant;
    float linear;
    float exponent;
};

struct PointLight{
    vec3 color;
    vec3 position; //in view space coordinates
    float intensity;
    Attenuation attenuation;
};

struct DirectionalLight{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Material{
    vec4 color;
    int hasTexture;
    float reflectance;
    float specularPower;
};

uniform float pointLightOn;
uniform sampler2D textureSampler;
uniform vec4 ambientLight;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;

vec4 set_up_colors(Material mat, vec2 texCords){
    if(mat.hasTexture == 1){
       return texture(textureSampler, texCords);
    }
    else{
        return material.color;
    }
}

vec4 calc_light_color(vec3 lightColor, float lightIntensity, vec3 vertex_pos, vec3 toLightSource, vec3 normal){
    //Diffuse Light
    float diffuseFactor = max(dot(normal, toLightSource), 0.0f);
    vec4 diffuseColor = diffuseFactor * vec4(lightColor, 1.0f) * lightIntensity;

    // Specular Light
    vec3 cameraDirection = normalize( - vertex_pos);
    vec3 fromLightSource = -toLightSource;                      // -L --> Points away from light
    vec3 reflectedLight = normalize(reflect(fromLightSource, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, material.specularPower);
    vec4 specularColor = specularFactor * material.reflectance * vec4(lightColor, 1.0f);

    return diffuseColor + specularColor;
}

vec4 calc_point_light(PointLight light, vec3 vertex_pos, vec3 normal){
    vec3 toLightSource = normalize(light.position - vertex_pos);        // L --> points towards the light
    vec4 lightColor = calc_light_color(light.color, light.intensity, vertex_pos, toLightSource, normal);

    // Light Attenuation
    float distance = length(light.position - vertex_pos);
    float attenuationInv = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.exponent * distance * distance;

    //要用的材料 -> attV
    return lightColor / attenuationInv;
}

vec4 calc_directional_light(DirectionalLight light, vec3 vertex_pos, vec3 normal){
    return calc_light_color(light.color, light.intensity, vertex_pos, normalize(light.direction), normal);
}

void main(){
    vec4 fragColorComponent = set_up_colors(material, fTexCoords);

    vec4 diffuse_spec_comp;
    if(pointLightOn == 1){
        diffuse_spec_comp = calc_point_light(pointLight, vertexPos, vertexNormal) + calc_directional_light(directionalLight, vertexPos, vertexNormal);
    }
    else{
        diffuse_spec_comp = calc_directional_light(directionalLight, vertexPos, vertexNormal);
    }

    color = (ambientLight + diffuse_spec_comp) * fragColorComponent;
}