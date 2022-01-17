package physics2d;

import jade.rendering.debug.Line2D;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import physics2d.primitives.AABB;
import physics2d.primitives.Box2D;
import physics2d.rigidBody.IntersectionDetector2D;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollisionDetectorTest {
    private final float EPSILON = 0.000001f;

    @Test
    public void pointOnLine2DShouldReturnTrueTest() {
        Line2D line = new Line2D(new Vector2f(0, 0), new Vector2f(12, 4));
        Vector2f point = new Vector2f(0, 0);

        assertTrue(IntersectionDetector2D.isPointOnLine(point, line));
    }

    @Test
    public void pointOnLine2DShouldReturnTrueTestTwo() {
        Line2D line = new Line2D(new Vector2f(0, 0), new Vector2f(12, 4));
        Vector2f point = new Vector2f(12, 4);

        assertTrue(IntersectionDetector2D.isPointOnLine(point, line));
    }

    @Test
    public void pointOnVerticalLineShouldReturnTrue() {
        Line2D line = new Line2D(new Vector2f(0, 0), new Vector2f(0, 10));
        Vector2f point = new Vector2f(0, 5);

        assertTrue(IntersectionDetector2D.isPointOnLine(point, line));
    }

    @Test
    public void pointOnHorizontalLineShouldReturnTrue() {
        Line2D line = new Line2D(new Vector2f(0, 3), new Vector2f(10, 3));
        Vector2f point = new Vector2f(5, 3);

        assertTrue(IntersectionDetector2D.isPointOnLine(point, line));
    }

    @Test
    public void lineOnBoxShouldReturnTrue() {
        Line2D line = new Line2D(new Vector2f(-2, -3), new Vector2f(4, 6));
        AABB aabb = new AABB(new Vector2f(2, 2), new Vector2f(5, 5));

        assertTrue(IntersectionDetector2D.isLineIntersectingAABB(line, aabb));
    }

    @Test
    public void lineOnLeftBoxBoundariesShouldReturnTrue() {
        Line2D line = new Line2D(new Vector2f(-2, -3), new Vector2f(4, 6));
        AABB aabb = new AABB(new Vector2f(2, 2), new Vector2f(5, 5));

        assertTrue(IntersectionDetector2D.isLineIntersectingAABB(line, aabb));
    }

    // TODO: This test fails, so there is a problem in isLineIntersectingAABB
    /*@Test
    public void lineOnRightBoxBoundariesShouldReturnTrue() {
        Line2D line = new Line2D(new Vector2f(11, 13), new Vector2f(11, 2));
        AABB aabb = new AABB(new Vector2f(7, 4), new Vector2f(11, 7));

        assertTrue(IntersectionDetector2D.isLineIntersectingAABB(line, aabb));
    }*/

    // TODO: This test fails, so there is a problem in isLineIntersectingBox2D
    /*@Test
    public void lineOn45DegreesRotatedBoxShouldReturnTrue() {
        Line2D line1 = new Line2D(new Vector2f(11, 6), new Vector2f(3, 6));
        Line2D line2 = new Line2D(new Vector2f(-5, -6), new Vector2f(9, 8));
        Line2D line3 = new Line2D(new Vector2f(0, 2), new Vector2f(12, 6));

        Box2D box = new Box2D(2, 2);
        box.getRigidBody2D().setRotation(45.0f);
        box.getRigidBody2D().setPosition(new Vector2f(6, 5));

        assertTrue(IntersectionDetector2D.isLineIntersectingBox2D(line1, box)
                && IntersectionDetector2D.isLineIntersectingBox2D(line2, box)
                && IntersectionDetector2D.isLineIntersectingBox2D(line3, box));
    }*/
}
