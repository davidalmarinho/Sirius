package sirius.imgui;

import gameobjects.GameObject;
import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.ImNodesContext;
import imgui.extension.imnodes.flag.ImNodesPinShape;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import sirius.SiriusTheFox;
import sirius.input.KeyListener;
import sirius.input.MouseListener;

import java.awt.*;
import java.net.URI;

import static org.lwjgl.glfw.GLFW.*;

public class SpriteAnimationWindow {

    private static final ImNodesContext CONTEXT = new ImNodesContext();
    private static final String URL = "https://github.com/Nelarius/imnodes/tree/857cc86";

    private static final ImInt LINK_A = new ImInt();
    private static final ImInt LINK_B = new ImInt();

    private Graph graph;

    private boolean collapsed;

    public SpriteAnimationWindow() {
        ImNodes.createContext();
        this.graph = new Graph();
    }

    private void addTrigger(Graph.GraphNode node, int index) {
        ImNodes.beginInputAttribute(node.getInputPinIds()[index], ImNodesPinShape.Quad);
        ImNodes.endInputAttribute();

        ImGui.sameLine();
        ImGui.pushID("nodeTrigger: " + node.id);
        float val = 11.3f;
        int charsNumber = node.trigger.get().length();
        float currentSize = (charsNumber + 1) * val; // +1 to maintain the integrity of this logic
        float maxSize = 20.8f * val;
        if (charsNumber == 0) {
            ImGui.setNextItemWidth(val * 2);
        } else
            ImGui.setNextItemWidth(Math.min(currentSize, maxSize));

        ImGui.inputText("", node.trigger);
        ImGui.popID();
        ImGui.sameLine();

        ImNodes.beginOutputAttribute(node.getOutputPinIds()[index]);
        ImGui.text("");
        ImNodes.endOutputAttribute();
    }

