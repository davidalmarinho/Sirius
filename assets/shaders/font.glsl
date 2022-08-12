    #type vertex
    #version 330 core

    layout(location = 0) in vec2 aPos;
    layout(location = 1) in vec4 aColor;
    layout(location = 2) in vec2 aTexCoords;

    uniform mat4 uProjection;
    uniform mat4 uView;

    out vec2 fTexCoords;
    out vec4 fColor;

    void main() {
        fTexCoords  = aTexCoords;
        fColor      = aColor;
        gl_Position = uProjection * uView * vec4(aPos, -10.0, 1.0);
    }

    #type fragment
    #version 330 core

    in vec2 fTexCoords;
    in vec4 fColor;

    uniform sampler2D uFontTexture;

    out vec4 color;

    void main() {
        float c = texture(uFontTexture, fTexCoords).r;
        color   = vec4(1, 1, 1, c) * fColor;
    }
