package physics2d;

import jade.rendering.debug.Line2D;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import physics2d.rigidBody.IntersectionDetector2D;

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
}
