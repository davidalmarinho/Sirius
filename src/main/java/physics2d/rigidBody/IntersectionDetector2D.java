package physics2d.rigidBody;

import jade.rendering.debug.Line2D;
import jade.utils.JMath;
import org.joml.Vector2f;
import physics2d.primitives.*;

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

        // Can't divide y/0 -- When lines are horizontal, a y/0 division happens
        if (dx == 0.0f) return JMath.compare(point.x, line.getStart().x);

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

    // ==================================================
    // Line vs. Primitive tests
    // ==================================================

    /**
     * Checks if a line is intersecting a circle
     *
     * @param line Line that is desired to check if it is intersecting a circle
     * @param circle Circle that is desired to check if it is intersecting a line
     * @return true if a line is intersecting with a circle
     */
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

    /**
     * Checks if a line is intersecting a box
     * If the box is rotated, you should use {@link #isLineIntersectingBox2D(Line2D, Box2D)} instead
     *
     * @param line Line that may be intersecting a box
     * @param box Box that may be intersected by a line
     * @return true if the line is intersecting a box
     */
    public static boolean isLineIntersectingAABB(Line2D line, AABB box) {
        // Check if the points are in the box -- if they are already in the box, means that the line is intersecting the box
        if (isPointInAABB(line.getStart(), box) || isPointInAABB(line.getEnd(), box)) {
            return true;
        }

        // Unit vector -- will show us the direction of the line
        Vector2f unitVec = new Vector2f(line.getEnd()).sub(line.getStart());
        unitVec.normalize();
        unitVec.x = (unitVec.x != 0) ? 1.0f / unitVec.x : 0f;
        unitVec.y = (unitVec.y != 0) ? 1.0f / unitVec.y : 0f;

        Vector2f boxLeftBottomCorner = box.getBottomLeftCorner();
        Vector2f boxTopRightCorner = box.getTopRightCorner();

        // Parse to unit vectors the line.getStart() and the both corner of the boxes
        boxLeftBottomCorner.sub(line.getStart()).mul(unitVec);
        boxTopRightCorner.sub(line.getStart()).mul(unitVec);

        float tmin = Math.max(Math.min(boxLeftBottomCorner.x, boxTopRightCorner.x), Math.min(boxLeftBottomCorner.y, boxTopRightCorner.y));
        float tmax = Math.min(Math.max(boxLeftBottomCorner.x, boxTopRightCorner.x), Math.max(boxLeftBottomCorner.y, boxTopRightCorner.y));

        // Means that the line isn't intersecting the box
        if (tmax < 0 || tmin > tmax) {
            return false;
        }

        float t = (tmin < 0f) ? tmax : tmin;

        return t > 0f && t * t < line.lengthSquared();
    }

    /**
     * Checks if a line is intersecting with a box.
     * If the box doesn't rotate, you should use {@link #isLineIntersectingAABB(Line2D, AABB)} instead
     * since this method heavier than the one mentioned before
     *
     * @param line Line that may be intersecting a box
     * @param box Box (maybe rotated or not) that may be intersected by a line
     * @return true if the line is intersecting a box
     */
    public static boolean isLineIntersectingBox2D(Line2D line, Box2D box) {
        // Gets box's rotation angle in degrees
        float theta = box.getRigidBody2D().getRotation();
        Vector2f center = box.getRigidBody2D().getPosition();

        // Gets the lineStart and the lineEnd
        Vector2f localStart = new Vector2f(line.getStart());
        Vector2f localEnd = new Vector2f(line.getEnd());

        // Rotates the lines according to box's angle
        JMath.rotate(localStart, theta, center);
        JMath.rotate(localEnd, theta, center);

        /* Now we have all what we need to check if a line is intersecting with a rotated box,
         * since we rotated the lines too
         */
        Line2D localLine = new Line2D(localStart, localEnd);
        AABB aabb = new AABB(box.getBottomLeftCorner(), box.getTopRightCorner());

        return isLineIntersectingAABB(localLine, aabb);
    }

    // ==================================================
    // Ray-casts
    // ==================================================

    /**
     * Checks if a circle is being ray-casting
     *
     * @param circle The circle that will be checked the ray-cast
     * @param ray A ray-cast
     * @param result Optional, if you aren't really ray-casting, this value might be null.
     * @return true if a circle is being ray-casted.
     */
    public static boolean raycast(Circle circle, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        Vector2f originToCircle = new Vector2f(circle.getCenter()).sub(ray.getOrigin());
        float radiusSquared = circle.getRadius() * circle.getRadius();
        float originToCircleLengthSquared = originToCircle.lengthSquared();

        // Project the vector from the ray origin onto the direction of the ray
        float a = originToCircle.dot(ray.getDirection());
        float bSquare = originToCircleLengthSquared - (a * a);

        // Indicates that the circle wasn't hit
        if (radiusSquared - bSquare < 0.0f) {
            return false;
        }

        // This is what makes ray-casting slow. 5x slower than square the length
        float f = (float) Math.sqrt(radiusSquared - bSquare);
        float t = 0;

        // if originToCircleLengthSquared is less than radiusSquared, means that the ray starts inside the circle
        t = originToCircleLengthSquared < radiusSquared ? (a + f) : a - f;

        if (result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(ray.getDirection().mul(t));
            Vector2f normal = new Vector2f(point).sub(circle.getCenter());
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }
}
