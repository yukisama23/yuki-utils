#version 150

#moj_import <minecraft:dynamictransforms.glsl>

in vec2 localPos;
in vec4 vertexColor;
in vec2 shapeParams;

out vec4 fragColor;

float roundedBox(vec2 point, vec2 halfSize, float radius) {
    vec2 q = abs(point) - halfSize + radius;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
}

void main() {
    vec2 localPixelSize = max(fwidth(localPos), vec2(0.000001));
    vec2 point = localPos / localPixelSize;
    vec2 halfSize = vec2(0.5) / localPixelSize;
    float maxRadius = halfSize.x;
    float maxBorder = min(halfSize.x, halfSize.y);
    float radius = clamp(shapeParams.x, 0.0, maxRadius);
    float border = clamp(shapeParams.y * 0.5, 0.0, maxBorder);
    float outer = roundedBox(point, halfSize, radius);
    float aa = max(fwidth(outer) * 1.35, 0.75);
    float alpha = 1.0 - smoothstep(-aa, aa, outer);
    if (border > 0.0) {
        vec2 innerHalf = max(halfSize - border, vec2(0.0));
        float innerRadius = max(radius - border, 0.0);
        float inner = roundedBox(point, innerHalf, innerRadius);
        float innerAlpha = 1.0 - smoothstep(-aa, aa, inner);
        alpha *= 1.0 - innerAlpha;
    }
    vec4 color = vertexColor;
    color.a *= alpha;
    if (color.a <= 0.001) discard;
    fragColor = color * ColorModulator;
}
