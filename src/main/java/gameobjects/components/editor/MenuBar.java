package gameobjects.components.editor;

import imgui.ImGui;
import observers.EventSystem;
import observers.events.EEventType;
import observers.events.Event;

public class MenuBar {
    public void imgui() {
        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "Ctrl + S")) {
                EventSystem.notify(null, new Event(EEventType.SAVE_LEVEL));
            }

            if (ImGui.menuItem("Load", "Ctrl + O")) {
                EventSystem.notify(null, new Event(EEventType.LOAD_LEVEL));
            }
            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
