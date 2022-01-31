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

        Vector2f leftBottomCorner = box.getLocalBottomLeftCorner();
        Vector2f topRightCorner = box.getLocalTopRightCorner();

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
        AABB aabb = new AABB(box.getLocalBottomLeftCorner(), box.getLocalTopRightCorner());

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
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(point).sub(circle.getCenter());
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    /**
     * Ray-cast against an un-rotated box.
     * If you want to ray-cast against a rotated box you might use {@link #raycast(Box2D, Ray2D, RaycastResult)} method.
     *
     * @param box Un-rotated box itself
     * @param ray Ray that will be checked collision against the box
     * @param result Ray-casting result
     * @return true if the ray-cast happens
     */
    public static boolean raycast(AABB box, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        // Unit vector -- will show us the direction of the line
        Vector2f unitVec = ray.getDirection();
        unitVec.normalize();
        unitVec.x = (unitVec.x != 0) ? 1.0f / unitVec.x : 0f;
        unitVec.y = (unitVec.y != 0) ? 1.0f / unitVec.y : 0f;

        Vector2f boxLeftBottomCorner = box.getBottomLeftCorner();
        Vector2f boxTopRightCorner = box.getTopRightCorner();

        // Parse to unit vectors the line.getStart() and the both corner of the boxes
        boxLeftBottomCorner.sub(ray.getOrigin()).mul(unitVec);
        boxTopRightCorner.sub(ray.getOrigin()).mul(unitVec);

        float tmin = Math.max(Math.min(boxLeftBottomCorner.x, boxTopRightCorner.x), Math.min(boxLeftBottomCorner.y, boxTopRightCorner.y));
        float tmax = Math.min(Math.max(boxLeftBottomCorner.x, boxTopRightCorner.x), Math.max(boxLeftBottomCorner.y, boxTopRightCorner.y));

        // Means that the line isn't intersecting the box
        if (tmax < 0 || tmin > tmax) {
            return false;
        }

        float t = (tmin < 0f) ? tmax : tmin;

        boolean hit = t > 0f; // && t * t < ray.getMaximum();
        if (!hit) return false;

        // Means that we have our t variable
        if (result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }
    /**
     * Ray-cast against a rotated box.
     * If you want to ray-cast against an un-rotated box you might use
     * {@link #raycast(AABB, Ray2D, RaycastResult)} method.
     *
     * @param box Rotated box itself
     * @param ray Ray that will be checked collision against the box
     * @param result Ray-casting result
     * @return true if the ray-cast happens
     */
    public static boolean raycast(Box2D box, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        Vector2f halfSize = box.getHalfSize();

        // Get xAxis and yAxis
        Vector2f xAxis = new Vector2f(1, 0);
        Vector2f yAxis = new Vector2f(0, 1);
        JMath.rotate(xAxis, -box.getRigidBody2D().getRotation(), new Vector2f(0, 0));
        JMath.rotate(yAxis, -box.getRigidBody2D().getRotation(), new Vector2f(0, 0));

        Vector2f p = new Vector2f(box.getRigidBody2D().getPosition()).sub(ray.getOrigin());

        // Project the direction of the ray onto each axis of the box
        Vector2f f = new Vector2f(xAxis.dot(ray.getDirection()), yAxis.dot(ray.getDirection()));

        // Nest, project p onto every axis of the box
        Vector2f e = new Vector2f(xAxis.dot(p), yAxis.dot(p));

        float[] tArr = {0, 0, 0, 0};
        for (int i = 0; i < 2; i++) {
            if (JMath.compare(f.get(i), 0)) {
                // If the ray is parallel to the current axis, and the origin of the
                // ray isn't inside, we have no hit
                if (-e.get(i) - halfSize.get(i) > 0 || -e.get(i) + halfSize.get(i) < 0) {
                    return false;
                }

                f.setComponent(i, 0.00001f); // Set it to small value to avoid divide by zero
            }
            tArr[i * 2 + 0] = (e.get(i) + halfSize.get(i)) / f.get(i); // tmax for this axis
            tArr[i * 2 + 1] = (e.get(i) + halfSize.get(i)) / f.get(i); // tmin for this axis
        }

        float tmin = Math.max(Math.min(tArr[0], tArr[1]), Math.min(tArr[2], tArr[3]));
        float tmax = Math.min(Math.max(tArr[0], tArr[1]), Math.max(tArr[2], tArr[3]));

        float t = (tmin < 0f) ? tmax : tmin;

        boolean hit = t > 0f; // && t * t < ray.getMaximum();
        if (!hit) return false;

        // Means that we have our t variable
        if (result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    // ==================================================
    // Circle vs. Primitive tests
    // ==================================================
    public static boolean isCircleIntersectingLine(Circle circle, Line2D line) {
        return isLineIntersectingCircle(line, circle);
    }

    /**
     * Checks if 2 circles are intersecting.
     *
     * @param c1 First circle
     * @param c2 Second circle
     * @return true if the 2 circles are intersecting
     */
    public static boolean isCircleIntersectingCircle(Circle c1, Circle c2) {
        // c1 center
        Vector2f A = new Vector2f(c1.getCenter());

        // c2 center
        Vector2f B = new Vector2f(c2.getCenter());

        // Vector between the centers
        Vector2f AB = new Vector2f(B).sub(A);

        float radiiSum = c1.getRadius() + c2.getRadius();

        // If vector's length is less than the sum of the 2 radius, means that the circles are intersecting
        return AB.lengthSquared() <= radiiSum * radiiSum;
    }

    /**
     * Checks if a circle is intersecting with an un-rotated box.
     * If you want to test collision between a box that is able to rotate and a circle, you might use
     * {@link #isCircleIntersectingBox2D(Circle, Box2D)} instead.
     *
     * @param circle Circle that will be tested the collision.
     * @param box Box that will be tested the collision.
     * @return true if they are intersecting with each other
     */
    public static boolean isCircleIntersectingAABB(Circle circle, AABB box) {
        Vector2f bottomLeftCorner = box.getBottomLeftCorner();
        Vector2f topRightCorner = box.getTopRightCorner();

        // The closest point to circle, for now, is the circle's center itself
        Vector2f closestPointToCircle = new Vector2f(circle.getCenter());

        // Put x-axis in box's boundaries
        if (closestPointToCircle.x < bottomLeftCorner.x) closestPointToCircle.x = bottomLeftCorner.x;
        else if (closestPointToCircle.x > topRightCorner.x) closestPointToCircle.x = topRightCorner.x;

        // Put y-axis in box's boundaries
        if (closestPointToCircle.y < bottomLeftCorner.y) closestPointToCircle.y = bottomLeftCorner.y;
        else if (closestPointToCircle.y > topRightCorner.y) closestPointToCircle.y = topRightCorner.y;

        // Catch the closest point between the circle and the box
        Vector2f circleToBox = new Vector2f(circle.getCenter()).sub(closestPointToCircle);
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    /**
     * Checks if a circle is intersecting with a rotated box.
     * If you want to test collision between an un-rotated box and a circle, you might use
     * {@link #isCircleIntersectingAABB(Circle, AABB)} instead, since it is quicker than this one.
     *
     * @param circle Circle that will be tested the collision.
     * @param box Box that will be tested the collision.
     * @return true if they are intersecting with each other
     */
    public static boolean isCircleIntersectingBox2D(Circle circle, Box2D box) {
        // Treat the box just like an AABB, after we rotate the stuff
        Vector2f bottomLeftCorner = new Vector2f();
        Vector2f topRightCorner = new Vector2f(box.getHalfSize()).mul(2.0f);

        // Create a circle in box's local space
        Vector2f rotat = new Vector2f(circle.getCenter()).sub(box.getRigidBody2D().getPosition());
        JMath.rotate(rotat, -box.getRigidBody2D().getRotation(), new Vector2f());
        Vector2f localCirclePos = new Vector2f(rotat).add(box.getHalfSize());

        // The closest point to circle, for now, is the circle's center itself
        Vector2f closestPointToCircle = new Vector2f(localCirclePos);

        // Put x-axis in box's boundaries
        if (closestPointToCircle.x < bottomLeftCorner.x) closestPointToCircle.x = bottomLeftCorner.x;
        else if (closestPointToCircle.x > topRightCorner.x) closestPointToCircle.x = topRightCorner.x;

        // Put y-axis in box's boundaries
        if (closestPointToCircle.y < bottomLeftCorner.y) closestPointToCircle.y = bottomLeftCorner.y;
        else if (closestPointToCircle.y > topRightCorner.y) closestPointToCircle.y = topRightCorner.y;

        // Catch the closest point between the circle and the box
        Vector2f circleToBox = new Vector2f(localCirclePos).sub(closestPointToCircle);
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    // ==================================================
    // AABB vs. Primitive tests
    // ==================================================

    /**
     * Checks if an un-rotated box is intersecting with a circle.
     * If you want to test collision between a box that is able to rotate and a circle, you might use
     * {@link #isBox2DIntersectingCircle(Box2D, Circle)} instead.
     *
     * @param box Box that will be tested the collision.
     * @param circle Circle that will be tested the collision.
     * @return true if they are intersecting with each other
     */
    public static boolean isAABBIntersectingCircle(AABB box, Circle circle) {
        return isCircleIntersectingAABB(circle, box);
    }

    /**
     * Checks if a circle is intersecting with a rotated box.
     * If you want to test collision between an un-rotated box and a circle, you might use
     * {@link #isAABBIntersectingCircle(AABB, Circle)} instead, since it is quicker than this one.
     *
     * @param box Box that will be tested the collision.
     * @param circle Circle that will be tested the collision.
     * @return true if they are intersecting with each other
     */
    public static boolean isBox2DIntersectingCircle(Box2D box, Circle circle) {
        return isCircleIntersectingBox2D(circle, box);
    }

    /**
     * Checks collisions between 2 un-rotated boxes.
     *
     * @param box1 First un-rotated box
     * @param box2 Second un-rotated box
     * @return true if the 2 boxes are intersecting with each other
     */
    public static boolean isAABBIntersectingAABB(AABB box1, AABB box2) {
        // Axis aligned (1, 0) (0, 1)
        Vector2f[] axisToTest = {new Vector2f(0, 1), new Vector2f(1, 0)};
        for (Vector2f vector2f : axisToTest) {
            if (!isOverlappingOnAxis(box1, box2, vector2f))
                return false;
        }
        return true;
    }

    /**
     * Checks collision between an un-rotated box and with a rotated box
     *
     * @param aabb Un-rotated box
     * @param box2D Rotated box
     * @return true if are intersecting with each other
     */
    public static boolean isAABBIntersectingBox2D(AABB aabb, Box2D box2D) {
        Vector2f[] axisToTest = {
                new Vector2f(0, 1), new Vector2f(1, 0),
                new Vector2f(0, 1), new Vector2f(1, 0)
        };
        JMath.rotate(axisToTest[2], box2D.getRigidBody2D().getRotation(), new Vector2f());
        JMath.rotate(axisToTest[3], box2D.getRigidBody2D().getRotation(), new Vector2f());

        for (int i=0; i < axisToTest.length; i++) {
            if (!isOverlappingOnAxis(aabb, box2D, axisToTest[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks collision between an un-rotated box and with a rotated box
     *
     * @param box1 First rotated box
     * @param box2 Second rotated box
     * @return true if are intersecting with each other
     */
    public static boolean isBox2DIntersectingBox2D(Box2D box1, Box2D box2) {
        Vector2f[] axisToTest = {
                new Vector2f(0, 1), new Vector2f(1, 0),
                new Vector2f(0, 1), new Vector2f(1, 0)
        };
        JMath.rotate(axisToTest[0], box1.getRigidBody2D().getRotation(), new Vector2f());
        JMath.rotate(axisToTest[1], box1.getRigidBody2D().getRotation(), new Vector2f());
        JMath.rotate(axisToTest[2], box2.getRigidBody2D().getRotation(), new Vector2f());
        JMath.rotate(axisToTest[3], box2.getRigidBody2D().getRotation(), new Vector2f());

        for (Vector2f vector2f : axisToTest) {
            if (!isOverlappingOnAxis(box1, box2, vector2f))
                return false;
        }
        return true;
    }

    private static boolean isOverlappingOnAxis(AABB box1, AABB box2, Vector2f axis) {
        Vector2f interval1 = getInterval(box1, axis);
        Vector2f interval2 = getInterval(box2, axis);
        return (interval2.x <= interval1.y) && (interval1.x <= interval2.y);
    }

    private static boolean isOverlappingOnAxis(AABB box1, Box2D box2, Vector2f axis) {
        Vector2f interval1 = getInterval(box1, axis);
        Vector2f interval2 = getInterval(box2, axis);
        return ((interval2.x <= interval1.y) && (interval1.x <= interval2.y));
    }

    private static boolean isOverlappingOnAxis(Box2D box1, Box2D box2, Vector2f axis) {
        Vector2f interval1 = getInterval(box1, axis);
        Vector2f interval2 = getInterval(box2, axis);
        return (interval2.x <= interval1.y) && (interval1.x <= interval2.y);
    }

    private static Vector2f getInterval(AABB rect, Vector2f axis) {
        Vector2f result = new Vector2f();

        Vector2f bottomLeftCorner = rect.getBottomLeftCorner();
        Vector2f topRightCorner = rect.getTopRightCorner();

        Vector2f squareVertices[] = {
                new Vector2f(bottomLeftCorner.x, bottomLeftCorner.y),
                new Vector2f(bottomLeftCorner.x, topRightCorner.y),
                new Vector2f(topRightCorner.x, bottomLeftCorner.y),
                new Vector2f(topRightCorner.x, topRightCorner.y)
        };

        result.x = axis.dot(squareVertices[0]);
        result.y = result.x;
        for (int i = 1; i < 4; i++) {
            float projection = axis.dot(squareVertices[i]);
            if (projection < result.x) result.x = projection;
            if (projection > result.y) result.y = projection;
        }

        return result;
    }

    private static Vector2f getInterval(Box2D rect, Vector2f axis) {
        Vector2f result = new Vector2f();

        Vector2f[] squareVertices = rect.getVertices();

        result.x = axis.dot(squareVertices[0]);
        result.y = result.x;
        for (int i = 1; i < 4; i++) {
            float projection = axis.dot(squareVertices[i]);
            if (projection < result.x) result.x = projection;
            if (projection > result.y) result.y = projection;
        }

        return result;
    }
}
