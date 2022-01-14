package physics2d.rigidBody;

import jade.rendering.debug.Line2D;
import jade.utils.JMath;
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

        Vector2f begin = line.getStart();
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
    public static boolean isPointInCircle(Vector2f point, Circle circle) {
        // If the radius length is lower than the line length, means that the point belongs to the circle
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToPoint = new Vector2f(point).sub(circleCenter);

        // length squared for the cpu do less cpu cycles than do by the length rooted. So, we also have to do radius * radius
        return centerToPoint.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    /**
     * Checks if a point is inside a non-rotated box
     * If the box is rotated, use {@link #isPointInBox2D(Vector2f point, Box2D box) instead}
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

    /**
     * Checks if a point is inside a box
     * If the box isn't rotated, you should use {@link #isPointInAABB(Vector2f point, AABB box)}  instead, since it does less calculus,
     * but this method is fine too for non-rotated boxs.
     *
     * @param point coordinates of the point
     * @param box AABB object
     * @return if the point is inside the AABB
     */
    public static boolean isPointInBox2D(Vector2f point, Box2D box) {
        // Rotate the point based on object rotation
        Vector2f localPointBoxSpace = new Vector2f(point);
        JMath.rotate(localPointBoxSpace, box.getRigidBody2D().getRotation(), box.getRigidBody2D().getPosition());

        Vector2f leftBottomCorner = box.getBottomLeftCorner();
        Vector2f topRightCorner = box.getTopRightCorner();

        return (localPointBoxSpace.x >= leftBottomCorner.x && localPointBoxSpace.x <= topRightCorner.x)
                && (localPointBoxSpace.y <= topRightCorner.y && localPointBoxSpace.y >= leftBottomCorner.y);
    }

    public static boolean isLineIntersectingCircle(Line2D line, Circle circle) {
        // If one of the extremes of the line is already inside the circle, means that the line is intersecting the circle
        if (isPointInCircle(line.getStart(), circle) || isPointInCircle(line.getEnd(), circle))  {
            return true;
        }

        // point(A) = line.getStart()
        // point(B) = line.getEnd()
        // vec(AB) -> lineStartToLineEnd
        // vec(AB) = point(B) - point (A)
        Vector2f ab = new Vector2f(line.getEnd()).sub(line.getStart());

        // Project the point (circle position) onto ab (line segment) parameterized position t
        Vector2f circleCenter = circle.getCenter();

        // point(C) = circleCenter
        // vec(AC) -> centerToLineStart
        // vec(AC) = point(C) - point(A)
        Vector2f centerToLineStart = new Vector2f(circleCenter).sub(line.getStart());

        // Get the line's nearest point of the circle
        /*
         * Formula:
         *
         *   (A . B) / (B . B) =
         * = (|A| * |B| * cos(alpha)) / ((|B| * |B| * cos(alpha)) =
         * = |A| / |B|
         *
         * This will give a percentage of 0 to 1. If we don't get a value in [0, 1] range,
         * means that the nearest point isn't inside of the circle and,
         * consequently, means that the line isn't intersecting the circle
         * todo explain better this result
         */
        float t = centerToLineStart.dot(ab) / ab.dot(ab);

        // It is not in the line segment
        if (t < 0.0f || t > 1.0f) {
            return false;
        }

        // Find the closest point to the line segment
        Vector2f closestPoint = new Vector2f(line.getStart()).add(ab.mul(t));

        return isPointInCircle(closestPoint, circle);
    }

    // ==================================================
    // Line vs. Primitive tests
    // ==================================================
}
