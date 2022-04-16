package sirius.imgui;

import imgui.type.ImString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    public int nextNodeId = 1;
    public int nextPinId = 100;

    public final Map<Integer, GraphNode> nodes = new HashMap<>();

    public Graph() {
        /*final GraphNode first = createGraphNode(2, 2);
        final GraphNode second = createGraphNode(2, 2);
        first.outputNodeId = second.nodeId;*/
    }

    public GraphNode createGraphNode(int numberInputs, int numberOutputs) {
        int[] inputIds = new int[numberInputs];
        int[] outputIds = new int[numberOutputs];


        // TODO: 15/04/2022 change this if dint work
        int inputId;
        for (int i = 0; i < numberInputs; i++) {
            inputId = nextPinId++;
            inputIds[i] = inputId;
        }

        int outputId;
        for (int i = 0; i < numberOutputs; i++) {
            outputId = nextPinId++;
            outputIds[i] = outputId;
        }

        final GraphNode node = new GraphNode(nextNodeId++, inputIds, outputIds);
        this.nodes.put(node.id, node);
        return node;
    }

    public GraphNode findByInput(final long inputPinId) {
        for (GraphNode node : nodes.values()) {
            for (int i = 0; i < node.inputPinIds.length; i++) {
                if (node.inputPinIds[i] == inputPinId) {
                    return node;
                }
            }
        }
        return null;
    }

    public GraphNode findByOutput(final long outputPinId) {
        for (GraphNode node : nodes.values()) {
            for (int i = 0; i < node.outputPinIds.length; i++) {
                if (node.outputPinIds[i] == outputPinId) {
                    return node;
                }
            }
        }
        return null;
    }

    public static final class GraphNode {
        public ImString name;
        public ImString trigger;
        public final int id;
        public final int[] inputPinIds;
        public final int[] outputPinIds;
        public static List<Integer> currentInput = new ArrayList<>();
        public static List<Integer> currentOutput = new ArrayList<>();

        public int outputNodeId = -1;

        public GraphNode(int id, int[] inputPinIds, int[] outputPinIds) {
            this.id = id;
            this.name = new ImString("name" + id, 32);
            this.trigger      = new ImString("trigger", 128);
            this.inputPinIds  = inputPinIds;
            this.outputPinIds = outputPinIds;
        }

        public int[] getInputPinIds() {
            return inputPinIds;
        }

        public int[] getOutputPinIds() {
            return outputPinIds;
        }

        public String getName() {
            return "Node " + (char) (64 + id);
        }
    }
}
