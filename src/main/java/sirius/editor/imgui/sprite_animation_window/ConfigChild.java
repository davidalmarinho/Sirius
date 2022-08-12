package sirius.editor.imgui.sprite_animation_window;

import gameobjects.GameObject;
import gameobjects.components.Sprite;
import gameobjects.components.SpriteRenderer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import sirius.animations.Frame;
import sirius.editor.imgui.JImGui;
import sirius.rendering.color.Color;
import sirius.utils.AssetPool;
import sirius.utils.Settings;

import javax.swing.*;

public class ConfigChild {
    private ImVec2 size;
    public ImBoolean showConfigChild;

    private String currentSpritesheet = "";
    private final String POPUP_FRAME_SETTINGS = "frame_settings_popup";
    private final String POPUP_ADD_FRAME_CONTEXT = "add_frame_popup";

    private boolean closePopup = false;

    private boolean changeAllFrames = false;
    private float allFramesTime = Settings.DEFAULT_FRAME_TIME;

    private AnimationBox activeBox;
    private Wire activeWire;
    private Frame activeFrame = null;

    // Just have the objective to show a preview of the animation
    private int curFrameAnimationIndex = 0;
    private float animationTime;

    public ConfigChild() {
        this.showConfigChild = new ImBoolean(false);
        this.size = new ImVec2();
    }

    private void framesMenu() {
        // Gets item's spacing
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        // Get the window x position
        float windowPosX = ImGui.getWindowPosX();

        // Represent graphically added frames
        for (int i = 0; i < activeBox.getFrameListSize(); i++) {
            Sprite sprite = activeBox.getFrame(i).sprite;
            Color btnBackgroundColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);

            if (activeBox.getFrame(i) == activeFrame) {
                btnBackgroundColor = new Color(0.0f, 1.0f, 0.0f, 0.4f);
            }

            if (JImGui.imgButton(i, sprite, btnBackgroundColor)) {
                activeFrame = activeBox.getFrame(i);
                ImGui.openPopup(POPUP_FRAME_SETTINGS);
            }

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);

            float lastButtonX = lastButtonPos.x;
            float nextButtonX = lastButtonX + itemSpacing.x + sprite.getWidth() * 2;

