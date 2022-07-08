package sirius.editor.imgui;

import imgui.ImGui;
import imgui.type.ImInt;
import sirius.SiriusTheFox;
import observers.EventSystem;
import observers.events.Events;
import observers.events.Event;
import sirius.editor.imgui.sprite_animation_window.SpriteAnimationWindow;
import sirius.editor.imgui.tool_window.ToolWindow;
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
                        Level level = new Level("level" + lvl.get() + ".json",
                                "assets/levels/level" + lvl.get() + ".json", lvl.get());

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

        // TODO: 08/07/2022 Save system to know which docks were non showed
        if (ImGui.beginMenu("Dock")) {
            ImGuiLayer imGuiLayer = SiriusTheFox.getImGuiLayer();

            GameViewWindow gameViewWindow = imGuiLayer.getGameViewWindow();
            gameViewWindow.show = JImGui.checkBox("Game View Window", gameViewWindow.show);

            SpriteAnimationWindow animationWindow = imGuiLayer.getSpriteAnimationWindow();
            animationWindow.show = JImGui.checkBox("Sprite Animation Window", animationWindow.show);

            TabBar tabBar = imGuiLayer.getTabBar();
            tabBar.show = JImGui.checkBox("Tab Bar", tabBar.show);

            ToolWindow toolWindow = imGuiLayer.getToolWindow();
            toolWindow.show = JImGui.checkBox("Tool Window", toolWindow.show);

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
