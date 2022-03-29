package physics2d.components;

import org.joml.Vector2f;

public class CircleCollider extends Collider2d {
    private float radius = 1.0f;

    /**
     * Gets radius size
     * @return Radius' size
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets radius' size
     * @param radius The size that is wished
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }
}
