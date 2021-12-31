package physics2d.rigidBody;

import jade.rendering.debug.Line2D;
import org.joml.Vector2f;
import physics2d.primitives.Circle;

// Detects 2 objects when they are intersecting
public class IntersectionDetector2D {
    // ==================================================
    // Points vs. Primitive tests
    // ==================================================

    /**
     * Checks if a point intersects with a line
     *
     * @param point point's coordinates
     * @param line line itself
     * @return if a point intersects with a line
     */
    public static boolean isPointOnLine(Vector2f point, Line2D line) {
        /*  Math required:
         *    y = mx + b
         *    m = (y1-y2) / (x1-x2)
         *    b = y - mx
         */

        Vector2f begin = line.getBegin();
        Vector2f end = line.getEnd();

        float dy = end.y - begin.y;
        float dx = end.x - begin.x;
        float m = dy / dx;
        float b = end.y - m * end.x;

        return point.y == m * point.x + b;
    }

    // ==================================================
    // Line vs. Primitive tests
    // ==================================================
}
