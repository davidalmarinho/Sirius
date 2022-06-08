package sirius.editor.imgui.sprite_animation_window;

import imgui.*;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import sirius.editor.imgui.JImGui;
import sirius.encode_tools.Encode;
import sirius.utils.AssetPool;
import sirius.utils.Settings;

import java.io.File;
import java.io.IOException;

public class SpriteAnimationWindow {
    private static SpriteAnimationWindow instance;

    private ConfigChild configChild;
    private Animator animator;

    private final String NEW_ANIMATION_CONTEXT = "New Animation";
    private final String LOAD_CONTEXT = "Load Animation";
    private String bufferFileName = "";
    private String currentAnimationPath = "";
    private int currentItem = -1;

    private SpriteAnimationWindow() {
        this.configChild = new ConfigChild();
    }

    public void imgui(float dt) {
        if (ImGui.begin("Sprite Animation Window", new ImBoolean(true))) {
            if (ImGui.button("Create")) {
                ImGui.openPopup(NEW_ANIMATION_CONTEXT);
            }

            ImGui.sameLine();

            if (ImGui.button("Load")) {
                ImGui.openPopup(LOAD_CONTEXT);
            }

            if (ImGui.button("Save")) {
                Encode.saveAnimation(animator.animationBlueprint, currentAnimationPath);
                AssetPool.updateAnimation(currentAnimationPath, animator.animationBlueprint);
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

            if (ImGui.beginPopupModal(LOAD_CONTEXT, ImGuiWindowFlags.AlwaysUseWindowPadding | ImGuiWindowFlags.AlwaysAutoResize)) {
                String[] items = AssetPool.getAnimationsPaths();

                // Close instantly the load menu popup if there aren't any animations files
                if (items.length == 0)
                    ImGui.closeCurrentPopup();

                currentItem = JImGui.list("", currentItem, items);

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

            if (animator == null) {
                ImGui.end();
                return;
            }

            ImGui.text("Mouse Left: drag to add lines, or drag inside the boxes to move them." +
                    "\nMouse Middle: drag to scroll," +
                    "\nMouse Right: click for context menu.");
            ImGui.checkbox("Show config: ", configChild.showConfigChild);
            animator.showAnimator = JImGui.checkBox("Show animator: ",
                    animator.showAnimator);

            // Put config child in left side of the sprite animation window and if the animator is active,
            // config child's size has to be 240
            if (configChild.isShowConfigChild()) {
                if (animator.showAnimator)
                    configChild.imgui(new ImVec2(300, ImGui.getContentRegionAvailY()), dt);
                else
                    configChild.imgui(ImGui.getContentRegionAvail(), dt);
            }

            if (animator.showAnimator)
                animator.imgui(ImGui.getContentRegionAvail(), dt);

        }
        ImGui.end();
    }

    public static SpriteAnimationWindow get() {
        if (instance == null) instance = new SpriteAnimationWindow();

        return instance;
    }

    public static Animator getAnimator() {
        return get().animator;
    }
}
