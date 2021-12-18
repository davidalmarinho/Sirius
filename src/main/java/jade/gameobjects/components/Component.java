package jade.gameobjects.components;

import jade.gameobjects.GameObject;

public abstract class Component {
    /* Marked as transient, because each component as its parent game object and each
     * parent game object as its components and that components have, again, a parent game object.
     */
    public transient GameObject gameObject = null;
    /**
     * We have this method, because we can't access the gameObject
     * inside builder's method.
     */
    public void start() {

    }

    public void update(float dt) {

    }

    public void imgui() {

    }
}
