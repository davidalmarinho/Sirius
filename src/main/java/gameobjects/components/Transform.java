package gameobjects.components;

import gameobjects.components.editor.JImGui;
import org.joml.Vector2f;

public class Transform extends Component {
    public Vector2f position, scale;
    public float rotation;
    public int zIndex;

    public Transform(Vector2f position, Vector2f scale, int zIndex) {
        this.position = position;
        this.scale = scale;
        this.zIndex = zIndex;
    }

    public Transform() {
        this(new Vector2f(), new Vector2f(), 0);
    }

    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale), this.zIndex);
    }

    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
        to.zIndex = this.zIndex;
    }

    public void imgui() {
        JImGui.drawVec2Control("Position", this.position);
        JImGui.drawVec2Control("Scale", this.scale, 32.0f);
        JImGui.dragFloat("Rotation", this.rotation);
        JImGui.dragInt("Z-Index", this.zIndex);
    }

    // All classes have this method, but we can change it
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;

        if (o instanceof Transform) {
            Transform transform = (Transform) o;
            return transform.position.equals(this.position) && transform.scale.equals(this.scale)
                    && transform.rotation == this.rotation && transform.zIndex == this.zIndex;
        }

        return false;
    }
}
