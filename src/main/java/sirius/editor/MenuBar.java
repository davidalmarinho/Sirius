package sirius.editor;

import imgui.ImGui;
import imgui.type.ImInt;
import sirius.SiriusTheFox;
import observers.EventSystem;
import observers.events.Events;
import observers.events.Event;
import sirius.input.KeyListener;
import sirius.levels.Level;
import sirius.utils.AssetPool;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MenuBar {
    private transient ImInt lvl = new ImInt(1);

    public void imgui() {
        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.beginMenu("New")) {
                ImGui.textUnformatted("Level Number: ");
                ImGui.sameLine();
                ImGui.setNextItemWidth(140f);
                ImGui.inputInt("##inputInt", lvl, 1);

                if (ImGui.button("Create")) {
                    try {
                        Level level = new Level("level" + lvl.get() + ".txt",
                                "assets/levels/level" + lvl.get() + ".txt", lvl.get());

                        // Checks if we don't create an existing level
                        if (AssetPool.getLevel(level.getId()) != null)
                            System.err.println("Error: Level " + level.getId() + " already exists.");
                        else {
                            new FileWriter(level.getPath());
                            AssetPool.addLevel(level);
                        }

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
                List<Level> levels = AssetPool.getLevelList();
                for (int i = 0; i < levels.size(); i++) {
                    Level level = levels.get(i);
                    if (ImGui.menuItem(level.getName())) {
                        Level.currentLevel = level.getId();
                        EventSystem.notify(null, new Event(Events.LOAD_LEVEL));
                    }
                }

                ImGui.endMenu();
            }

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Settings")) {
            if (ImGui.menuItem("Export Game Preview", "Ctrl + E"))
                EventSystem.notify(null, new Event(Events.EXPORT_GAME));

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
