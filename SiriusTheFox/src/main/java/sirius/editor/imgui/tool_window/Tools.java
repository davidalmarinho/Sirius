package sirius.editor.imgui.tool_window;

public enum Tools {
    SELECTION_TOOL("Selection tool"),
    TEXT_TOOL("Text tool");

    private final String TOOL;

    Tools(String tool) {
        this.TOOL = tool;
    }

    @Override
    public String toString() {
        return TOOL;
    }
}
