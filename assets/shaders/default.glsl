    #type vertex
    #version 330 core

    layout (location = 0) in vec3 aPosition;// 0 => attributionPosition (vec3 -> [x, y, z])
    layout (location = 1) in vec4 aColor;// 1 => attributonColor (vec4 -> [a, r, g, b])

    out vec4 fColor;// fragmentColor

    void main() {
        fColor = aColor;
        gl_Position = vec4(aPosition, 1.0);
    }

    #type fragment
    #version 330 core

    in vec4 fColor;

    out vec4 color;

    void main() {
        color = fColor;
    }