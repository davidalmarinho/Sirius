package sirius.editor.imgui.sprite_animation_window;

import imgui.*;
import imgui.type.ImBoolean;

public class SpriteAnimationWindow {
    private static SpriteAnimationWindow instance;
    private ConfigChild configChild;
    private StateMachineChild stateMachineChild;

    private SpriteAnimationWindow() {
        this.configChild = new ConfigChild();
        this.stateMachineChild = new StateMachineChild();
    }

    public void imgui() {
        if (ImGui.begin("Sprite Animation Window", new ImBoolean(true))) {
            ImGui.text("Mouse Left: drag to add lines, or drag inside the boxes to move them." +
                    "\nMouse Middle: drag to scroll," +
                    "\nMouse Right: click for context menu.");
            ImGui.checkbox("Show config: ", configChild.showConfigChild);
            ImGui.checkbox("Show state machine: ", stateMachineChild.showStateMachineChild);

            // Put config child in left side of the sprite animation window and if the state machine child is active,
            // config child's size has to be 240
            if (configChild.isShowConfigChild()) {
                if (stateMachineChild.isShowStateMachineChild())
                    configChild.imgui(new ImVec2(300, ImGui.getContentRegionAvailY()));
                else
                    configChild.imgui(ImGui.getContentRegionAvail());
            }

            if (stateMachineChild.isShowStateMachineChild())
                stateMachineChild.imgui(ImGui.getContentRegionAvail());

        }
        ImGui.end();
    }

    public static SpriteAnimationWindow get() {
        if (instance == null) instance = new SpriteAnimationWindow();

        return instance;
    }

    public static StateMachineChild getStateMachineChild() {
        return get().stateMachineChild;
    }
}
