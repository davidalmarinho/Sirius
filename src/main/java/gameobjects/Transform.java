package gameobjects;

import org.joml.Vector2f;

public class Transform {
    public Vector2f position, scale;
    public float rotation;

    public Transform() {
       init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    private void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    public void copy(Transform to) {
        to.position.set(this.position);
        to.scale.set(this.scale);
    }

    // All classes have this method, but we can change it
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;

        if (o instanceof Transform) {
            Transform transform = (Transform) o;
            return transform.position.equals(this.position) && transform.scale.equals(this.scale);
        }

        return false;
    }
}
