    #type vertex
    #version 330 core

    layout (location = 0) in vec2 aPosition;// 0 => attributionPosition (vec3 -> [x, y])
    layout (location = 1) in vec4 aColor;// 1 => attributonColor (vec4 -> [a, r, g, b])
    layout (location = 2) in vec2 aTexCoordinates; // 2 => attributonTextureCoordinates (vec2 → [x, y])
    layout (location = 3) in float aTexID; // 3 => attributionTextureID (float)

    uniform mat4 uProjection;
    uniform mat4 uView;

    out vec4 fColor; // fragmentColor
    out vec2 fTexCoordinates;
    out float fTexID;

    void main() {
        fColor = aColor;
        fTexCoordinates = aTexCoordinates;
        fTexID = aTexID;
        gl_Position = uProjection * uView * vec4(aPosition, -10.0, 1.0);
    }

    #type fragment
    #version 330 core

    in vec4 fColor;
    in vec2 fTexCoordinates;
    in float fTexID;

    uniform sampler2D uTextures[8];

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
        // color = noise(fColor);

        // applyTexture(color);
        // color = texture(uTexSampler, fTexCoordinates);

        // Apply texture
        switch(int(fTexID)) {
            case 0:
                color = fColor;
            break;
            case 1:
                color = fColor * texture(uTextures[1], fTexCoordinates);
            break;
            case 2:
                color = fColor * texture(uTextures[2], fTexCoordinates);
            break;
            case 3:
                color = fColor * texture(uTextures[3], fTexCoordinates);
            break;
            case 4:
                color = fColor * texture(uTextures[4], fTexCoordinates);
            break;
            case 5:
                color = fColor * texture(uTextures[5], fTexCoordinates);
            break;
            case 6:
                color = fColor * texture(uTextures[6], fTexCoordinates);
            break;
            case 7:
                color = fColor * texture(uTextures[7], fTexCoordinates);
            break;
        }
    }