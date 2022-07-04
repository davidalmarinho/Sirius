package sirius.editor.imgui;

import com.sun.istack.internal.NotNull;
import gameobjects.GameObject;
import gameobjects.Prefabs;
import gameobjects.components.Sprite;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector4f;
import sirius.rendering.Color;
import sirius.rendering.spritesheet.Spritesheet;

public class JImGui {
    // Keeps the game object selected on the sprites layout
    private static GameObject selectedGameObject;

    private final static float defaultColumnWidth = 220.0f;

    public static void drawVec2Control(String label, Vector2f values) {
        drawVec2Control(label, values, 0.0f, defaultColumnWidth);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        drawVec2Control(label, values, resetValue, defaultColumnWidth);
    }

    public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidth);
        ImGui.text(label);;
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeight + 3.0f, lineHeight);
        float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;

        ImGui.pushItemWidth(widthEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        if (ImGui.button("X", buttonSize.x, buttonSize.y))
            values.x = resetValue;

        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.dragFloat("##x", vecValuesX, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushItemWidth(widthEach);

        if (ImGui.button("Y", buttonSize.x, buttonSize.y))
            values.y = resetValue;

        ImGui.popStyleColor(3);

        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();

        ImGui.nextColumn();

        values.x = vecValuesX[0];
        values.y = vecValuesY[0];

        ImGui.columns(1);
        ImGui.popStyleVar();
        ImGui.popID();
    }

    public static float dragFloat(String label, float value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        float[] valArray = {value};
        ImGui.dragFloat("##dragFloat", valArray, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valArray[0];
    }

    public static float dragFloatPopups(String label, float value) {
        ImGui.pushID(label);

        ImGui.setNextItemWidth(defaultColumnWidth);
        ImGui.textUnformatted(label);

        ImGui.sameLine();
        ImGui.setNextItemWidth(defaultColumnWidth / 2);
        float[] valArray = {value};
        ImGui.dragFloat("##dragFloat", valArray, 0.1f);

        ImGui.popID();

        return valArray[0];
    }

    public static int dragInt(String label, int value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        int[] valArray = {value};
        ImGui.dragInt("##dragInt", valArray, 0.1f);

        ImGui.columns(1);
        ImGui.popID();

        return valArray[0];
    }

    public static boolean colorPicker4(String label, Vector4f vec4Color) {
        boolean res = false;

        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);;
        ImGui.nextColumn();

        float[] imColor = {vec4Color.x, vec4Color.y, vec4Color.z, vec4Color.w};
        if (ImGui.colorEdit4("##colorPicker", imColor)) {
            vec4Color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            res = true;
        }

        ImGui.columns(1);
        ImGui.popID();

        return res;
    }

    public static String inputText(String label, String text) {
        return inputText(label, text, defaultColumnWidth, 256);
    }

    public static String inputText(String label, String text, int maxTextLength) {
        return inputText(label, text, 0, maxTextLength);
    }

    public static String defaultInputText(String label, String text, int maxTextLength) {
        ImGui.pushID(label);

        ImString outString = new ImString(text, maxTextLength);
        ImGui.text(label);

        ImGui.sameLine();

        if (ImGui.inputText("##" + label, outString, ImGuiInputTextFlags.AutoSelectAll)) {
            ImGui.popID();
            return outString.get();
        }

        ImGui.popID();

        return text;
    }

    public static String inputText(String label, String text, float columnWidth, int maxTextLength) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        if (columnWidth != 0)
            ImGui.setColumnWidth(1, columnWidth);

        ImString outString = new ImString(text, maxTextLength);
        if (ImGui.inputText("##" + label, outString)) {
            ImGui.columns(1);
            ImGui.popID();

            return outString.get();
        }

        ImGui.columns(1);
        ImGui.popID();

        return text;
    }

    public static int inputInt(int step, String label, int value) {
        ImGui.pushID(label);

        ImGui.columns(2);
        ImGui.setColumnWidth(0, defaultColumnWidth);
        ImGui.text(label);
        ImGui.nextColumn();

        ImInt outInt = new ImInt(value);
        if (ImGui.inputInt("##" + label, outInt, step)) {
            ImGui.columns(1);
            ImGui.popID();

            return outInt.get();
        }

        ImGui.columns(1);
        ImGui.popID();

        return value;
    }

    public static boolean checkBox(String label, boolean value) {
        ImGui.pushID(label);

        ImBoolean imBool = new ImBoolean(value);
        ImGui.checkbox(label, imBool);

        ImGui.popID();

        return imBool.get();
    }

    /**
     * Checks if a key was pressed based on ascii table.
     * Range: [0, 127]
     *
     * @return true is any key was pressed.
     */
    public static boolean isAnyKeyPressed() {
        boolean pressed = false;
        for (int i = 0; i < 127; i++) {
            if (ImGui.isKeyPressed(i)) {
                pressed = true;
                break;
            }
        }

        return pressed;
    }

    public static boolean imgButton(int id, Sprite sprite) {
        return imgButton(id, sprite, new Color(0.0f, 0.0f, 0.0f, 0.0f));
    }

    /**
     * Place an ImGui button with a customized image.
     *
     * @param sprite The image you want to draw in the button
     * @param id An identifier. Probably the current index of the loop you are going throughout.
     * @param backgroundColor Background customized color for button
     * @return true, if the button was pressed.
     */
    public static boolean imgButton(int id, Sprite sprite, Color backgroundColor) {
        float r = backgroundColor.getRed();
        float g = backgroundColor.getGreen();
        float b = backgroundColor.getBlue();
        float a = backgroundColor.getOpacity();

        boolean pressed;

        ImGui.pushID(id);

        int texId            = sprite.getTextureID();
        Vector2f[] texCoords = sprite.getTextureCoordinates();
        float spriteWidth    = sprite.getWidth() * 2;
        float spriteHeight   = sprite.getHeight() * 2;

        pressed = ImGui.imageButton(
                texId, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y, 0,
                r, g, b, a);

        ImGui.popID();

        return pressed;
    }

    public static void image(Sprite sprite) {
        int texId = sprite.getTextureID();

        Vector2f[] texCoords = sprite.getTextureCoordinates();
        float spriteWidth    = sprite.getWidth() * 2;
        float spriteHeight   = sprite.getHeight() * 2;

        ImGui.image(texId, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y);
    }
    public static void image(Sprite sprite, float width, float height) {
        int texId = sprite.getTextureID();

        Vector2f[] texCoords = sprite.getTextureCoordinates();

        ImGui.image(texId, width, height, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static boolean spritesLayout(Spritesheet spritesheet, ImVec2 customWindowSize) {
        boolean pressed = false;

        // Gets the window's positions
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        // Gets item's spacing
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + customWindowSize.x;
        for (int i = 0; i < spritesheet.size(); i++) {
            Sprite sprite = spritesheet.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;

            int id = sprite.getTextureID();
            Vector2f[] texCoords = sprite.getTextureCoordinates();

            // Each texture has the spritesheet id, so all textures have the same id, so there is needed to pushID()
            ImGui.pushID(i);

            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                pressed = true;
                selectedGameObject = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
            }

            // After we don't want to worry about that we have changed textures' id, so let's replace it again
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);

            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            // Keep in the same line if we still have items and if the current item isn't bigger than the window itself
            if (i + 1 < spritesheet.size() && nextButtonX2 < windowX2)
                ImGui.sameLine();
        }

        return pressed;
    }

    /**
     * Shows a layout with buttons where each button has a sprite and if you click in a button, it generates a
     * game object using {@link gameobjects.Prefabs#generateSpriteObject(Sprite, float, float)} method.
     * The game object is kept in {@link JImGui#selectedGameObject} until next frame.
     *
     * @param spritesheet Spritesheet where the sprites will be caught.
     * @return true if we click in a button.
     */
    public static boolean spritesLayout(Spritesheet spritesheet) {
        return spritesLayout(spritesheet, ImGui.getWindowSize());
    }

    /**
     * @return game object generated from {@link JImGui#spritesLayout(Spritesheet)} or null.
     */
    public static GameObject getSelectedGameObject() {
        // TODO: 18/05/2022 This should be a copy
        return selectedGameObject;
    }

    public static int listBox(String label, int currentItem, @NotNull String[] items, int heightItems) {
        ImInt imInt = new ImInt(currentItem);
        ImGui.listBox("##" + label, imInt, items, heightItems);

        return imInt.get();
    }

    /**
     * Uses {@link ImGui#beginListBox(String)} API.
     */
    public static int doListBox(String label, int currentItem, @NotNull String[] items) {
        if (ImGui.beginListBox("##" + label)) {
            for (int i = 0; i < items.length; i++) {

                boolean is_selected = (currentItem == i);
                if (ImGui.selectable(items[i], is_selected)) {
                    System.out.println(currentItem);
                    currentItem = i;
                }

                // Set the initial focus when opening the combo (scrolling + keyboard navigation focus)
                if (is_selected)
                    ImGui.setItemDefaultFocus();
            }
            ImGui.endListBox();
        }
        return currentItem;
    }

    public static int listLeaf(String label, int currentItem, String[] items) {
        if (label.isEmpty())
            label = "##";

        return list(label, currentItem, items, ImGuiTreeNodeFlags.Leaf);
    }

    public static int listOpenArrow(String label, int currentItem, String[] items) {
        if (label.isEmpty())
            label = "##";

        return list(label, currentItem, items, ImGuiTreeNodeFlags.OpenOnArrow);
    }

    private static int list(String label, int currentItem, String[] items, int imGuiTreeNodeFlags) {
        ImGui.pushID(label);

        if (ImGui.treeNodeEx("" + label, imGuiTreeNodeFlags)) {
            for (int i = 0; i < items.length; i++) {
                if (ImGui.selectable(items[i], currentItem == i))
                    currentItem = i;
            }
            ImGui.treePop();
        }
        ImGui.popID();

        return currentItem;
    }
}
