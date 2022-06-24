package gameobjects.components.fonts;

import gameobjects.components.Component;
import imgui.ImGui;
import sirius.editor.imgui.JImGui;
import sirius.utils.AssetPool;

public class FontRenderer extends Component {
    private String showingText;
    private String fontpath;
    private transient int currentItem = -1;

    @Override
    public void update(float dt) {

    }

    @Override
    public void imgui() {
        // Set showing text
        this.showingText = JImGui.inputText("Font path:", this.showingText);
        // TODO: 23/06/2022 Delete button
        if (ImGui.button("Load showing text")) {

        }

        // Load font
        currentItem = JImGui.list("test", currentItem, AssetPool.getFontsNames());
        if (ImGui.button("Load") && currentItem >= 0) {
            this.fontpath = AssetPool.getFontsPaths()[currentItem];
            // TODO: 23/06/2022 Change actual font
            currentItem = -1;
        }
    }
}
