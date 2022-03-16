package jade.animations;

import gameobjects.components.Component;
import gameobjects.components.SpriteRenderer;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Component {
    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> animationStateList = new ArrayList<>();
    private transient AnimationState currentState = null;
    private String defaultStateTitle = "";

    public void addStateTrigger(String from, String to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addState(AnimationState state) {
        this.animationStateList.add(state);
    }

    // Transfers us to the correct state depending on the trigger
    public void trigger(String trigger) {
        for (StateTrigger stateTrigger : stateTransfers.keySet()) {
            // Means we are on the state
            if (stateTrigger.state.equals(currentState.title) && stateTrigger.trigger.equals(trigger)) {
                if (stateTransfers.get(stateTrigger) != null) {
                    int newStateIndex = -1;
                    for (int i = 0; i < animationStateList.size(); i++) {
                        AnimationState currentAnimationState = new AnimationState();
                        if (currentAnimationState.title.equals(stateTransfers.get(stateTrigger))) {
                            newStateIndex = i;
                            break;
                        }
                    }

                    if (newStateIndex > -1) {
                        currentState = animationStateList.get(newStateIndex);
                        return;
                    }
                }
                return;
            }
        }

        System.err.println("Unable to find trigger '" + trigger + "'.");
    }

    @Override
    public void start() {
        for (AnimationState animationState : animationStateList) {
            if (animationState.title.equals(defaultStateTitle)) {
                currentState = animationState;
                break;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

            if (spriteRenderer != null) spriteRenderer.setSprite(currentState.getCurrentSprite());
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

            if (spriteRenderer != null) spriteRenderer.setSprite(currentState.getCurrentSprite());
        }
    }

    @Override
    public void imgui() {
        int index = 0;
        for (AnimationState animationState : animationStateList) {
            ImString title = new ImString(animationState.title);
            ImGui.inputText("State: ", title);
            animationState.title = title.get();

            // Checkbox for doesLoop boolean
            ImBoolean doesLoop = new ImBoolean(animationState.isDoesLoop());
            ImGui.checkbox("Does Loop", doesLoop);
            animationState.setLoop(doesLoop.get());

            for (Frame frame : animationState.animationFramesList) {
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame (" + index + ") Time: ", tmp, 0.01f);
                frame.frameTime = tmp[0];
                index++;
            }
        }
    }

    private class StateTrigger {
        public String state;
        public String trigger;

        public StateTrigger(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != StateTrigger.class) return false;
            StateTrigger st2 = (StateTrigger) o;

            return st2.trigger.equals(this.trigger) && st2.state.equals(this.state);
        }

        @Override
        public int hashCode() {
            return Objects.hash(trigger, state);
        }
    }
}
