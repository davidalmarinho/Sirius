package sirius.editor;

import imgui.ImGui;
import imgui.type.ImInt;
import imgui.type.ImString;
import sirius.ImGuiLayer;
import sirius.SiriusTheFox;
import observers.EventSystem;
import observers.events.Events;
import observers.events.Event;
import sirius.levels.Level;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MenuBar {
    private transient ImString lvlName = new ImString(256);
    private transient ImInt lvl = new ImInt(1);

    public void imgui() {
        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.beginMenu("New")) {
                ImGui.textUnformatted("Level Name: ");
                ImGui.sameLine();
                ImGui.inputText("##inputText", lvlName);

                ImGui.textUnformatted("Level Number: ");
                ImGui.sameLine();
                ImGui.inputInt("##inputInt", lvl, 1);

                if (ImGui.button("Create")) {
                    try {
                        Level level = new Level(lvlName.get(), lvl.get());
                        new FileWriter("assets/levels/" + level.getCustomLevelName() + ".txt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ImGui.endMenu();
            }

            if (ImGui.menuItem("Save", "Ctrl + S")) {
                if (!SiriusTheFox.get().isRuntimePlaying())
                    EventSystem.notify(null, new Event(Events.SAVE_LEVEL));

            }

            if (ImGui.beginMenu("Load")) {
                if (ImGui.menuItem("Level1", ""))
                    EventSystem.notify(null, new Event(Events.LOAD_LEVEL));
                ImGui.menuItem("Level2", "");
                ImGui.menuItem("Level3", "");
                // AssetPool.getLevels();
                // EventSystem.notify(null, new Event(Events.LOAD_LEVEL));

                ImGui.endMenu();
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Settings")) {
            if (ImGui.menuItem("Export Game Preview", ""))
                EventSystem.notify(null, new Event(Events.EXPORT_GAME));

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
