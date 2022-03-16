package jade.animations;

import gameobjects.components.Sprite;
import jade.utils.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class AnimationState {
    public String title;
    public List<Frame> animationFrameList = new ArrayList<>();

    // Show something if the animation system crashes
    private static Sprite defaultSprite = Sprite.Builder.newInstance().build();

    private transient float timeTracker = 0.0f;
    private transient int currentSprite = 0;
    private boolean doesLoop = false;

    public void addFrame(Sprite sprite, float frameTime) {
        animationFrameList.add(new Frame(sprite, frameTime));
    }

    public boolean isDoesLoop() {
        return doesLoop;
    }

    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }

    public void refreshTextures() {
        for (Frame frame : animationFrameList) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilePath()));
        }
    }

    public void update(float dt) {
        // Make sure to not get ArrayIndexOutOfBoundsException
        if (currentSprite >= animationFrameList.size()) return;

        timeTracker -= dt;

        if (timeTracker <= 0) {
            if (currentSprite != animationFrameList.size() - 1 || doesLoop)
                currentSprite = (currentSprite + 1) % animationFrameList.size();

            timeTracker = animationFrameList.get(currentSprite).frameTime;
        }
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrameList.size()) return animationFrameList.get(currentSprite).sprite;

        return defaultSprite;
    }
}
