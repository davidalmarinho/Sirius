package physcis2d.components;

import gameobjects.components.Component;
import org.joml.Vector2f;

public abstract class Collider2d extends Component {
    private Vector2f offset = new Vector2f();

    public Vector2f getOffset() {
        return offset;
    }
}
