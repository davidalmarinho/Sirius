    #type vertex
    #version 330 core

    layout (location = 0) in vec3 aPosition;// 0 => attributionPosition (vec3 -> [x, y, z])
    layout (location = 1) in vec4 aColor;// 1 => attributonColor (vec4 -> [a, r, g, b])
    layout (location = 2) in vec2 aTexCoordinates; // 2 => attributonTextureCoordinates (vec2 → [x, y])
    layout (location = 3) in float aTexID; // 3 => attributionTextureID (float)
    layout (location = 4) in float aEntityID;

    uniform mat4 uProjection;
    uniform mat4 uView;

    out vec4 fColor; // fragmentColor
    out vec2 fTexCoordinates;
    out float fTexID;
    out float fEntityID;

    void main() {
        fColor = aColor;
        fTexCoordinates = aTexCoordinates;
        fTexID = aTexID;
        fEntityID = aEntityID;
        gl_Position = uProjection * uView * vec4(aPosition, 1.0);
    }

    #type fragment
    #version 330 core

    in vec4 fColor;
    in vec2 fTexCoordinates;
    in float fTexID;
    in float fEntityID;

    uniform sampler2D uTextures[8];

    out vec3 color;

    void main() {
        vec4 texColor = vec4(1, 1, 1, 1);
        switch(int(fTexID)) {
            case 0:
            // We don't want to pick a object that hasn't texture
            break;
            case 1:
            texColor = fColor * texture(uTextures[1], fTexCoordinates);
            break;
            case 2:
            texColor = fColor * texture(uTextures[2], fTexCoordinates);
            break;
            case 3:
            texColor = fColor * texture(uTextures[3], fTexCoordinates);
            break;
            case 4:
            texColor = fColor * texture(uTextures[4], fTexCoordinates);
            break;
            case 5:
            texColor = fColor * texture(uTextures[5], fTexCoordinates);
            break;
            case 6:
            texColor = fColor * texture(uTextures[6], fTexCoordinates);
            break;
            case 7:
            texColor = fColor * texture(uTextures[7], fTexCoordinates);
            break;
        }

        if (texColor.a < 0.5) {
            discard;
        }
        color = vec3(fEntityID, fEntityID, fEntityID);
    }