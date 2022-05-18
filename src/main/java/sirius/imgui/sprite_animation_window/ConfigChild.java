package sirius.imgui.sprite_animation_window;

import gameobjects.GameObject;
import gameobjects.components.SpriteRenderer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import sirius.animations.Frame;
import sirius.editor.JImGui;
import sirius.utils.AssetPool;

import java.util.ArrayList;
import java.util.List;

public class ConfigChild {
    public ImBoolean showConfigChild;
    private ImBoolean doesLoop;
    private String currentSpritesheet = "";
    private List<Frame> frameList;

    public ConfigChild() {
        this.showConfigChild = new ImBoolean(false);
        this.doesLoop = new ImBoolean(false);
        this.frameList = new ArrayList<>();
    }

    public void imgui(ImVec2 regionAvailable) {
        // Initialize currentSpritesheet with the first spritesheet from the list
        if (currentSpritesheet.equals(""))
            currentSpritesheet = AssetPool.getSpritesheetsPaths()[0];

        ImGui.beginChild("config", regionAvailable.x, regionAvailable.y, true,
                ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.HorizontalScrollbar);

        // Get the animation box that is selected
        AnimationBox activeBox = SpriteAnimationWindow.getStateMachineChild().getActiveBox();

        if (activeBox == null) {
            ImGui.endChild();
            ImGui.sameLine();
            return;
        }

        ImGui.text("Trigger: ");
        ImGui.sameLine();
        ImGui.text(activeBox.getTrigger());
        ImGui.checkbox("Loop: ", doesLoop);

        if (ImGui.button("Add Frame")) {
            ImGui.openPopup("popup_menu");
        }

        // Menu to select which spritesheet we want
        if (ImGui.beginPopup("popup_menu", ImGuiWindowFlags.MenuBar)) {
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("Select spritesheet")) {
                    String[] loadSprites = AssetPool.getSpritesheetsNames();
                    for (int i = 0; i < loadSprites.length; i++) {
                        if (ImGui.menuItem(loadSprites[i])) {
                            currentSpritesheet = AssetPool.getSpritesheetsPaths()[i];
                        }
                    }

                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }

            // List of buttons to select what sprite we want to put in frame list
            if (ImGui.beginMenu("Sprites")) {
                GameObject selectedGo = null;
                if (JImGui.spritesLayout(AssetPool.getSpritesheet(currentSpritesheet)))
                    selectedGo = JImGui.getSelectedGameObject();

                if (selectedGo != null) {
                    Frame frame = new Frame(selectedGo.getComponent(SpriteRenderer.class).getSprite(), 0.25f);
                    frameList.add(frame);
                }
                ImGui.endMenu();
            }
            ImGui.endPopup();
        }

        for (Frame frame : frameList) {
            System.out.println(frame.sprite.toString());
        }

        System.out.println(currentSpritesheet);


        // Add frames / sprites (if it doesn't loop just let put one frame)


        // TODO: 15/05/2022 Code needed for:
        // Just let edit sprite animation window's children if a game object has been selected
        // SOLVED -> Get the trigger based on the last selected animation box, so the user knows which box is selected
        // SOLVED -> It is convenient to draw a border around the selected box

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
