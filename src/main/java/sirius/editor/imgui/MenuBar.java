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
import sirius.rendering.color.ColorBlindnessCategories;
import sirius.utils.AssetPool;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MenuBar {
    private final ColorBlindnessCategories[] COLOR_BLINDNESSES;
    private int currentBlindness = 0;
    private transient ImInt lvl = new ImInt(1);

    public MenuBar() {
        COLOR_BLINDNESSES = new ColorBlindnessCategories[9];
        COLOR_BLINDNESSES[0] = ColorBlindnessCategories.NO_COLOR_BLINDNESS;
        COLOR_BLINDNESSES[1] = ColorBlindnessCategories.PROTANOPIA;
        COLOR_BLINDNESSES[2] = ColorBlindnessCategories.PROTANOMALY;
        COLOR_BLINDNESSES[3] = ColorBlindnessCategories.DEUTERANOPIA;
        COLOR_BLINDNESSES[4] = ColorBlindnessCategories.DEUTERANOMALY;
        COLOR_BLINDNESSES[5] = ColorBlindnessCategories.TRITANOPIA;
        COLOR_BLINDNESSES[6] = ColorBlindnessCategories.TRITANOMALY;
        COLOR_BLINDNESSES[7] = ColorBlindnessCategories.ACHROMATOPSIA;
        COLOR_BLINDNESSES[8] = ColorBlindnessCategories.ACHROMATOMALY;
    }

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
            if (SiriusTheFox.get().isRuntimePlaying()) ImGui.beginDisabled();
            SiriusTheFox.get().setMaximizeOnPlay(JImGui.checkBox("Maximize on play", SiriusTheFox.get().isMaximizeOnPlay()));
            if (SiriusTheFox.get().isRuntimePlaying()) ImGui.endDisabled();

            SiriusTheFox.getWindow().setVsync(JImGui.checkBox("V-sync", SiriusTheFox.getWindow().isVsync()));

            if (SiriusTheFox.getWindow().isVsync()) ImGui.beginDisabled();
            SiriusTheFox.getWindow().maxFps = JImGui.defaultInputInt(1, "Max fps:", SiriusTheFox.getWindow().maxFps);
            if (SiriusTheFox.getWindow().isVsync()) ImGui.endDisabled();

            currentBlindness = JImGui.combo("Color Blindness", COLOR_BLINDNESSES, currentBlindness);

            ImGui.endMenu();
        }

        if (ImGui.beginMenu("Dock")) {
            ImGuiLayer imGuiLayer = SiriusTheFox.getImGuiLayer();

            GameViewWindow gameViewWindow = imGuiLayer.getGameViewWindow();
            gameViewWindow.setVisibility(JImGui.checkBox("Game View Window", gameViewWindow.isVisible()));

            SpriteAnimationWindow animationWindow = imGuiLayer.getSpriteAnimationWindow();
            animationWindow.setVisibility(JImGui.checkBox("Sprite Animation Window", animationWindow.isVisible()));

            TabBar tabBar = imGuiLayer.getTabBar();
            tabBar.setVisibility(JImGui.checkBox("Tab Bar", tabBar.isVisible()));

            ToolWindow toolWindow = imGuiLayer.getToolWindow();
            toolWindow.setVisibility(JImGui.checkBox("Tool Window", toolWindow.isVisible()));

            SceneHierarchy sceneHierarchy = imGuiLayer.getSceneHierarchy();
            sceneHierarchy.setVisibility(JImGui.checkBox("Scene Hierarchy Window", sceneHierarchy.isVisible()));

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
