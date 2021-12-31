package physics2d.rigidBody;

import jade.rendering.debug.Line2D;
import org.joml.Vector2f;
import physics2d.primitives.AABB;
import physics2d.primitives.Box2D;
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
         *      y = mx + b
         *      m = (y1-y2) / (x1-x2)
         *      b = y - mx
         */

        Vector2f begin = line.getBegin();
        Vector2f end = line.getEnd();

        float dy = end.y - begin.y;
        float dx = end.x - begin.x;
        float m = dy / dx;
        float b = end.y - m * end.x;

        return point.y == m * point.x + b;
    }

    /**
     * Checks if the point is inside a circle
     *
     * @param point point's coordinates
     * @param circle circle itself
     * @return if the point is inside the circle
     */
    public static boolean isPointOnCircle(Vector2f point, Circle circle) {
        // If the radius length is lower than the line length, means that the point belongs to the circle
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToPoint = new Vector2f(point).sub(circleCenter);

        // length squared for the cpu do less cpu cycles than do by the length rooted. So, we also have to do radius * radius
        return centerToPoint.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    /**
     * Checks if the point is inside a box
     *
     * @param point coordinates of the point
     * @param box AABB object
     * @return if the point is inside the AABB
     */
    public static boolean isPointInAABB(Vector2f point, AABB box) {
        Vector2f leftBottomCorner = box.getBottomLeftCorner();
        Vector2f topRightCorner = box.getTopRightCorner();

        return (point.x >= leftBottomCorner.x && point.x <= topRightCorner.x) && (point.y <= topRightCorner.y && point.y >= leftBottomCorner.y);
    }

    // ==================================================
    // Line vs. Primitive tests
    // ==================================================
}
