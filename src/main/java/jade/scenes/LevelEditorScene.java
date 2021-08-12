package jade.scenes;

import jade.renderer.Camera;
import jade.renderer.Shader;
import jade.utils.Data;
import jade.utils.Time;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Lógica para editar níveis
 */
public class LevelEditorScene extends Scene {
    private Shader defaultShader;

    private final float[] vertexArray = {
            // Positions               // Colors (r, g, b, a)
            100.5f,   0.5f, 0.0f,        1.0f, 0.0f, 0.0f, 1.0f, // Bottom right [0]
              0.5f, 100.5f, 0.0f,        0.0f, 1.0f, 0.0f, 1.0f, // Top left [1]
            100.5f, 100.5f, 0.0f,        0.0f, 0.0f, 0.0f, 0.0f, // Bottom left [2]
              0.5f,   0.5f, 0.0f,        0.0f, 0.0f, 1.0f, 0.0f  // Top right [3]
    };

    private final int[] elementArray = {
            0, 1, 2,
            0, 3, 1
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        
    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector3f());
        defaultShader = new Shader("assets/shaders/default.glsl");

        defaultShader.compile();

        // Gerar VAO, EBO e VBO e mandá-los para o GPU
        vaoID = glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        // Criar float buffer dos vertices
        FloatBuffer vertexBuffer = Data.toBuffer(vertexArray);

        // Criar VBO e mandá-lo para o GPU
        vboID = glGenBuffers();
        GL15.glBindBuffer(GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Criar os índices e carregá-los
        IntBuffer elementBuffer = Data.toBuffer(elementArray);

        eboID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Atributos dos vértices (por ponteiros)
        int positionIndexes = 3;
        int colorIndexes    = 4;
        int vertexSizeBytes = (positionIndexes + colorIndexes) * Float.BYTES;

        GL20.glVertexAttribPointer(0, positionIndexes, GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, colorIndexes, GL_FLOAT, false, vertexSizeBytes,
                positionIndexes * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        camera.position.x -= dt * 50.0f;
        // Bind shader program
        defaultShader.use();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

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
        // glUseProgram(0); // Use no shaderProgram
        defaultShader.detach();
    }
}