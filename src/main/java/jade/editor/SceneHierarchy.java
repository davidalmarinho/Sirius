package jade.editor;

import gameobjects.GameObject;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import jade.Window;

import java.util.List;

public class SceneHierarchy {

    public void imgui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getCurrentScene().getGameObjectList();
        int index = 0;
        for (GameObject go : gameObjects) {
            if (!go.isDoSerialization()) {
                continue;
            }

            ImGui.pushID(index);
            boolean treeNodeOpen = ImGui.treeNodeEx(
                    go.NAME,
                    ImGuiTreeNodeFlags.DefaultOpen
                            | ImGuiTreeNodeFlags.FramePadding
                            | ImGuiTreeNodeFlags.OpenOnArrow
                            | ImGuiTreeNodeFlags.SpanAvailWidth,
                    go.NAME);
            ImGui.popID();

            if (treeNodeOpen) ImGui.treePop();
        }

        ImGui.end();
    }
}
