package physics2d.primitives;

import org.joml.Vector2f;
import physics2d.rigidBody.RigidBody2D;

public class Box2D {
    private final Vector2f size;
    private final Vector2f halfSize;
    private RigidBody2D rigidBody2D = null;

    /**
     * Constructor method
     *
     * @param width wished of the box
     * @param height wished of the box
     */
    public Box2D(float width, float height) {
        this.size = new Vector2f(width, height);
        this.halfSize = new Vector2f(size.mul(0.5f));
    }

    /**
     * Gets the bottom left corner coordinates of the box
     *
     * @return box's bottom left corner coordinates
     */
    public Vector2f getBottomLeftCorner() {
        return rigidBody2D.getPosition().sub(halfSize);
    }

    /**
     * Gets the top right corner coordinates of the box
     *
     * @return box's top right corner coordinates
     */
    public Vector2f getTopRightCorner() {
        return rigidBody2D.getPosition().add(halfSize);
    }


    /**
     * Gets the vertices of the box.
     *
     * @return The positions of the vertices of the box:
     *         getVertices()[0]; -> returns the bottom left corner position
     *         getVertices()[1]; -> returns the top left corner position
     *         getVertices()[2]; -> returns the bottom right corner position
     *         getVertices()[3]; -> returns the top right corner position
     */
    public Vector2f[] getVertices() {
        Vector2f topRightCorner = getTopRightCorner();
        Vector2f bottomLeftCorner = getBottomLeftCorner();

        Vector2f[] vertices = {
                new Vector2f(bottomLeftCorner),                      // Bottom left corner
                new Vector2f(bottomLeftCorner.x, topRightCorner.y),  // Top left corner
                new Vector2f(topRightCorner.x, bottomLeftCorner.y),  // Bottom right corner
                new Vector2f(topRightCorner)                         // Top right corner
        };

        // Check if the box is rotated
        if (rigidBody2D.getRotation() != 0.0f) {
            for (Vector2f currentVertex : vertices) {
                // TODO: 31/12/2021 Implement me
                // Rotates point(vec2f) about center (vec2f) by rotation (float in degrees)
                // JMath.rotate(currentVertex, rigidBody2D.getRotation(), rigidBody2D.getRotation());
            }
        }

        return vertices;
    }
}
