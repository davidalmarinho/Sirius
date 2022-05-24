package sirius.editor.imgui.sprite_animation_window;

import gameobjects.GameObject;
import gameobjects.components.SpriteRenderer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import sirius.animations.Frame;
import sirius.editor.imgui.JImGui;
import sirius.utils.AssetPool;

import javax.swing.*;

public class ConfigChild {
    String popupAddFrameContext = "add_frame_popup";
    String popupFrameSettings = "frame_settings_popup";
    public ImBoolean showConfigChild;
    private String currentSpritesheet = "";
    private boolean closePopup = false;

    private int currentFrameIndex = 0;

    public ConfigChild() {
        this.showConfigChild = new ImBoolean(false);
    }

    public void imgui(ImVec2 regionAvailable) {
        // Initialize currentSpritesheet with the first spritesheet from the list
        if (currentSpritesheet.equals(""))
            currentSpritesheet = AssetPool.getSpritesheetsPaths()[0];

        ImGui.beginChild("config", regionAvailable.x, regionAvailable.y, true,
                ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.HorizontalScrollbar);

        // Get the animation box that is selected
        AnimationBox activeBox = SpriteAnimationWindow.getStateMachineChild().getActiveBox();

        // Don't have active box to show its configs
        if (activeBox == null) {
            ImGui.endChild();
            ImGui.sameLine();
            return;
        }

        ImGui.text("Trigger: ");
        ImGui.sameLine();
        ImGui.text(activeBox.getTrigger());
        activeBox.doesLoop = JImGui.checkBox("Loop:", activeBox.doesLoop);

        // Gets item's spacing
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        // Get the window x position
        float windowPosX = ImGui.getWindowPosX();

        // Represent graphically added frames
        for (int i = 0; i < activeBox.getFrameListSize(); i++) {
            if (JImGui.imgButton(activeBox.getFrame(i).sprite, i)) {
                // TODO: 23/05/2022 Popup with frame's settings
                currentFrameIndex = i;
                ImGui.openPopup(popupFrameSettings);
            }

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);

            float lastButtonX = lastButtonPos.x;
            float nextButtonX = lastButtonX + itemSpacing.x + activeBox.getFrame(i).sprite.getWidth() * 2;

            // Keep in the same line if we still have items and if the current item isn't bigger than the window itself
            if (i + 1 < activeBox.getFrameListSize() && nextButtonX < windowPosX + regionAvailable.x)
                ImGui.sameLine();
        }

        // Can't un-mark 'doesLoop' checkbox if frame list size > 1
        if (!activeBox.doesLoop && activeBox.getFrameListSize() > 1) {
            JOptionPane.showMessageDialog(new JFrame("Error message"),
                    "Couldn't un-mark this checkbox." +
                            "\nEnsure that you have just one frame so you acn un-mark the checkbox.");
            activeBox.doesLoop = true;
        }

        if (activeBox.doesLoop || activeBox.getFrameListSize() == 0) {
            if (ImGui.button("Add Frame")) {
                ImGui.openPopup(popupAddFrameContext);
            }
        }

        // Menu to select which spritesheet we want
        if (ImGui.beginPopup(popupAddFrameContext, ImGuiWindowFlags.MenuBar)) {
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
                // spriteLayout() is getting the size of the popup window and, we want the size of config's child window.
                if (JImGui.spritesLayout(AssetPool.getSpritesheet(currentSpritesheet), regionAvailable))
                    selectedGo = JImGui.getSelectedGameObject();

                if (selectedGo != null) {
                    // TODO: 23/05/2022 custom time to the frame
                    Frame frame = new Frame(selectedGo.getComponent(SpriteRenderer.class).getSprite(), 0.25f);
                    activeBox.addFrame(frame);
                    closePopup = true;
                }
                ImGui.endMenu();
            }

            if (closePopup) {
                ImGui.closeCurrentPopup();
                closePopup = false;
            }

            ImGui.endPopup();
        }

        if (ImGui.beginPopupModal("Frame Settings")) {
            Frame curFrame = activeBox.getFrame(currentFrameIndex);
            curFrame.frameTime = JImGui.dragFloat("Frame Time: ", curFrame.frameTime);

            if (ImGui.button("Remove Frame")) {
                activeBox.removeFrame(curFrame);
                ImGui.closeCurrentPopup();
            }

            // Close modal popup
            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }

        // Add frames / sprites (if it doesn't loop just let put one frame)


        // TODO: 15/05/2022 Code needed for:
        // Just let edit sprite animation window's children if a game object has been selected
        // SOLVED -> Get the trigger based on the last selected animation box, so the user knows which box is selected
        // SOLVED -> It is convenient to draw a border around the selected box
        // SOLVED -> If it has to loop
        // SOLVED -> Set its active time

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
