package physics2d.components;

import gameobjects.components.Component;
import org.joml.Vector2f;

// TODO: 29/03/2022 Delete if is not being used.
public abstract class Collider2d extends Component {
    private Vector2f offset;

    public Collider2d() {
        this.offset = new Vector2f();
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(float xOffset, float yOffset) {
        this.offset.set(xOffset, yOffset);
    }

    public void setOffset(Vector2f offset) {
        this.offset.set(offset);
    }
}
