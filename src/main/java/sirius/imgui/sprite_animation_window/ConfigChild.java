package sirius.imgui.sprite_animation_window;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class ConfigChild {
    public ImBoolean showConfigChild;

    public ConfigChild() {
        this.showConfigChild = new ImBoolean(false);
    }

    public void imgui(ImVec2 regionAvailable) {
        ImGui.beginChild("config", regionAvailable.x, regionAvailable.y, true,
                ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.HorizontalScrollbar);

        // Content
        ImGui.text("Trigger: ");
        ImGui.sameLine();
        ImGui.text("I'm a box");

        // TODO: 15/05/2022 Code needed for:
        // Just let edit sprite animation window's children if a game object has been selected
        // Get the trigger based on the last selected animation box, so the user knows which box is selected
        // It is convenient to draw a border around the selected box
        // Add frames / sprites (if it doesn't loop just let put one frame)
        // Set its active time
        // If it has to loop
        // Code to automatically add the states to the state machine, based on the points connection
        // Save all to one or more json files (depends on the organization)
        // Add state machine component to the current selected game object

        // Terminate child
        ImGui.endChild();
        ImGui.sameLine();
    }

    public boolean isShowConfigChild() {
        return showConfigChild.get();
    }
}
