package physcis2d.components;

import gameobjects.components.Component;
import org.joml.Vector2f;

public class Box2DCollider extends Component {
    private Vector2f halfSize = new Vector2f(1.0f);

    /**
     * Gets the half width and the half height
     * @return halfSize vector
     */
    public Vector2f getHalfSize() {
        return halfSize;
    }

    /**
     * Sets half's size float vector
     * @param halfSize Sets halfSize vector
     */
    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    /**
     * Sets half's size float vector
     * @param halfWidth HalfWidth that we wish on halfSize.x vector
     * @param halfHeight HalfHeight that we wish on halfSize.y vector
     */
    public void setHalfSize(float halfWidth, float halfHeight) {
        this.halfSize.set(halfWidth, halfHeight);
    }
}
