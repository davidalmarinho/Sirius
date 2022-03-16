package jade.animations;

import gameobjects.components.Sprite;

import java.util.ArrayList;
import java.util.List;

public class AnimationState {
    public String title;
    public List<Frame> animationFramesList = new ArrayList<>();

    // Show something if the animation system crashes
    private static Sprite defaultSprite = Sprite.Builder.newInstance().build();

    private transient float timeTracker = 0.0f;
    private transient int currentSprite = 0;
    private boolean doesLoop = false;

    public void addFrame(Sprite sprite, float frameTime) {
        animationFramesList.add(new Frame(sprite, frameTime));
    }

    public boolean isDoesLoop() {
        return doesLoop;
    }

    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    public void update(float dt) {
        // Make sure to not get ArrayIndexOutOfBoundsException
        if (currentSprite >= animationFramesList.size()) return;

        timeTracker -= dt;

        if (timeTracker <= 0) {
            if (currentSprite == animationFramesList.size() - 1 || !doesLoop)
                currentSprite = (currentSprite + 1) % animationFramesList.size();

            timeTracker = animationFramesList.get(currentSprite).frameTime;
        }
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < animationFramesList.size()) return animationFramesList.get(currentSprite).sprite;

        return defaultSprite;
    }
}
