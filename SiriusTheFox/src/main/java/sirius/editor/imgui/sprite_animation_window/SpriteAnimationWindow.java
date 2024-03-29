package sirius.editor.imgui.sprite_animation_window;

import imgui.*;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import sirius.editor.imgui.GuiWindow;
import sirius.editor.imgui.JImGui;
import sirius.encode_tools.Encode;
import sirius.utils.Pool;
import sirius.utils.Settings;

import java.io.File;
import java.io.IOException;

public class SpriteAnimationWindow extends GuiWindow {
    private static SpriteAnimationWindow instance;

    private ConfigChild configChild;
    private Animator animator;

    private final String NEW_ANIMATION_CONTEXT = "New Animation";
    private final String LOAD_CONTEXT = "Load Animation";
    private final String DELETE_CONTEXT = "Delete Animation";
    private String bufferFileName = "";
    private String currentAnimationPath = "";
    private int currentItem = -1;

    private SpriteAnimationWindow() {
        super();
        this.configChild = new ConfigChild();
    }

    public void imgui(float dt) {
        if (!isVisible())
            return;

        if (ImGui.begin("Sprite Animation Window", show)) {
            ImGui.pushID(223);

            ImGui.columns(2);

            final float CONFIG_CHILD_WIDTH = 300;

            ImGui.setColumnWidth(0, CONFIG_CHILD_WIDTH);

            configChild.imgui(new ImVec2(CONFIG_CHILD_WIDTH, CONFIG_CHILD_WIDTH), dt);

            createButton();

            deleteButton();

            saveButton();

            loadButton();

            if (ImGui.getWindowWidth() - CONFIG_CHILD_WIDTH > 0) {
                ImGui.nextColumn();
                ImGui.setColumnWidth(1, ImGui.getWindowWidth() - CONFIG_CHILD_WIDTH);
            }

            if (animator != null) {
                // ImGui.sameLine();
                animator.imgui(ImGui.getContentRegionAvail());
            }

            ImGui.columns(1);
            ImGui.popID();


            if (animator == null) {
                ImGui.end();
                return;
            }

            // ImGui.text("Mouse Left: drag to add lines, or drag inside the boxes to move them." +
            //         "\nMouse Middle: drag to scroll," +
            //         "\nMouse Right: click for context menu.");
            // configChild.showConfigChild = JImGui.checkBox("Show config", configChild.showConfigChild);
            // animator.showAnimator = JImGui.checkBox("Show animator", animator.showAnimator);

            // Put config child in left side of the sprite animation window and if the animator is active,
            // config child's size has to be 240
            //if (configChild.isShowConfigChild()) {
                //if (animator.showAnimator)
                    //configChild.imgui(new ImVec2(ImGui.getContentRegionAvailX(), 300), dt);
                //else
                    //configChild.imgui(ImGui.getContentRegionAvail(), dt);
            //}

            //if (animator.showAnimator)
                //animator.imgui(ImGui.getContentRegionAvail());

        }
        ImGui.end();
    }

    private void createButton() {
        if (ImGui.button("Create")) {
            ImGui.openPopup(NEW_ANIMATION_CONTEXT);
        }

        if (ImGui.beginPopupModal(NEW_ANIMATION_CONTEXT, ImGuiWindowFlags.AlwaysUseWindowPadding)) {
            bufferFileName = JImGui.inputText("File Name: ", bufferFileName, 600, 32);

            if (ImGui.button("New Animation")) {
                if (bufferFileName.isEmpty()) {
                    System.err.println("Error: File might have a name.");
                } else {
                    currentAnimationPath = Settings.Files.ANIMATIONS_FOLDER + bufferFileName + ".json";
                    bufferFileName = "";
                    File file = new File(currentAnimationPath);
                    try {
                        if (file.createNewFile()) {
                            this.animator = new Animator();
                        } else {
                            System.err.println("Error: Couldn't create '" + bufferFileName + "' animation.");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    ImGui.closeCurrentPopup();
                }
            }

            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }
    }

    private void deleteButton() {
        if (ImGui.button("Delete")) {
            ImGui.openPopup(DELETE_CONTEXT);
        }

        if (ImGui.beginPopupModal(DELETE_CONTEXT, ImGuiWindowFlags.AlwaysUseWindowPadding
                | ImGuiWindowFlags.AlwaysAutoResize)) {
            String[] items = Pool.Assets.getAnimationsPaths();

            // Close instantly the load menu popup if there aren't any animations files
            if (items.length == 0)
                ImGui.closeCurrentPopup();

            currentItem = JImGui.listLeaf("", currentItem, items);

            if (ImGui.isMouseReleased(ImGuiMouseButton.Left) && currentItem >= 0) {
                // TODO: 17/06/2022 Are you sure? Window
                // TODO: 13/11/22 Error: Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 2 out of bounds for length 2
                currentAnimationPath = items[currentItem];
                new File(currentAnimationPath).delete();
                Pool.Assets.removeAnimation(currentAnimationPath);
                ImGui.closeCurrentPopup();
            }

            ImGui.newLine();
            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }
    }

    private void saveButton() {
        if (ImGui.button("Save") && animator.animationBlueprint != null) {
            // TODO: 17/06/2022 Throw error when a trigger is just "" (empty)
            Encode.saveAnimation(animator.animationBlueprint, currentAnimationPath);
            Pool.Assets.updateAnimation(currentAnimationPath, animator.animationBlueprint);
        }
    }


    private void loadButton() {
        if (ImGui.button("Load")) {
            ImGui.openPopup(LOAD_CONTEXT);
        }

        if (ImGui.beginPopupModal(LOAD_CONTEXT, ImGuiWindowFlags.AlwaysUseWindowPadding
                | ImGuiWindowFlags.AlwaysAutoResize)) {
            String[] items = Pool.Assets.getAnimationsPaths();

            // Close instantly the load menu popup if there aren't any animations files
            if (items.length == 0)
                ImGui.closeCurrentPopup();

            currentItem = JImGui.listLeaf("", currentItem, items);

            if (ImGui.isMouseReleased(ImGuiMouseButton.Left) && currentItem >= 0) {
                currentAnimationPath = items[currentItem];
                this.animator = new Animator(Encode.getAnimation(items[currentItem]));

                // Reset current item
                currentItem = -1;

                ImGui.closeCurrentPopup();
            }

            ImGui.newLine();

            if (ImGui.button("Close")) {
                ImGui.closeCurrentPopup();
            }

            ImGui.endPopup();
        }
    }

    public static SpriteAnimationWindow get() {
        if (instance == null) instance = new SpriteAnimationWindow();

        return instance;
    }

    public static Animator getAnimator() {
        return get().animator;
    }
}
