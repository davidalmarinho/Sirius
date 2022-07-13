package sirius.rendering;

import sirius.rendering.fonts.Glyph;
import sirius.rendering.fonts.Font;
import org.lwjgl.opengl.GL15;
import sirius.SiriusTheFox;
import sirius.utils.AssetPool;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_BUFFER;

public class BatchFont {
    private int[] indices = {
            0, 1, 3,
            1, 2, 3
    };

    // 25 quads
    public static int BATCH_SIZE = 100;
    public static int VERTEX_SIZE = 7;
    public float[] vertices = new float[BATCH_SIZE * VERTEX_SIZE];
    public int size = 0;

    public int vao;
    public int vbo;
    public Font font;

    public void generateEbo() {
        int elementSize = BATCH_SIZE * 3;
        int[] elementBuffer = new int[elementSize];

        for (int i = 0; i < elementSize; i++) {
            elementBuffer[i] = indices[(i % 6)] + ((i / 6) * 4);
        }

        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
    }

    public void initBatch() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (long) Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);

        generateEbo();

        int stride = 7 * Float.BYTES;
        GlObjects.attributeAndEnablePointer(0, 2, stride, 0);
        GlObjects.attributeAndEnablePointer(1, 3, stride, 2 * Float.BYTES);
        GlObjects.attributeAndEnablePointer(2, 2, stride, 5 * Float.BYTES);
    }

    public void flushBatch() {
        // Clear the buffer on the GPU, and then upload the CPU contents, and then draw
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, (long) Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Use shader
        Shader shader = Renderer.getBoundShader();
        shader.use();
        shader.uploadTexture("uFontTexture", 0);
        shader.uploadMat4f("uProjection", SiriusTheFox.getCurrentScene().getCamera().getProjectionMatrix());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_BUFFER, font.getTextureId());

        GlObjects.bindVao(vao);
        GlObjects.enableAttributes(2);

        glDrawElements(GL_TRIANGLES, size * 6, GL_UNSIGNED_INT, 0);

        // Disengage everything
        GlObjects.disableAttributes(2);
        GlObjects.unbindVao();

        // Disengage the textures
        glBindTexture(GL_TEXTURE_2D, 0);

        shader.detach();

        // Reset batch for use on next draw call
        size = 0;
    }

    public void addCharacter(float x, float y, float scale, Glyph glyph, int rgb) {
        // If we have no more room in the current batch, flush it and start with a fresh batch
        if (size >= BATCH_SIZE - 4) {
            flushBatch();
        }

        float r = (float) ((rgb >> 16) & 0xFF) / 255.0f;
        float g = (float) ((rgb >> 8) & 0xFF) / 255.0f;
        float b = (float) ((rgb >> 0) & 0xFF) / 255.0f;

        float x0 = x;
        float y0 = y - scale * (glyph.height - glyph.yBearing);
        float x1 = x + scale * glyph.width;
        float y1 = y + scale * (glyph.height - (glyph.height - glyph.yBearing));

        float ux0 = glyph.textureCoordinates[0].x;
        float uy0 = glyph.textureCoordinates[0].y;
        float ux1 = glyph.textureCoordinates[1].x;
        float uy1 = glyph.textureCoordinates[1].y;

        int index = size * 7;
        vertices[index] = x1;
        vertices[index + 1] = y0;
        vertices[index + 2] = r;
        vertices[index + 3] = g;
        vertices[index + 4] = b;
        vertices[index + 5] = ux1;
        vertices[index + 6] = uy0;

        index += 7;
        vertices[index] = x1;
        vertices[index + 1] = y1;
        vertices[index + 2] = r;
        vertices[index + 3] = g;
        vertices[index + 4] = b;
        vertices[index + 5] = ux1;
        vertices[index + 6] = uy1;

        index += 7;
        vertices[index] = x0;
        vertices[index + 1] = y1;
        vertices[index + 2] = r;
        vertices[index + 3] = g;
        vertices[index + 4] = b;
        vertices[index + 5] = ux0;
        vertices[index + 6] = uy1;

        index += 7;
        vertices[index] = x0;
        vertices[index + 1] = y0;
        vertices[index + 2] = r;
        vertices[index + 3] = g;
        vertices[index + 4] = b;
        vertices[index + 5] = ux0;
        vertices[index + 6] = uy0;

        size += 4;
    }

    public void addText(String text, float x, float y, float scale, int color) {
        if (font == null) {
            this.font = new Font(AssetPool.getFont("assets/fonts/verdana.ttf"));

            // TODO: 04/07/2022 Handle better this error
            if (AssetPool.getFont("assets/fonts/verdana.ttf") == null) {
                System.err.println("Error: Couldn't find font. Have you added a font?");
            }
        }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            Glyph glyph = font.getCharacter(c);

            if (glyph.width == 0) {
                System.out.println("Unknown character " + c);
                continue;
            }

            float xPos = x;
            float yPos = y;
            addCharacter(xPos, yPos, scale, glyph, color);
            x += glyph.width * scale;
        }
    }
}