            // Keep in the same line if we still have items and if the current item isn't bigger than the window itself
            if (i + 1 < activeBox.getFrameListSize() && nextButtonX < windowPosX + size.x)
                ImGui.sameLine();
        }
    }

    private void framesSettings() {
        if (ImGui.beginPopupModal(POPUP_FRAME_SETTINGS,
                ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.AlwaysAutoResize)) {
            if (changeAllFrames) {
                allFramesTime = JImGui.dragFloatPopups("Frame Time:", allFramesTime);

                // Close modal popup
                if (ImGui.button("Close and Save")) {
                    activeBox.getFrameList().forEach(frame -> frame.frameTime = allFramesTime);
                    allFramesTime = Settings.DEFAULT_FRAME_TIME;
                    changeAllFrames = false;
                    ImGui.closeCurrentPopup();
                }
                ImGui.sameLine();
                if (ImGui.button("Just Close")) {
                    allFramesTime = Settings.DEFAULT_FRAME_TIME;
                    changeAllFrames = false;
                    ImGui.closeCurrentPopup();
                }
            } else {
                activeFrame.frameTime = JImGui.dragFloatPopups("Frame Time: ", activeFrame.frameTime);

                // Reorder frame list
                final int SIZE = activeBox.getFrameListSize();
                if (ImGui.button("Move Left")) {
                    for (int i = 0; i < SIZE; i++) {
                        Frame curFrame = activeBox.getFrame(i);
                        if (curFrame == activeFrame) {
                            if (i - 1 >= 0) {
                                // Swaps current value with the previous value in the list
                                activeBox.setFrame(i, activeBox.getFrame(i - 1));
                                activeBox.setFrame(i - 1, activeFrame);
                                break;
                            }
                        }
                    }
                }

                ImGui.sameLine();

                if (ImGui.button("Move Right")) {
                    for (int i = 0; i < SIZE; i++) {
                        Frame curFrame = activeBox.getFrame(i);
                        if (curFrame == activeFrame) {
                            if (i + 1 < SIZE) {
                                // Swaps current value with the next value in the list
                                activeBox.setFrame(i, activeBox.getFrame(i + 1));
                                activeBox.setFrame(i + 1, activeFrame);
                                break;
                            }
                        }
                    }
                }

                if (ImGui.button("Remove Frame")) {
                    removeFrame();
                }

                // Close modal popup
                ImGui.newLine();
                if (ImGui.button("Close")) {
                    activeFrame = null;
                    ImGui.closeCurrentPopup();
                }
            }

            ImGui.endPopup();
        }
    }

    private void previewAnimation(float dt) {
        if (activeBox.getFrameListSize() > 0) {
            ImGui.newLine();
            animationTime += dt;

            // Bug fix
            if (curFrameAnimationIndex >= activeBox.getFrameListSize() || activeBox.getFrame(curFrameAnimationIndex) == null)
                resetAnimationPreview();

            if (animationTime >= activeBox.getFrame(curFrameAnimationIndex).frameTime) {
                animationTime = 0.0f;
                curFrameAnimationIndex++;
                if (curFrameAnimationIndex >= activeBox.getFrameListSize()) {
                    curFrameAnimationIndex = 0;
                }
            }

            Sprite sprite = activeBox.getFrame(curFrameAnimationIndex).sprite;

            // TODO: 28/05/2022 Make button to change background's color
            JImGui.image(sprite, sprite.getWidth() * 6, sprite.getHeight() * 6);
        } else {
            curFrameAnimationIndex = 0;
        }

        if (activeBox.getFrameListSize() > 1) {
            ImGui.newLine();
            if (ImGui.button("Edit all frames")) {
                ImGui.openPopup(POPUP_FRAME_SETTINGS);
                changeAllFrames = true;
            }
        }
    }

    private void addFrameButton() {
        if (activeBox.doesLoop || activeBox.getFrameListSize() == 0) {
            if (ImGui.button("Add Frame"))
                ImGui.openPopup(POPUP_ADD_FRAME_CONTEXT);
        }

        if (ImGui.beginPopup(POPUP_ADD_FRAME_CONTEXT, ImGuiWindowFlags.MenuBar)) {
            // Menu to select which spritesheet we want
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
                if (JImGui.spritesLayout(AssetPool.getSpritesheet(currentSpritesheet), size))
                    selectedGo = JImGui.getSelectedGameObject();

                if (selectedGo != null) {
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
    }

    private void removeFrame() {
        activeBox.removeFrame(activeFrame);
        activeFrame = null;
        resetAnimationPreview();
        ImGui.closeCurrentPopup();
    }

    private void resetAnimationPreview() {
        curFrameAnimationIndex = 0;
        animationTime = 0;
    }

    public void imgui(ImVec2 regionAvailable, float dt) {
        this.size.set(regionAvailable);

        // Initialize currentSpritesheet with the first spritesheet from the list
        if (currentSpritesheet.equals(""))
            currentSpritesheet = AssetPool.getSpritesheetsPaths()[0];

        ImGui.beginChild("config", regionAvailable.x, regionAvailable.y, true,
                ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.HorizontalScrollbar);

        // Get the animation box that is selected or get the selected wire if it is select.
        // Just one of these may be selected.
        this.activeBox  = SpriteAnimationWindow.getAnimator().getActiveBox();
        this.activeWire = SpriteAnimationWindow.getAnimator().getActiveWire();

        // Don't have active box to show its configs
        if (activeBox == null && activeWire == null) {
            resetAnimationPreview();
            ImGui.endChild();
            ImGui.sameLine();
            return;
        }

        if (activeBox != null) {
            ImGui.text("Title: ");
            ImGui.sameLine();
            ImGui.text(activeBox.getTitle());
            activeBox.doesLoop = JImGui.checkBox("Loop:", activeBox.doesLoop);

            framesMenu();

            // Can't un-mark 'doesLoop' checkbox if frame list size > 1
            if (!activeBox.doesLoop && activeBox.getFrameListSize() > 1) {
                JOptionPane.showMessageDialog(new JFrame("Error message"),
                        "Couldn't un-mark this checkbox." +
                                "\nEnsure that you have just one frame so you acn un-mark the checkbox.");
                activeBox.doesLoop = true;
            }

            addFrameButton();

            framesSettings();

            // Show a preview of the animation
            previewAnimation(dt);
        } else {
            // TODO: 17/06/2022 Make system to create the trigger and reuse them
            ImGui.text("Trigger: ");
            ImGui.sameLine();
            activeWire.setTrigger(inputText(activeWire.getTrigger()));
        }

        // Terminate child
        ImGui.endChild();
        ImGui.sameLine();
    }

    private String inputText(String text) {
        ImString outString = new ImString(text, 32);

        if (ImGui.inputText("Trigger: ", outString, ImGuiInputTextFlags.AutoSelectAll)) {
            return outString.get();
        }

        return text;
    }

    public boolean isShowConfigChild() {
        return showConfigChild.get();
    }
}
