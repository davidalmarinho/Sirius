package sirius.editor.imgui.tool_window;

import gameobjects.components.Sprite;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import sirius.editor.imgui.GuiWindow;
import sirius.rendering.spritesheet.Spritesheet;
import sirius.utils.AssetPool;

public class ToolWindow extends GuiWindow {
    private final Tools[] TOOLS;
    private int currentToolIndex;

    public ToolWindow() {
        super();
        TOOLS = new Tools[]{Tools.SELECTION_TOOL, Tools.TEXT_TOOL};
        currentToolIndex = 0;
    }

    public void imgui() {
        if (!isVisible()) return;

        ImGui.begin("Tools", show);
        toolsLayout(AssetPool.getSpritesheet("assets/images/tools/tools.png"));
        ImGui.end();
    }

    private void toolsLayout(Spritesheet spritesheet) {
        // Gets the window's positions
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        // Gets item's spacing
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + ImGui.getWindowSizeX();
        for (int i = 0; i < spritesheet.size(); i++) {
            Sprite sprite = spritesheet.getSprite(i);
            float iconWidth = 32.0f;
            float iconHeight = 32.0f;

            int id = sprite.getTextureID();
            Vector2f[] texCoords = sprite.getTextureCoordinates();

            // Each texture has the spritesheet id, so all textures have the same id, so there is needed to pushID()
            ImGui.pushID(i);

            if (ImGui.imageButton(id, iconWidth, iconHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                currentToolIndex = i;
            }

            // Show tip when cursor above tool
            if (ImGui.isItemHovered())
                ImGui.setTooltip(TOOLS[i].toString());

            // After we don't want to worry about that we have changed textures' id, so let's replace it again
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);

            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + iconWidth;

            // Keep in the same line if we still have items and if the current item isn't bigger than the window itself
            if (i + 1 < spritesheet.size() && nextButtonX2 < windowX2)
                ImGui.sameLine();
        }
    }

    public Tools getCurrentTool() {
        return TOOLS[currentToolIndex];
    }
}
