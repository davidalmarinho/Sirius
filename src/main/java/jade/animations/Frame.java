package jade.animations;

import gameobjects.components.Sprite;

public class Frame {
    public Sprite sprite;
    public float frameTime;

    public Frame(Sprite sprite, float time) {
        this.sprite = sprite;
        this.frameTime = time;
    }

    public Frame() {

    }
}
