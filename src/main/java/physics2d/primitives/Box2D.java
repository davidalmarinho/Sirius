package physics2d.primitives;

import jade.utils.JMath;
import org.joml.Vector2f;
import physics2d.rigidBody.RigidBody2D;

public class Box2D {
    private final Vector2f size;
    private final Vector2f halfSize;
    private RigidBody2D rigidBody2D;

    /**
     * Constructor method
     *
     * @param width wished of the box
     * @param height wished of the box
     */
    public Box2D(float width, float height) {
        this.size = new Vector2f(width, height);
        this.halfSize = new Vector2f(size.mul(0.5f));
        rigidBody2D = new RigidBody2D();
    }

    /**
     * Constructor method
     *
     * @param width wished of the box
     * @param height wished of the box
     */
    public Box2D(Vector2f position, float width, float height) {
        this(width, height);
        rigidBody2D.setTransform(position);
    }

    /**
     * Gets the bottom left corner coordinates of the box
     *
     * @return box's bottom left corner coordinates
     */
    public Vector2f getLocalBottomLeftCorner() {
        return new Vector2f(rigidBody2D.getPosition()).sub(halfSize);
    }

    /**
     * Gets the top right corner coordinates of the box
     *
     * @return box's top right corner coordinates
     */
    public Vector2f getLocalTopRightCorner() {
        return new Vector2f(rigidBody2D.getPosition()).add(halfSize);
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
        Vector2f topRightCorner = getLocalTopRightCorner();
        Vector2f bottomLeftCorner = getLocalBottomLeftCorner();

        Vector2f[] vertices = {
                new Vector2f(bottomLeftCorner.x, bottomLeftCorner.y),  // Bottom left corner
                new Vector2f(bottomLeftCorner.x, topRightCorner.y),    // Top left corner
                new Vector2f(topRightCorner.x, bottomLeftCorner.y),    // Bottom right corner
                new Vector2f(topRightCorner.x, topRightCorner.y)       // Top right corner
        };

        // Check if the box is rotated
        if (rigidBody2D.getRotation() != 0.0f) {
            for (int i = 0; i < vertices.length; i++) {
                Vector2f currentVertex = vertices[i];
                // Rotates point(vec2f) about center (vec2f) by rotation (float in degrees)
                JMath.rotate(currentVertex, rigidBody2D.getRotation(), rigidBody2D.getPosition());
            }
        }

        return vertices;
    }

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public RigidBody2D getRigidBody2D() {
        return rigidBody2D;
    }

    public void setRigidBody2D(RigidBody2D rigidBody2D) {
        this.rigidBody2D = rigidBody2D;
    }
}
