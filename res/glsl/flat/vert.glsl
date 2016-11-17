#define PROCESSING_LIGHT_SHADER

uniform mat4 modelview;
uniform mat4 transform;
uniform mat3 normalMatrix;

uniform int lightCount;
uniform vec4 lightPosition[8];
uniform vec3 lightAmbient[8];
uniform vec3 lightDiffuse[8];
uniform vec3 lightSpecular[8];

uniform vec3 matAmbient;
uniform vec3 matDiffuse;
uniform vec3 matSpecular;
uniform float matShininess;

uniform vec3 cameraPosition;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;

varying vec4 vertColor;

vec3 ambientLight(vec3 light){
    return matAmbient * light;
}

vec3 diffuseLight(vec3 L, vec3 N, vec3 light){
    float intensity = max(0.0, dot(L, N));
    return matDiffuse * light * intensity;
}

//Phong
vec3 specularLight(vec3 L, vec3 N, vec3 V, vec3 light){
    if(dot(L, N) > 0) {
        vec3 R = reflect(-L, N);
        float intensity = pow(max(0.0, dot(R, V)), matShininess);
        return matSpecular * light * intensity;
    }
    return vec3(0,0,0);
}

//Blinn–Phong
vec3 specularLight2(vec3 L, vec3 N, vec3 V, vec3 light){
    if(dot(L, N) > 0) {
        vec3 H = normalize(L + V);
        float intensity = pow(dot(N, H), matShininess);
        return matSpecular * light * intensity;
    }
    return vec3(0,0,0);
}

void main() {
    vec3 ecPosition = vec3(modelview * position);
    vec3 N = normalize(normalMatrix * normal);

    vec3 light = vec3(0,0,0);
    for (int i = 0; i < lightCount; i++) {
        vec3 L = normalize(lightPosition[i].xyz - ecPosition);
        vec3 V = normalize(cameraPosition - ecPosition);

        light += ambientLight(lightAmbient[i]);
        light += diffuseLight(L,N,lightDiffuse[i]);
        light += specularLight(L,N,V,lightSpecular[i]);
    }

    vertColor = vec4(light, 1) * color;
    gl_Position = transform * modelview * position;
}
