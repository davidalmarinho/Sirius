package jade.gameobjects.components;

import jade.gameobjects.GameObject;

public abstract class Component {
    public GameObject gameObject = null;
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
