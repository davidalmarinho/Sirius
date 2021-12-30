package jade.rendering.debug;

import jade.Window;
import jade.rendering.Color;
import jade.rendering.Shader;
import jade.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static int MAX_LINES = 500;

    private static List<Line2D> line2DList = new ArrayList<>();
    // 6 floats per vertex (x, y, z, r, g, b) , 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID;
    private static int vboID;

    private static boolean started;

    public static void start() {
        // Generate the vao
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create vbo
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Enable vertex array attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }
        
        // Remove dead lines
        for (int i = 0; i < line2DList.size(); i++) {
            if (line2DList.get(i).beginFrame() < 0) {
                line2DList.remove(i);
                i--;
            }
        }
    }

    /**
     * Draws the lines
     */
    public static void draw() {
        if (line2DList.isEmpty()) return;
        
        int index = 0;
        for (Line2D line : line2DList) {
            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();
                Vector3f color = line.getColor();

                // Load position
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                // Load the color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }
        
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, line2DList.size() * 6 * 2));
        
        // Use our shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());
        
        // Bind vao
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        // Draw the batch
        glDrawArrays(GL_LINES, 0, line2DList.size() * 6 * 2);
        // Bresenham's line algorithm (Optimize lines drawing)

        // Disable location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }
    
    // ===================================================================
    // Add Line2D
    // ===================================================================
    
    public static void addLine2D(Vector2f from, Vector2f to) {
        addLine2D(from, to, Color.GREEN, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Color color) {
        addLine2D(from, to, color, 1);
    }

    public static void addLine2D(Vector2f from, Vector2f to, int lifetime) {
        addLine2D(from, to, Color.GREEN, lifetime);
    }

    public static void addLine2D(Vector2f from, Vector2f to, Color color, int lifetime) {
        if (line2DList.size() >= MAX_LINES) return;
        DebugDraw.line2DList.add(new Line2D(from, to, color, lifetime));
    }

    // ===================================================================
    // Add Box2D
    // ===================================================================

    public static void addBox2D(Vector2f center, Vector2f dimensions) {
        addBox2D(center, dimensions, 0.0f, Color.GREEN, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
        addBox2D(center, dimensions, rotation, Color.GREEN, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, int lifetime) {
        addBox2D(center, dimensions, rotation, Color.GREEN, lifetime);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, Color color) {
        addBox2D(center, dimensions, 0.0f, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, Color color, int lifetime) {
        addBox2D(center, dimensions, 0.0f, color, lifetime);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Color color, int lifetime) {
        Vector2f bottomLeftCorner = new Vector2f(center).sub(new Vector2f(dimensions)).mul(0.5f);
        Vector2f topRightCorner = new Vector2f(center).add(new Vector2f(dimensions)).mul(0.5f);

        Vector2f[] vertices = {
                new Vector2f(bottomLeftCorner.x, bottomLeftCorner.y),  // Bottom left corner
                new Vector2f(topRightCorner.x, topRightCorner.y),      // Top right corner
                new Vector2f(topRightCorner.x, bottomLeftCorner.y),    // Bottom right corner
                new Vector2f(bottomLeftCorner.x, topRightCorner.y),    // Top left corner
        };

        // If it has been rotated
        if (rotation != 0.0f) {
            for (Vector2f vert : vertices) {
                // JMath.rotate(vert, rotation, center);
            }
        }

        addLine2D(vertices[0], vertices[2], color, lifetime); // BL -> BR
        addLine2D(vertices[0], vertices[3], color, lifetime); // BL -> TL
        addLine2D(vertices[1], vertices[3], color, lifetime); // TR -> TL
        addLine2D(vertices[1], vertices[2], color, lifetime); // TR -> BR
    }
}