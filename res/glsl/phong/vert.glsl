#define PROCESSING_LIGHT_SHADER

uniform mat4 modelview;
uniform mat4 transform;
uniform mat3 normalMatrix;

uniform int lightCount;
uniform vec4 lightPosition[8];

uniform vec3 cameraPosition;

attribute vec4 position;
attribute vec4 color;
attribute vec3 normal;

varying vec4 vertColor;

varying vec3 N;
varying vec3 L[8];
varying vec3 V;

void main() {
    vec3 ecPosition = vec3(modelview * position);

    N = normalize(normalMatrix * normal);
    for (int i = 0; i < lightCount; i++) {
        L[i] = normalize(lightPosition[i].xyz - ecPosition);
    }
    V = normalize(cameraPosition - ecPosition);

    vertColor = color;
    gl_Position = transform * modelview * position;
}
