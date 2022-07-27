package gameobjects.components;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import sirius.editor.imgui.JImGui;
import sirius.rendering.BatchFont;
import sirius.rendering.Color;
import sirius.rendering.fonts.Font;
import sirius.rendering.fonts.Glyph;
import sirius.utils.AssetPool;
import sirius.utils.Settings;

public class FontRenderer extends Component {
    private transient Font font;
    private float scale;
    private String fontpath;
    private int previousMaxTextLength;
    private int maxTextLength;
    private transient BatchFont batchFont;
    private Color color;

    private transient int currentItem = -1;

    public FontRenderer() {
        font = new Font(AssetPool.getFont(Settings.Files.CURRENT_FONT_PATH));
        this.maxTextLength = 32;
        this.previousMaxTextLength = maxTextLength;
        this.batchFont = new BatchFont(maxTextLength);
        this.scale = Settings.GameObjects.DEFAULT_FONT_SCALE;
        this.color = new Color(Color.BLACK);
    }

    @Override
    public void start() {
        this.batchFont.initBatch();
    }

    public void render() {
        TextBox textBox = gameObject.getComponent(TextBox.class);
        String text = textBox.getText();

        float greatestHeight = font.getGreatestHeight() * scale;

        // for (int i = 0; i < text.length(); i++) {
        //     char c = text.charAt(i);
        //     if (batchFont.getGlyph(c).height > greatestHeight) {
        //         greatestHeight = batchFont.getGlyph(c).height * scale;
        //     }
        // }

        final float TEXT_BOX_WIDTH = textBox.getWidth();
        float currentWidth = 0.0f;

        int numParagraphs = 0;

        int lastStrIndex = 0;
        // Go throughout each character of the desired text to render
        for (int i = 0; i < text.length(); i++) {
            Glyph glyph = batchFont.getGlyph(text.charAt(i));
            if (glyph == null) continue;

            currentWidth += glyph.width * scale;

            if (currentWidth > textBox.getWidth()) {
                // batchFont.addText(text.substring(lastStrIndex, i), gameObject.getPosition().x - TEXT_BOX_WIDTH / 2.0f, gameObject.getPosition().y + textBox.getHeight() / 2.0f - greatestHeight - greatestHeight * numParagraphs, this.scale, color);
                batchFont.addText(text.substring(lastStrIndex, i), gameObject.getPosition().x - TEXT_BOX_WIDTH / 2.0f, gameObject.getPosition().y + textBox.getHeight() / 2.0f - greatestHeight - greatestHeight * numParagraphs, this.scale, color);

                currentWidth = 0;
                lastStrIndex = i;
                numParagraphs++;
            }

            if (currentWidth < textBox.getWidth() && i == text.length() - 1) {
                // batchFont.addText(text.substring(lastStrIndex), gameObject.getPosition().x - TEXT_BOX_WIDTH / 2.0f, gameObject.getPosition().y + textBox.getHeight() / 2.0f - greatestHeight - greatestHeight * numParagraphs, this.scale, color);
                batchFont.addText(text.substring(lastStrIndex), gameObject.getPosition().x - TEXT_BOX_WIDTH / 2.0f, gameObject.getPosition().y + textBox.getHeight() / 2.0f - greatestHeight - greatestHeight * numParagraphs, this.scale, color);
            }
        }

        batchFont.flushBatch();
    }

    @Override
    public void imgui() {
        boolean reloadFont = false;

        // Select color
        JImGui.colorPicker4("Color: ", color);

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

        maxTextLength = gameObject.getComponent(TextBox.class).getText().length();
        if (previousMaxTextLength != maxTextLength) {
            batchFont.reset(maxTextLength);
            this.previousMaxTextLength = maxTextLength;
        }
    }
}
