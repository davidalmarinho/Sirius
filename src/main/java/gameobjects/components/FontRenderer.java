package gameobjects.components;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import sirius.editor.imgui.JImGui;
import sirius.rendering.BatchFont;
import sirius.rendering.Color;
import sirius.rendering.fonts.Font;
import sirius.utils.AssetPool;
import sirius.utils.Settings;

public class FontRenderer extends Component {
    private transient Font font;
    private float scale;
    private String fontpath;
    private int previousMaxTextLength;
    private int maxTextLength;
    private transient BatchFont batchFont;

    private transient int currentItem = -1;

    public FontRenderer() {
        font = new Font(AssetPool.getFont(Settings.Files.CURRENT_FONT_PATH));
        this.maxTextLength = 32;
        this.previousMaxTextLength = maxTextLength;
        this.batchFont = new BatchFont(maxTextLength);
        this.scale = Settings.GameObjects.DEFAULT_FONT_SCALE;
        // this.scale = 1.0f;
        // this.scale = 0.25f;
    }

    @Override
    public void start() {
        this.batchFont.initBatch();
    }

    public void render() {
        batchFont.addText("I am a Text  box!", gameObject.getPosition().x, gameObject.getPosition().y,
                this.scale, Color.BLACK.getDecimal32());
        batchFont.flushBatch();
    }

    @Override
    public void imgui() {
        boolean reloadFont = false;

        // Set the scale of the text
        this.scale = JImGui.dragFloat("Font Scale:", this.scale);

        // Load desired font
        currentItem = JImGui.listOpenArrow("Font list:", currentItem, AssetPool.getFontsNames());
        if (ImGui.isMouseReleased(ImGuiMouseButton.Left) && currentItem >= 0) {
            this.fontpath = AssetPool.getFontsPaths()[currentItem];
            reloadFont = true;
            currentItem = -1;
        }

        if (reloadFont) {
            font = AssetPool.getFont(this.fontpath);
        }

        if (previousMaxTextLength != maxTextLength) {
            batchFont.reset(maxTextLength);
            this.previousMaxTextLength = maxTextLength;
        }
    }
}
