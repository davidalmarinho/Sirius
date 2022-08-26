package sirius.rendering;

import gameobjects.GameObject;
import sirius.SiriusTheFox;
import gameobjects.components.SpriteRenderer;
import sirius.rendering.spritesheet.Texture;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

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

    private Renderer renderer;

    /**
     * @param maxBatchSize How many textures we can draw with the current batch
     */
    public RenderBatch(int maxBatchSize, int zIndex, Renderer renderer) {
        this.renderer = renderer;

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
        vaoID = GlObjects.allocateVao();

        // Allocate space to the vertices
        vboID = GlObjects.allocateVbo((long) vertices.length * Float.BYTES);

        // Create and load the indices
        eboID = GlObjects.allocateEbo();

        // Gives enable to the attributes
        GlObjects.attributeAndEnablePointer(0, POSITION_SIZE, VERTEX_SIZE_BYTES, POSITION_OFFSET);
        GlObjects.attributeAndEnablePointer(1, COLOR_SIZE, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        GlObjects.attributeAndEnablePointer(2, TEXTURE_COORDINATES_SIZE, VERTEX_SIZE_BYTES, TEXTURE_COORDINATES_OFFSET);
        GlObjects.attributeAndEnablePointer(3, TEXTURE_ID_SIZE, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
        GlObjects.attributeAndEnablePointer(4, ENTITY_ID_SIZE, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add renderObject
        int index = this.numSprites;
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

        Vector4f color = sprite.getColorVec4();
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

        boolean isRotated = sprite.gameObject.getTransform().rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(sprite.gameObject.getTransform().position.x,
                    sprite.gameObject.getTransform().position.y, 0.0f);
            transformMatrix.rotate((float) Math.toRadians(sprite.gameObject.getTransform().rotation), 0, 0, 1);
            transformMatrix.scale(sprite.gameObject.getTransform().scale.x,
                    sprite.gameObject.getTransform().scale.y, 1);
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
                    sprite.gameObject.getTransform().position.x + (xAdd * sprite.gameObject.getTransform().scale.x),
                    sprite.gameObject.getTransform().position.y + (yAdd * sprite.gameObject.getTransform().scale.y),
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
                if (!hasTexture(sprite.getTexture())) {
                    renderer.destroyGameObject(sprite.gameObject);
                    renderer.add(sprite.gameObject);
                } else {
                    loadVertexProperties(i);
                    sprite.setDirty(false);
                    rebuffer = true;
                }
            }

            // TODO: 27/02/2022 Get better solution
            if (sprite.gameObject.getTransform().zIndex != this.zIndex) {
                destroyIfExists(sprite.gameObject);
                renderer.add(sprite.gameObject);
                i--;
            }
        }

        // We rebuffer if the sprite is dirty
        if (rebuffer) {
            GlObjects.replaceVboData(vboID, vertices);
        }

        // Use shader
        Shader shader = Renderer.getBoundShader();
        shader.use();
        shader.uploadMat4f("uProjection", SiriusTheFox.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", SiriusTheFox.getCurrentScene().getCamera().getViewMatrix());

        // Load the textures
        for (int i = 0; i < textureList.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textureList.get(i).bind();
        }
        shader.uploadIntArray("uTextures", textureSlots);

        GlObjects.bindVao(vaoID);
        GlObjects.enableAttributes(2);

        // Draw the elements
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        // Disengage everything
        GlObjects.disableAttributes(2);
        GlObjects.unbindVao();

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
                for (int j = i; j < numSprites - 1; j++) {
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