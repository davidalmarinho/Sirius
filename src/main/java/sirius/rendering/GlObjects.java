package sirius.rendering;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class GlObjects {
    private static final int DEFAULT_BATCH_SIZE = 1000;

    /**
     * Generates a vertex array object.
     *
     * @return the vertex array object id.
     */
    public static int allocateVao() {
        int vaoId = 0;
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        return vaoId;
    }

    /**
     * Generates a vertex buffer object.
     *
     * @param vertexSizeBytes Vertex buffer object's data size in bytes.
     * @return The vertex buffer object id.
     */
    public static int allocateVbo(long vertexSizeBytes) {
        int vboId = 0;
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexSizeBytes, GL_DYNAMIC_DRAW);

        return vboId;
    }

    /**
     * Binds the wished vertex array object.
     *
     * @param vaoId The vertex array object that is pretending to bind.
     */
    public static void bindVao(int vaoId) {
        glBindVertexArray(vaoId);
    }

    /**
     * Unbinds the current vertex array object.
     */
    public static void unbindVao() {
        glBindVertexArray(0);
    }

    private static int[] generateIndices(int maxBatchSize) {
        int indicesPerTriangle = 3;

        // 6 indices per square (3 per triangle)
        int[] elements = new int[maxBatchSize * indicesPerTriangle * 2];

        for (int i = 0; i < maxBatchSize; i++) {
            loadElement(elements, i);
        }

        return elements;
    }

    private static void loadElement(int[] elements, int index) {
        // To find the beginning of the rendering
        int arrayIndexOffset = 6 * index;

        // To find the current
        int offset = 4 * index; // 4, because we need 4 elements to draw a square

        // 3, 2, 0, 0, 2, 1           7, 6, 4, 4, 6, 5
        /*
              [3]-----[2]
               |       |
               |       |
              [0]-----[1]
         */

        // Triangle 1
        elements[arrayIndexOffset]     = offset + 3;
        elements[arrayIndexOffset + 1] = offset + 2;
        elements[arrayIndexOffset + 2] = offset + 0;

        // Triangle 2
        elements[arrayIndexOffset + 3] = offset + 0;
        elements[arrayIndexOffset + 4] = offset + 2;
        elements[arrayIndexOffset + 5] = offset + 1;
    }

    /**
     * Generates an Element buffer object.
     * This, specifically generates an ebo to render 2 triangles that form a square.
     *
     * @param maxBatchSize Specifies the space reserved to the indices.
     * @return Element buffer object's id
     */
    public static int allocateEbo(int maxBatchSize) {
        int eboId = 0;
        eboId = glGenBuffers();
        int[] indices = generateIndices(maxBatchSize);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        return eboId;
    }

    /**
     * Generates an Element buffer object.
     * This, specifically generates an ebo to render 2 triangles that form a square.
     *
     * @return Element buffer object's id
     */
    public static int allocateEbo() {
        return allocateEbo(DEFAULT_BATCH_SIZE);
    }

    /**
     * Redefines all the data store for the specified buffer object.
     *
     * @param vboId Vertex buffer data id (where is allocated the data)
     * @param data Vbo's values we are going to redefine
     */
    public static void replaceVboData(int vboId, float[] data) {
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
    }

    public static void enableAttributes(int amount) {
        throwMaxVertexAttributesError(amount);

        for (int i = 0; i < amount; i++) {
            glEnableVertexAttribArray(i);
        }
    }

    public static void disableAttributes(int amount) {
        throwMaxVertexAttributesError(amount);

        for (int i = 0; i < amount; i++) {
            glDisableVertexAttribArray(i);
        }
    }

    public static void attributeAndEnablePointer(int index, int size, int vertexSizeBytes, int offsetBytes) {
        throwMaxVertexAttributesError(index);

        glVertexAttribPointer(index, size, GL_FLOAT, false, vertexSizeBytes, offsetBytes);
        glEnableVertexAttribArray(index);
    }

    private static void throwMaxVertexAttributesError(int index) {
        if (index > GL_MAX_VERTEX_ATTRIBS) {
            try {
                throw new Exception("The maximum amount of attributes per vertex data is '" + GL_MAX_VERTEX_ATTRIBS + "'.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
