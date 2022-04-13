package jade.editor;

import imgui.ImGui;
import jade.SiriusTheFox;
import observers.EventSystem;
import observers.events.Events;
import observers.events.Event;

public class MenuBar {
    public void imgui() {
        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "Ctrl + S"))
                if (!SiriusTheFox.get().isRuntimePlaying())
                    EventSystem.notify(null, new Event(Events.SAVE_LEVEL));

            if (ImGui.menuItem("Load", "Ctrl + O"))
                EventSystem.notify(null, new Event(Events.LOAD_LEVEL));

            ImGui.endMenu();
        } else if (ImGui.beginMenu("Settings")) {
            if (ImGui.menuItem("Export Game", ""))
                EventSystem.notify(null, new Event(Events.EXPORT_GAME));

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
