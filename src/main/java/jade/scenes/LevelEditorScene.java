package jade.scenes;

import jade.utils.Data;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Lógica para editar níveis
 */
public class LevelEditorScene extends Scene {
    private String vertexShaderSrc = "    #version 330 core\n" +
            "    \n" +
            "    layout (location = 0) in vec3 aPosition;// 0 => attributionPosition (vec3 -> [x, y, z])\n" +
            "    layout (location = 1) in vec4 aColor;// 1 => attributonColor (vec4 -> [a, r, g, b])\n" +
            "    \n" +
            "    out vec4 fColor;// fragmentColor\n" +
            "    \n" +
            "    void main() {\n" +
            "        fColor = aColor;\n" +
            "        gl_Position = vec4(aPosition, 1.0);\n" +
            "    }\n";
    private String fragmentShaderSrc = "    #version 330 core\n" +
            "    \n" +
            "    in vec4 fColor;\n" +
            "    \n" +
            "    out vec4 color;\n" +
            "    \n" +
            "    void main() {\n" +
            "        color = fColor;\n" +
            "    }";

    private int vertexID, fragmentID;
    private int shaderProgram; // Responsável pelo linking dos outros 2

    private float[] vertexArray = {
            // Positions               // Colors (r, g, b, a)
             0.5f, -0.5f, 0.0f,        1.0f, 0.0f, 0.0f, 1.0f, // Bottom right [0]
            -0.5f,  0.5f, 0.0f,        0.0f, 1.0f, 0.0f, 1.0f, // Top left [1]
            -0.5f, -0.5f, 0.0f,        0.0f, 0.0f, 0.0f, 0.0f, // Bottom left [2]
             0.5f,  0.5f, 0.0f,        0.0f, 0.0f, 1.0f, 0.0f  // Top right [3]
    };

    private int[] elementArray = {
            0, 1, 2,
            0, 3, 1
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        
    }

    @Override
    public void init() {
        // Compilar e ligar
        // Primeiro: carregar e compilar o vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Colocar o shader vertex ao nível do GPU
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);

        // Verificar se existiram erros na compilação dos glsl
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS); // Buscar informações à cerca da compilação do vertex shader
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + "default.glsl" + "', couldn't be compiled.\n\tFailed vertex shader " +
                    "compilation");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // Mesma coisa para o fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + "default.glsl" + "', couldn't be compiled.\n\tFailed " +
                    "fragment shader compilation.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Linking
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("Error: '" + "default.glsl" + "' couldn't be linked.");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

        // Gerar VAO, EBO e VBO e mandá-los para o GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Criar float buffer dos vertices
        FloatBuffer vertexBuffer = Data.toBuffer(vertexArray);

        // Criar VBO e mandá-lo para o GPU
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Criar os índices e carregá-los
        IntBuffer elementBuffer = Data.toBuffer(elementArray);

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Atributos dos vértices (por ponteiros)
        int floatBytes      = 4;
        int positionIndexes = 3;
        int colorIndexes    = 4;

        int vertexSizeBytes = (positionIndexes + colorIndexes) * floatBytes;
        glVertexAttribPointer(0, positionIndexes, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorIndexes, GL_FLOAT, false, vertexSizeBytes, positionIndexes * floatBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        // Bind shader program
        glUseProgram(shaderProgram);

        // Bind VAO
        glBindVertexArray(vaoID);

        // Enable vertexAttributePointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0); // Use no VAO
        glUseProgram(0); // Use no shaderProgram
    }
}