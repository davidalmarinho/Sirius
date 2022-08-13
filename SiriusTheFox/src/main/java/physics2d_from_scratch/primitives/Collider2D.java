package physics2d_from_scratch.primitives;

import gameobjects.components.Component;
import org.joml.Vector2f;

public abstract class Collider2D extends Component {
    protected Vector2f offset = new Vector2f();

    // TODO: 31/12/2021 Implement this
    public abstract float getInertialTensor(float mass);
}
