package sirius.editor.imgui;

import gameobjects.GameObject;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import sirius.SiriusTheFox;

import java.util.List;

public class SceneHierarchy extends GuiWindow {
    private static String payloadDragDropType = "SceneHierarchy";

    public SceneHierarchy() {
        super(false);
    }

    public void imgui() {
        if (!isVisible()) return;

        ImGui.begin("Scene Hierarchy", show);

        List<GameObject> gameObjects = SiriusTheFox.getCurrentScene().getGameObjectList();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);

            if (!go.isDoSerialization()) continue;

            boolean treeNodeOpen = doTreeNode(go, i);

            if (treeNodeOpen) ImGui.treePop();
        }

        ImGui.end();
    }

    private boolean doTreeNode(GameObject go, int index) {
        ImGui.pushID(index);
        boolean result = ImGui.treeNodeEx(
                go.name,
                ImGuiTreeNodeFlags.DefaultOpen
                        | ImGuiTreeNodeFlags.FramePadding
                        | ImGuiTreeNodeFlags.OpenOnArrow
                        | ImGuiTreeNodeFlags.SpanAvailWidth,
                go.name);
        ImGui.popID();

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(payloadDragDropType, go);

            ImGui.text(go.name);

            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            Object payloadObj = ImGui.acceptDragDropPayload(payloadDragDropType);

            // Means that we are dragging
            if (payloadObj != null) {
                if (payloadObj.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject playerGameObj = (GameObject) payloadObj;
                    System.out.println("Payload accepted '" + playerGameObj.name + "'");
                }
            }

            ImGui.endDragDropTarget();
        }

        return result;
    }
}
