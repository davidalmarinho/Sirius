package jade.gameobjects.components;

import jade.gameobjects.GameObject;

public abstract class Component {
    private GameObject parent = null;

    public void start() {

    }

    public abstract void update(float dt);

    public GameObject getGameObject() {
        return this.parent;
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }
}