    public void imgui() {
        if (ImGui.begin("ImNodes Demo", new ImBoolean(true))) {
            this.collapsed = false;

            ImGui.text("This a demo graph editor for ImNodes");

            ImGui.alignTextToFramePadding();
            ImGui.text("Repo:");
            ImGui.sameLine();
            if (ImGui.button(URL)) {
                try {
                    Desktop.getDesktop().browse(new URI(URL));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            ImNodes.editorContextSet(CONTEXT);
            ImNodes.beginNodeEditor();

            GameObject activeGameObject = SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObject();
            if (activeGameObject != null) {
                for (Graph.GraphNode node : graph.nodes.values()) {
                    ImNodes.beginNode(node.id);

                    ImNodes.beginNodeTitleBar();
                    ImGui.textUnformatted(activeGameObject.name);

                    // TODO: 23/04/2022 Save nodes in files to after load animations to all game objects that have the same name  
                    
                    /*ImGui.pushID("nodeName: " + node.id);
                    float val = 11.5f;
                    int charsNumber = node.name.get().length();
                    float currentSize = (charsNumber + 1) * val; // +1 to maintain the integrity of this logic
                    float maxSize = 16.8f * val;
                    if (charsNumber == 0) {
                        ImGui.setNextItemWidth(val * 2);
                    } else
                        ImGui.setNextItemWidth(Math.min(currentSize, maxSize));
                    ImGui.inputText("", node.name);
                    ImGui.popID();*/

                    ImNodes.endNodeTitleBar();

                    addTrigger(node, 0);
                    /*if (ImGui.button("Add trigger", 130f, 30f)) {
                        for (int index = 1; index < node.inputPinIds.length; index++)
                            addTrigger(node, index);
                    }*/

                    ImNodes.endNode();
                }
            }

            int uniqueLinkId = 1;
            // for (Graph.GraphNode node : graph.nodes.values()) {
                /*if (graph.nodes.containsKey(node.outputNodeId)) {
                    for (int i = 0; i < node.getInputPinIds().length; i++) {
                        for (int j = 0; j < node.getOutputPinIds().length; j++) {
                            for (int k = 0; k < Graph.GraphNode.currentInput.size(); k++) {
                                if (k == Graph.GraphNode.currentOutput.size())
                                    break;
                                if (graph.nodes.get(node.outputNodeId).getInputPinIds()[i] == Graph.GraphNode.currentInput.get(k)
                                        && node.getOutputPinIds()[j] == Graph.GraphNode.currentOutput.get(k))
                                    ImNodes.link(uniqueLinkId++, node.getOutputPinIds()[j], graph.nodes.get(node.outputNodeId).getInputPinIds()[i]);
                            }
                        }
                    }
                }*/
            // }
                int highestSize = Math.max(Graph.GraphNode.currentInput.size(), Graph.GraphNode.currentOutput.size());
                boolean inputGreatest = Graph.GraphNode.currentInput.size() > Graph.GraphNode.currentOutput.size();
                for (int i = 0; i < highestSize; i++) {
                    if (Graph.GraphNode.currentInput.size() != Graph.GraphNode.currentOutput.size()) {
                        if (inputGreatest) {
                            if (i == Graph.GraphNode.currentInput.size() - 1)
                                break;
                        } else {
                            if (i == Graph.GraphNode.currentOutput.size() - 1)
                                break;
                        }
                    }

                    ImNodes.link(uniqueLinkId++, Graph.GraphNode.currentOutput.get(i), Graph.GraphNode.currentInput.get(i));
                }

            final boolean isEditorHovered = ImNodes.isEditorHovered();
            ImNodes.endNodeEditor();

            boolean mouseClicked = ImGui.isMouseDown(GLFW_MOUSE_BUTTON_LEFT);
            boolean oneFrameMouse = ImGui.isMouseClicked(GLFW_MOUSE_BUTTON_LEFT);

            for (Graph.GraphNode node : graph.nodes.values()) {
                for (int i = 0; i < node.inputPinIds.length; i++) {
                    if ((ImNodes.getHoveredPin() == node.inputPinIds[i] && mouseClicked && oneFrameMouse)
                            || (ImNodes.getHoveredPin() == node.inputPinIds[i] && mouseClicked
                                && Graph.GraphNode.currentInput.size() < Graph.GraphNode.currentOutput.size())) {
                        Graph.GraphNode.currentInput.add(node.inputPinIds[i]);
                        break;
                    }
                }

                for (int i = 0; i < node.outputPinIds.length; i++) {
                    if (
                            (ImNodes.getHoveredPin() == node.outputPinIds[i] && mouseClicked
                            // && Graph.GraphNode.currentOutput.stream().noneMatch(t -> t == ImNodes.getHoveredPin())
                                    && oneFrameMouse)
                            || (ImNodes.getHoveredPin() == node.outputPinIds[i] && mouseClicked
                                    && Graph.GraphNode.currentOutput.size() < Graph.GraphNode.currentInput.size())
                    ) {
                        Graph.GraphNode.currentOutput.add(node.outputPinIds[i]);
                        break;
                    }
                }
            }

            // System.out.println("In: " + Graph.GraphNode.currentInput);
            // System.out.println("Out: " + Graph.GraphNode.currentOutput);

            // Checks
            if (!ImGui.isMouseDown(GLFW_MOUSE_BUTTON_LEFT)) {
                if (Graph.GraphNode.currentInput.size() > Graph.GraphNode.currentOutput.size())
                    Graph.GraphNode.currentInput.remove(Graph.GraphNode.currentInput.size() - 1);
                else if (Graph.GraphNode.currentOutput.size() > Graph.GraphNode.currentInput.size())
                    Graph.GraphNode.currentOutput.remove(Graph.GraphNode.currentOutput.size() - 1);
            }

            if (Graph.GraphNode.currentOutput.size() == Graph.GraphNode.currentInput.size()
                    && !ImGui.isMouseDown(GLFW_MOUSE_BUTTON_LEFT)) {
                for (int i = Graph.GraphNode.currentInput.size() - 1; i >= 0; i--) {
                    int[] values = {Graph.GraphNode.currentInput.get(i), Graph.GraphNode.currentOutput.get(i)};

                    for (int j = Graph.GraphNode.currentInput.size() - 1; j >= 0; j--) {
                        if (i == j) continue;
                        int[] values2 = {Graph.GraphNode.currentInput.get(j), Graph.GraphNode.currentOutput.get(j)};

                        if (values[0] == values2[0] && values[1] == values2[1]) {
                            Graph.GraphNode.currentInput.remove(i);
                            Graph.GraphNode.currentOutput.remove(i);
                        }
                    }
                }
            }

            if (ImNodes.isLinkCreated(LINK_A, LINK_B)) {
                final Graph.GraphNode source = graph.findByOutput(LINK_A.get());
                final Graph.GraphNode target = graph.findByInput(LINK_B.get());
                if (source != null && target != null && source.outputNodeId != target.id) {
                    source.outputNodeId = target.id;
                }
            }

            if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                final int hoveredNode = ImNodes.getHoveredNode();
                if (hoveredNode != -1) {
                    ImGui.openPopup("node_context");
                    ImGui.getStateStorage().setInt(ImGui.getID("delete_node_id"), hoveredNode);
                } else if (isEditorHovered) {
                    ImGui.openPopup("node_editor_context");
                }
            }

            if (ImGui.isPopupOpen("node_context")) {
                final int targetNode = ImGui.getStateStorage().getInt(ImGui.getID("delete_node_id"));
                if (ImGui.beginPopup("node_context")) {
                    if (ImGui.button("Delete " + graph.nodes.get(targetNode).getName())) {
                        graph.nodes.remove(targetNode);
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.endPopup();
                }
            }

            if (ImGui.beginPopup("node_editor_context")) {
                if (ImGui.button("Create New Node")) {
                    final Graph.GraphNode node = graph.createGraphNode(2, 2);
                    ImNodes.setNodeScreenSpacePos(node.id, ImGui.getMousePosX(), ImGui.getMousePosY());
                    ImGui.closeCurrentPopup();
                }
                ImGui.endPopup();
            }

            // TODO: 26/04/2022 Same bind key to save level scene... May be dangerous
            if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_S)) {
                ImNodes.saveCurrentEditorStateToIniFile("assets/animations/animation.txt");
            }

        } else
            collapsed = true;

        ImGui.end();
    }

    public boolean isCollapsed() {
        return collapsed;
    }
}
