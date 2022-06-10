package sirius.animations;

import gameobjects.components.Component;
import gameobjects.components.SpriteRenderer;
import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import sirius.editor.imgui.JImGui;
import sirius.editor.imgui.sprite_animation_window.*;
import sirius.encode_tools.Encode;
import sirius.utils.AssetPool;

import java.util.*;

public class StateMachine extends Component {
    private transient String animationBlueprintFilepath;
    private transient int currentItem = -1;
    public HashMap<StateTrigger, String> stateTransfers;
    private List<AnimationState> animationStateList;
    private transient AnimationState currentState;
    private String defaultStateTitle;

    public StateMachine() {
        this.stateTransfers = new HashMap<>();
        this.animationStateList = new ArrayList<>();
        this.currentState = null;
        this.defaultStateTitle = "";
    }

    public void refreshTextures() {
        for (AnimationState animationState : animationStateList) {
            animationState.refreshTextures();
        }
    }

    public void addStateTrigger(String from, String to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addState(AnimationState state) {
        this.animationStateList.add(state);
    }

    private int stateIndexOf(String stateTitle) {
        for (int i = 0; i < animationStateList.size(); i++) {
            AnimationState animationState = animationStateList.get(i);
            if (animationState.title.equals(stateTitle))
                return i;
        }

        return -1;
    }

    // Transfers us to the correct state depending on the trigger
    public void trigger(String trigger) {
        for (StateTrigger stateTrigger : stateTransfers.keySet()) {
            // Means we are on the state
            if (stateTrigger.state.equals(currentState.title) && stateTrigger.trigger.equals(trigger)) {
                if (stateTransfers.get(stateTrigger) != null) {
                    int newStateIndex = stateIndexOf(stateTransfers.get(stateTrigger));

                    if (newStateIndex > -1) {
                        currentState = animationStateList.get(newStateIndex);
                    }
                }
                return;
            }
        }

        // System.err.println("Unable to find trigger '" + trigger + "'.");
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

    public void setDefaultState(String animationTitle) {
        for (AnimationState animationState : animationStateList) {
            if (animationState.title.equals(animationTitle)) {
                defaultStateTitle = animationTitle;
                if (currentState == null) {
                    currentState = animationState;
                    return;
                }
            }
        }

        System.err.println("Unable to find state '" + animationTitle + "' in set default state.");
    }

    @Override
    public void update(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

            if (spriteRenderer != null)
                spriteRenderer.setSprite(currentState.getCurrentSprite());
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

            if (spriteRenderer != null)
                spriteRenderer.setSprite(currentState.getCurrentSprite());
        }
    }

    @Override
    public void imgui() {
        String[] animationsNames = AssetPool.getAnimationsNames();

        // List available blueprints animations
        ImGui.textUnformatted("Select animation: ");

        currentItem = JImGui.list("", currentItem, animationsNames);

        String[] animationsPaths = AssetPool.getAnimationsPaths();

        // Select a blueprint animation and pull it to the game object
        if (ImGui.isMouseReleased(ImGuiMouseButton.Left) && currentItem >= 0) {
            animationBlueprintFilepath = animationsPaths[currentItem];
            AnimationBlueprint bufferAnimationBlueprint = Encode.getAnimation(animationsPaths[currentItem]);

            resetStateMachine();

            List<AnimationBox> animationBoxList = bufferAnimationBlueprint.animationBoxList;
            List<Wire> wireList = new ArrayList<>(bufferAnimationBlueprint.wireList);

            if (!animationBoxList.isEmpty()) {

                for (int i = 0; i < animationBoxList.size(); i++) {
                    AnimationBox curAnimationBox = animationBoxList.get(i);

                    AnimationState animationState = new AnimationState();
                    animationState.title = curAnimationBox.getTrigger();
                    for (Frame frame : curAnimationBox.getFrameList()) {
                        animationState.addFrame(new Frame(frame));
                    }
                    animationState.setLoop(curAnimationBox.doesLoop);
                    addState(animationState);
                }

                for (Wire wire : wireList) {
                    addStateTrigger(
                            wire.getStartPoint().getOrigin(),
                            wire.getEndPoint().getOrigin(),
                            wire.getEndPoint().getOrigin());
                }

                for (int i = 0; i < animationBoxList.size(); i++) {
                    if (animationBoxList.get(i).isFlag())
                        setDefaultState(animationBoxList.get(i).getTrigger());
                }
                gameObject.getComponent(StateMachine.class).refreshTextures();
            }

            // Reset current item
            currentItem = -1;
        }
    }

    private void resetStateMachine() {
        this.stateTransfers = new HashMap<>();
        this.animationStateList = new ArrayList<>();
        this.currentState = null;
        this.defaultStateTitle = "";
    }

    private static class StateTrigger {
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
