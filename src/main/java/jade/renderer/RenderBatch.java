package jade.renderer;

import jade.Window;
import jade.gameobjects.components.SpriteRenderer;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    // Atributos
    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;

    // Offsets
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    // Necessário
    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID, eboID;
    private int maxBatchSize;
    private Shader shader;

    /**
     * @param maxBatchSize Quantos tiles desenhamos de uma só vez
     */
    public RenderBatch(int maxBatchSize) {
        this.shader = new Shader("assets/shaders/default.glsl");
        shader.compile();
        // 1 sprite por cada batch
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices per quad
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        hasRoom = true;
    }

    /**
     * Aqui é onde vamos criar e carregar para o CPU o vaoID, o vboID e o eboID.
     * Também vamos atribuir pointers e ativar esse respetivo atributo
     */
    public void start() {
        // Gerar e engatar o VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Alocar espaço para os vértices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Criar e carregar os índices
        eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Dar enable aos atributos
        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES,
                POSITION_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES,
                POSITION_OFFSET + COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    private int[] generateIndices() {
        // 6 indíces por quadrado (3 por triângulo)
        int[] elements = new int[maxBatchSize * 6];

        for (int i = 0; i < maxBatchSize; i++) {
            loadElement(elements, i);
        }

        return elements;
    }

    private void loadElement(int[] elements, int index) {
        // Para encontrarmos o início da renderização
        int arrayIndexOffset = 6 * index;

        // Para encontramos o atual
        int offset = 4 * index; // 4, pois precisamos de 4 elementos para desenhar um quadrado

        // 3, 2, 0, 0, 2, 1           7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[arrayIndexOffset] = offset + 3;
        elements[arrayIndexOffset + 1] = offset + 2;
        elements[arrayIndexOffset + 2] = offset + 0;

        // Triangle 2
        elements[arrayIndexOffset + 3] = offset + 0;
        elements[arrayIndexOffset + 4] = offset + 2;
        elements[arrayIndexOffset + 5] = offset + 1;
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add renderObject
        int index = this.numSprites; // Colocar no fim do array
        this.sprites[index] = spr;
        this.numSprites++;

        // Adicionar propriedades aos vértices
        loadVertexProperties(index);

        // Verificar se ainda temos mais espaço para renderizar algum gameObject
        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        // 4 vértices por quadrado
        SpriteRenderer sprite = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        // Adicionar os 4 vértices com as suas propriedades

        /*
              [3]-----[2]
               |       |
               |       |
              [0]-----[1]

         */

        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            // Carregar posições
            vertices[offset] = sprite.getGameObject().transform.position.x
                    + (xAdd * sprite.getGameObject().transform.scale.x);
            vertices[offset + 1] = sprite.getGameObject().transform.position.y
                    + (yAdd * sprite.getGameObject().transform.scale.y);

            // Carregar cor
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;
        }
    }

    public void render() {
        // Por agora, vamos fazer rebuffer aos dados por cada frame
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Usar o shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Desenhar os elementos
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        // Desengatar tudo
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    public boolean hasRoom() {
        return hasRoom;
    }
}
