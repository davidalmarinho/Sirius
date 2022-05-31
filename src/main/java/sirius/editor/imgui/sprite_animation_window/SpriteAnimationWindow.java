package sirius.editor.imgui.sprite_animation_window;

import gameobjects.GameObject;
import imgui.*;
import imgui.type.ImBoolean;
import sirius.SiriusTheFox;
import sirius.animations.AnimationState;
import sirius.animations.Frame;
import sirius.animations.StateMachine;
import sirius.editor.PropertiesWindow;
import sirius.editor.imgui.JImGui;
import sirius.encode_tools.Encode;
import sirius.utils.JMath;
import sirius.utils.Settings;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SpriteAnimationWindow {
    private static SpriteAnimationWindow instance;

    private ConfigChild configChild;
    private StateMachineChild stateMachineChild;

    private String lastActiveGameObjectName = "";
    private boolean selectedGameObject = false;

    private SpriteAnimationWindow() {
        this.configChild = new ConfigChild();
        this.stateMachineChild = new StateMachineChild();
    }

    public void imgui(float dt) {
        PropertiesWindow propertiesWindow = SiriusTheFox.getImGuiLayer().getPropertiesWindow();

        // Save animation boxes in file
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        if (propertiesWindow.getActiveGameObjectList().size() != 1
                || (activeGameObject != null && !lastActiveGameObjectName.equals(activeGameObject.name))) {
            if (!lastActiveGameObjectName.equals("")) {
                Encode.saveAnimation(stateMachineChild, Settings.Files.ANIMATIONS_FOLDER
                        + lastActiveGameObjectName + ".json");
                stateMachineChild = new StateMachineChild();
                lastActiveGameObjectName = "";
                selectedGameObject = false;
                System.out.println("Saved!!!");
            }
        }

        if (ImGui.begin("Sprite Animation Window", new ImBoolean(true))) {
            if (propertiesWindow.getActiveGameObjectList().size() != 1) {
                ImGui.text("Select a game object to start animating him!");
                ImGui.end();
                return;
            }

            // Create animation file to store the animation boxes
            if (!selectedGameObject) {
                try {
                    File file = new File(Settings.Files.ANIMATIONS_FOLDER
                            + propertiesWindow.getActiveGameObject().name + ".json");

                    // Check if the file was already created
                    if (!file.createNewFile()) {
                        System.out.println(Settings.Files.ANIMATIONS_FOLDER
                                + propertiesWindow.getActiveGameObject().name + ".json");
                        StateMachineChild animations = Encode.getAnimation(Settings.Files.ANIMATIONS_FOLDER
                                + propertiesWindow.getActiveGameObject().name + ".json");
                        stateMachineChild = new StateMachineChild(animations.pointList, animations.getAnimationBoxList());
                    }

                    lastActiveGameObjectName = propertiesWindow.getActiveGameObject().name;
                } catch (IOException e) {
                    e.printStackTrace();
                }



                // AssetPool.addAnimationPath(propertiesWindow.getActiveGameObject().name);
                selectedGameObject = true;
            }

            ImGui.text("Mouse Left: drag to add lines, or drag inside the boxes to move them." +
                    "\nMouse Middle: drag to scroll," +
                    "\nMouse Right: click for context menu.");
            ImGui.checkbox("Show config: ", configChild.showConfigChild);
            stateMachineChild.showStateMachineChild = JImGui.checkBox("Show state machine: ", stateMachineChild.showStateMachineChild);

            // Put config child in left side of the sprite animation window and if the state machine child is active,
            // config child's size has to be 240
            if (configChild.isShowConfigChild()) {
                if (stateMachineChild.showStateMachineChild)
                    configChild.imgui(new ImVec2(300, ImGui.getContentRegionAvailY()), dt);
                else
                    configChild.imgui(ImGui.getContentRegionAvail(), dt);
            }

            if (stateMachineChild.showStateMachineChild)
                stateMachineChild.imgui(ImGui.getContentRegionAvail(), dt);

        }
        ImGui.end();
    }

    public static SpriteAnimationWindow get() {
        if (instance == null) instance = new SpriteAnimationWindow();

        return instance;
    }

    public static StateMachineChild getStateMachineChild() {
        return get().stateMachineChild;
    }
}
