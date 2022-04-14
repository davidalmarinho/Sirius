package sirius.rendering.debug;

import sirius.SiriusTheFox;
import sirius.rendering.Camera;
import sirius.rendering.Color;
import sirius.rendering.GlObjects;
import sirius.rendering.Shader;
import sirius.utils.AssetPool;
import org.joml.Vector2f;
import sirius.utils.JMath;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;

public class DebugDraw {
    private static int MAX_LINES = 3000;
    public static float lineThickness = 1.0f;

    private static List<Line2D> line2DList = new ArrayList<>();
    // 6 floats per vertex (x, y, z, r, g, b) , 2 vertices per line
    private static float[] vertexArray = new float[MAX_LINES * 7 * 8];
    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID;
    private static int vboID;
    private static int eboID;

    private static boolean started;

    public static void start() {
        // Generate the vao
        vaoID = GlObjects.allocateVao();

        // Create vbo
        vboID = GlObjects.allocateVbo((long) vertexArray.length * Float.BYTES);

        eboID = GlObjects.allocateEbo(MAX_LINES);

        // Enable vertex array attributes
        GlObjects.attributeAndEnablePointer(0, 3, 7 * Float.BYTES, 0);
        GlObjects.attributeAndEnablePointer(1, 4, 7 * Float.BYTES, 3 * Float.BYTES);
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
        float constant = 0.0025f * lineThickness;
        for (Line2D line : line2DList) {
            for (int i = 0; i < 8; i++) {
                // Vector2f position = i == 0 ? line.getStart() : line.getEnd();
                Vector2f position = new Vector2f();

                // Horizontal lines
                if (i == 0)
                    // Top right
                    position.set(new Vector2f(line.getEnd()).add(0.0f, constant));
                else if (i == 1)
                    // Bottom right
                    position.set(new Vector2f(line.getEnd()).sub(0.0f, constant));
                else if (i == 2)
                    // Bottom lef
                    position.set(new Vector2f(line.getStart()).sub(0.0f, constant));
                else if (i == 3)
                    // Top left
                    position.set(new Vector2f(line.getStart()).add(0.0f, constant));

                // Vertical lines
                if (i == 4)
                    // Top right
                    position.set(new Vector2f(line.getStart()).add(constant, 0.0f));
                else if (i == 5)
                    // Bottom right
                    position.set(new Vector2f(line.getEnd()).add(constant, 0.0f));
                else if (i == 6)
                    position.set(new Vector2f(line.getEnd()).sub(constant, 0.0f));
                else if (i == 7)
                    position.set(new Vector2f(line.getStart()).sub(constant, 0.0f));

                // 3, 2, 0, 0, 2, 1           7, 6, 4, 4, 6, 5
                /*
                      [3]-----[2]
                       |       |
                       |       |
                      [0]-----[1]
                */

                Vector4f color = line.getColor();

                // Load position
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                // Load the color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                vertexArray[index + 6] = color.w;
                index += 7;
            }
        }

        GlObjects.replaceVboData(vboID, vertexArray);

        // Use our shader
        shader.use();
        shader.uploadMat4f("uProjection", SiriusTheFox.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", SiriusTheFox.getCurrentScene().getCamera().getViewMatrix());

        // Bind vao
        GlObjects.bindVao(vaoID);
        GlObjects.enableAttributes(2);

        // Draw the batch
        glDrawElements(GL_TRIANGLES, line2DList.size() * 12, GL_UNSIGNED_INT, 0);
        // Bresenham's line algorithm (Optimize lines drawing)

        // Disable location
        GlObjects.disableAttributes(2);
        GlObjects.unbindVao();

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

        Camera camera = SiriusTheFox.getCurrentScene().getCamera();
        Vector2f unrealBottomLeftCamera = new Vector2f(camera.position).add(new Vector2f(-2.0f, -2.0f));
        Vector2f unrealTopRightCamera  = new Vector2f(camera.position).add(
                new Vector2f(camera.getProjectionSize()).mul(camera.getZoom())).add(new Vector2f(4.0f, 4.0f));

        boolean lineInView = ((from.x >= unrealBottomLeftCamera.x && from.x <= unrealTopRightCamera.x)
                && from.y >= unrealBottomLeftCamera.y && from.y <= unrealTopRightCamera.y)
                || ((to.x >= unrealBottomLeftCamera.x && to.x <= unrealTopRightCamera.x)
                && to.y >= unrealBottomLeftCamera.y && to.y <= unrealTopRightCamera.y);

        if (lineInView)
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

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Color color) {
        addBox2D(center, dimensions, rotation, color, 1);
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
        Vector2f bottomLeftCorner = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
        Vector2f topRightCorner = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(bottomLeftCorner.x, bottomLeftCorner.y),  // Bottom left corner
                new Vector2f(topRightCorner.x, topRightCorner.y),      // Top right corner
                new Vector2f(topRightCorner.x, bottomLeftCorner.y),    // Bottom right corner
                new Vector2f(bottomLeftCorner.x, topRightCorner.y),    // Top left corner
        };

        // If it has been rotated
        if (rotation != 0.0f) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, rotation, center);
            }
        }

        addLine2D(vertices[0], vertices[2], color, lifetime); // BL -> BR
        addLine2D(vertices[0], vertices[3], color, lifetime); // BL -> TL
        addLine2D(vertices[1], vertices[3], color, lifetime); // TR -> TL
        addLine2D(vertices[1], vertices[2], color, lifetime); // TR -> BR
    }

    // ===================================================================
    // Add Circle2D
    // ===================================================================

    public static void addCircle(Vector2f center, float radius, int lifeTime) {
        addCircle(center, radius, Color.GREEN, lifeTime);
    }

    public static void addCircle(Vector2f center, float radius, Color color) {
        addCircle(center, radius, color, 1);
    }

    /**
     * Adds a circle to the screen
     *
     * @param center   center's coordinates of the circle
     * @param radius   how big the circle is
     * @param color    its color
     * @param lifeTime how much time it will take to disappear in fps/second units
     */
    public static void addCircle(Vector2f center, float radius, Color color, int lifeTime) {
        // How many points the circle is going to have
        Vector2f[] points = new Vector2f[20];

        // Increment in degrees per segment
        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(radius, 0);
            JMath.rotate(tmp, currentAngle, new Vector2f());

            points[i] = new Vector2f(tmp).add(center);

            if (i > 0) {
                addLine2D(points[i - 1], points[i], color, lifeTime);
            }

            currentAngle += increment;
        }

        addLine2D(points[points.length - 1], points[0], color, lifeTime);
    }
}
