package physics2d_from_scratch.primitives;

import org.joml.Vector2f;

public class Ray2D {
    private Vector2f origin;
    private Vector2f direction;

    /**
     * Ray2D constructor method
     *
     * @param origin The origin of the ray-cast
     * @param direction Direction of the ray-cast. It is normalized by default, so you shouldn't normalize it too.
     */
    public Ray2D(Vector2f origin, Vector2f direction) {
        this.origin = origin;
        this.direction = direction;
        this.direction.normalize();
    }

    public Vector2f getOrigin() {
        return this.origin;
    }

    public Vector2f getDirection() {
        return this.direction;
    }
}
