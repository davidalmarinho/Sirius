package jade.renderer;

import jade.Window;
import jade.gameobjects.components.SpriteRenderer;
import jade.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    /* Em síntese:
     *
     * Pos              Color                           Texture Coordinates      Texture ID
     * float, float,    float, float, float, float,     float, float             float
     */

    // Atributos
    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXTURE_COORDINATES_SIZE = 2;
    private final int TEXTURE_ID_SIZE = 1;

    // Offsets
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int TEXTURE_COORDINATES_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDINATES_OFFSET + TEXTURE_COORDINATES_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE + TEXTURE_COORDINATES_SIZE + TEXTURE_ID_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    // Necessário
    private SpriteRenderer[] sprites;
    private List<Texture> textureList;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private int vaoID, vboID, eboID;
    private int maxBatchSize;
    private Shader shader;

    /**
     * @param maxBatchSize Quantos tiles desenhamos de uma só vez
     */
    public RenderBatch(int maxBatchSize) {
        this.shader = AssetPool.getShader("assets/shaders/default.glsl");

        // 1 sprite por cada batch
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices per quad
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        hasRoom = true;
        this.textureList = new ArrayList<>();
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
                COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_COORDINATES_SIZE, GL_FLOAT, false,
                VERTEX_SIZE_BYTES, TEXTURE_COORDINATES_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES,
                TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);
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

        // Colocar a textura na nossa lista
        if (spr.getTexture() != null) {
            if (!textureList.contains(spr.getTexture())) {
                textureList.add(spr.getTexture());
            }
        }

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
        Vector2f[] textCoords = sprite.getTexCoords();

        // Por default, a nossa textID será 0
        int textID = 0;

        // Verificar se temos alguma textura
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textureList.size(); i++) {
                if (textureList.get(i) == sprite.getTexture()) {
                    textID = i + 1;
                    break;
                }
            }
        }

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

            // Carregar textura
            vertices[offset + 6] = textCoords[i].x;
            vertices[offset + 7] = textCoords[i].y;

            // Carregar TextureID
            vertices[offset + 8] = textID;

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

        // Carregar as texturas
        for (int i = 0; i < textureList.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textureList.get(i).bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Desenhar os elementos
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        // Desengatar tudo
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // Desengatar texturas
        for (int i = 0; i < textureList.size(); i++) {
            textureList.get(i).unbind();
        }

        shader.detach();
    }

    public boolean hasRoom() {
        return hasRoom;
    }

    public boolean hasRoomTexture() {
        return this.textureList.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return this.textureList.contains(texture);
    }
}
