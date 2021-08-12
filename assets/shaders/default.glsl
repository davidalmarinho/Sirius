    #type vertex
    #version 330 core

    layout (location = 0) in vec3 aPosition;// 0 => attributionPosition (vec3 -> [x, y, z])
    layout (location = 1) in vec4 aColor;// 1 => attributonColor (vec4 -> [a, r, g, b])

    uniform mat4 uProjection;
    uniform mat4 uView;

    out vec4 fColor;// fragmentColor

    void main() {
        fColor = aColor;
        gl_Position = uProjection * uView * vec4(aPosition, 1.0);
    }

    #type fragment
    #version 330 core

    uniform float uTime;

    in vec4 fColor;

    out vec4 color;

    // Noise function
    vec4 noise(vec4 fColor) {
        float noise = fract(sin(dot(fColor.xy, vec2(12.9898, 78.233))) * 43758.5453);

        return noise * fColor;
    }

    // Black and White function
    vec4 toBlackAndWhite(vec4 fColor) {
        float avg = (fColor.r + fColor.g + fColor.b) / 3;
        return vec4(avg, avg, avg, 1);
    }

    void main() {
        // Black and White effect
        // color = toBlackAndWhite(fColor);

        // Cor normal
        // color = fColor;

        // Noise
        color = noise(fColor);
    }