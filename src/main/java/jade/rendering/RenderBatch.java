package jade.rendering;

import gameobjects.GameObject;
import jade.Window;
import gameobjects.components.SpriteRenderer;
import jade.rendering.spritesheet.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    /* In synthesis:
     *
     * Pos              Color                           Texture Coordinates      Texture ID
     * float, float,    float, float, float, float,     float, float             float
     */

    // Attributes
    private final int POSITION_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXTURE_COORDINATES_SIZE = 2;
    private final int TEXTURE_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    // Offsets
    private final int POSITION_OFFSET = 0;
    private final int COLOR_OFFSET = POSITION_OFFSET + POSITION_SIZE * Float.BYTES;
    private final int TEXTURE_COORDINATES_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDINATES_OFFSET + TEXTURE_COORDINATES_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEXTURE_ID_OFFSET + TEXTURE_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = POSITION_SIZE + COLOR_SIZE + TEXTURE_COORDINATES_SIZE + TEXTURE_ID_SIZE + ENTITY_ID_SIZE;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    // Necessary stuff
    private SpriteRenderer[] sprites;
    private List<Texture> textureList;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private int vaoID, vboID, eboID;
    private int maxBatchSize;

    private int zIndex;

    /**
     * @param maxBatchSize Quantos tiles desenhamos de uma só vez
     */
    public RenderBatch(int maxBatchSize, int zIndex) {
        this.zIndex = zIndex;

        // 1 sprite per each batch
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices per quad
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        hasRoom = true;
        this.textureList = new ArrayList<>();
    }

    /**
     * Here is where we are going to create and load the vao, vbo and ebo to the CPU.
     * We will also attribute pointers and active its respective attribute
     */
    public void start() {
        // Generate and engage o VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space to the vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and load the indices
        eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Gives enable to the attributes
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

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES,
                ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    private int[] generateIndices() {
        // 6 indices per square (3 per triangle)
        int[] elements = new int[maxBatchSize * 6];

        for (int i = 0; i < maxBatchSize; i++) {
            loadElement(elements, i);
        }

        return elements;
    }

    private void loadElement(int[] elements, int index) {
        // To find the beginning of the rendering
        int arrayIndexOffset = 6 * index;

        // To find the current
        int offset = 4 * index; // 4, because we need 4 elements to draw a square

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

        // Add the texture to the list
        if (spr.getTexture() != null) {
            if (!textureList.contains(spr.getTexture())) {
                textureList.add(spr.getTexture());
            }
        }

        // Add properties to the vertices
        loadVertexProperties(index);

        // Verify if we still have space to draw one more game object in the same render batch
        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        // 4 vertices per square
        SpriteRenderer sprite = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] textCoords = sprite.getTexCoords();

        // By default, the textID will be 0
        int textID = 0;

        // Verify if we have textures
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textureList.size(); i++) {
                if (textureList.get(i).equals(sprite.getTexture())) {
                    textID = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(sprite.gameObject.transform.position.x, sprite.gameObject.transform.position.y, 0.0f);
            transformMatrix.rotate((float) Math.toRadians(sprite.gameObject.transform.rotation), 0, 0, 1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x, sprite.gameObject.transform.scale.y, 1);
        }

        // Add the 4 vertices with its properties

        /*
              [3]-----[2]
               |       |
               |       |
              [0]-----[1]

         */

        float xAdd = 0.5f;
        float yAdd = 0.5f;

        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            Vector4f currentPos = new Vector4f(
                    sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x),
                    sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y),
                    0, 1);

            if (isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }

            // Load positions
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture
            vertices[offset + 6] = textCoords[i].x;
            vertices[offset + 7] = textCoords[i].y;

            // Load texture's ID
            vertices[offset + 8] = textID;

            // Load entity id
            vertices[offset + 9] = sprite.gameObject.getUid() + 1; // +1 because 0 will be a flag that is an invalid object

            offset += VERTEX_SIZE;
        }
    }

    public void render() {
        boolean rebuffer = false;
        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer sprite = sprites[i];
            if (sprite.isDirty()) {
                loadVertexProperties(i);
                sprite.setDirty(false);
                rebuffer = true;
            }
        }

        // We rebuffer if the sprite is dirty
        if (rebuffer) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
        Shader shader = Renderer.getBoundShader();
        shader.use();
        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        // Load the textures
        for (int i = 0; i < textureList.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textureList.get(i).bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the elements
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        // Disengage everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // Disengage the textures
        for (int i = 0; i < textureList.size(); i++) {
            textureList.get(i).unbind();
        }

        shader.detach();
    }

    public boolean destroyIfExists(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        for (int i = 0; i < numSprites; i++) {
            if (sprites[i] == spriteRenderer) {
                for(int j = i; j < numSprites; j++) {
                    sprites[j] = sprites[j + 1];
                    sprites[j].setDirty(true);
                }
                numSprites--;
                return true;
            }
        }
        return false;
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

    public int getzIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
