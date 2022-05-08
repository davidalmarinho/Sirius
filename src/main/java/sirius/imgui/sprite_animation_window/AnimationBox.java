package sirius.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import org.joml.Vector2f;

public class AnimationBox {
    private int id;
    private ImString trigger;
    public float x, y;
    private float width, height;
    private Vector2f pointsFieldSize;

    public AnimationBox(String trigger, float x, float y) {
        this.id++;
        this.trigger = new ImString(trigger, 32);
        this.x = x;
        this.y = y;
        this.width = 128.0f;
        this.height = 128.0f;
        this.pointsFieldSize = new Vector2f();
    }
    public AnimationBox(String trigger, ImVec2 position) {
        this(trigger, position.x, position.y);
    }

    public void imgui(ImVec2 origin, ImVec2 scrolling) {
        // TODO: 05/05/2022 Child window has weird movement when moving in canvas
        ImGui.setCursorPos(ImGui.getCursorPosX() + x - getWidth() / 2 + scrolling.x,
                ImGui.getCursorPosY() + y - getHeight() / 2 + scrolling.y);
        ImGui.beginChild("box" + id, width, height, false, ImGuiWindowFlags.AlwaysAutoResize
                | ImGuiWindowFlags.NoCollapse);

        // Check what size the animation box will have --it changes depending on how many characters we have in text field
        final float BREAKER_WIDTH = 48f;
        float val = 11.8f;
        int charsNumber = this.trigger.toString().length();
        float currentSize = (charsNumber + 1) * val; // +1 to maintain the integrity of this logic
        float maxSize = 20.8f * val;

        // Changes the animation box size
        if (charsNumber < 10)
            this.width = val * 10 + BREAKER_WIDTH;
        else
            this.width = Math.min(currentSize + BREAKER_WIDTH, maxSize + BREAKER_WIDTH);

        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(origin.x + x - getWidth() / 2,
                origin.y + y - getHeight() / 2,
                origin.x + x + getWidth() / 2,
                origin.y + y + getHeight() / 2,
                ImColor.intToColor(112, 16, 20, 255), 20.0f);

        // Center the animation box input text box
        ImGui.setCursorPos(ImGui.getCursorPosX() + 22, ImGui.getCursorPosY() + getHeight() / 3);
        ImGui.pushID("nodeTrigger: " + id);

        // Changes the animation box input text box size
        if (charsNumber < 10)
            ImGui.setNextItemWidth(val * 10);
        else
            ImGui.setNextItemWidth(Math.min(currentSize, maxSize));

        ImGui.inputText("", this.trigger, ImGuiInputTextFlags.AutoSelectAll);
        ImGui.popID();
        ImGui.endChild();

        // TODO: 08/05/2022 In this outlines lines can be created
        // Draw the outlines of the box
        drawList.addRect(origin.x + x - getWidth() / 2,
                origin.y + y - getHeight() / 2,
                origin.x + x + getWidth() / 2,
                origin.y + y + getHeight() / 2,
                ImColor.intToColor(255, 255, 255, 255), 20.0f, ImDrawFlags.RoundCornersAll, 6.0f);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
