package sirius.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;

public class AnimationBox {
    public static int maxId = 0;
    private int id;
    private ImString trigger;
    public float x, y;
    private float width, height;
    private PointField[] pointFields;

    private final float THICKNESS = 10.0f;
    private final float ROUNDING  = 20.0f;

    public AnimationBox(String trigger, float x, float y) {
        maxId++;
        this.id = maxId;
        this.trigger = new ImString(trigger, 32);
        this.x = x;
        this.y = y;
        this.width = 128.0f;
        this.height = 128.0f;
        this.pointFields = new PointField[4];

        // Up
        this.pointFields[0] = new PointField(x, y - getHeight() / 2, getWidth(), THICKNESS);
        // Right
        this.pointFields[1] = new PointField(x + getWidth() / 2 + ROUNDING, y, THICKNESS, getHeight() - ROUNDING * 2);
        // Down
        this.pointFields[2] = new PointField(x, y + getHeight() / 2, getWidth(), THICKNESS);
        // Left
        this.pointFields[3] = new PointField(x - getWidth() / 2 - ROUNDING, y, THICKNESS, getHeight() - ROUNDING * 2);
    }

    public AnimationBox(String trigger, ImVec2 position) {
        this(trigger, position.x, position.y);
    }

    public void imgui(ImVec2 origin, ImVec2 scrolling) {
        // Draw the outlines of the box
        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRect(
                origin.x + x - getWidth() / 2,
                origin.y + y - getHeight() / 2,
                origin.x + x + getWidth() / 2,
                origin.y + y + getHeight() / 2,
                ImColor.intToColor(255, 255, 255, 255), ROUNDING, ImDrawFlags.RoundCornersAll, THICKNESS);

        // Reserve the region to draw the animation box
        ImGui.setCursorPos(x - getWidth() / 2 + scrolling.x, + y - getHeight() / 2 + scrolling.y);
        ImGui.beginChild("box" + id, width, height, false, ImGuiWindowFlags.AlwaysAutoResize
                | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoScrollbar);

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

        // ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(origin.x + x - getWidth() / 2,
                origin.y + y - getHeight() / 2,
                origin.x + x + getWidth() / 2,
                origin.y + y + getHeight() / 2,
                ImColor.intToColor(112, 16, 20, 255), ROUNDING);

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

        // Checks if the mouse is above of the AnimationBox
        boolean aboveChild = ImGui.isWindowHovered();

        ImGui.endChild();

        // Checks if a line can be created
        for (PointField pointField : pointFields) {
            // pointField.debug(origin);
            if (pointField.isMouseAbove(origin) && !aboveChild) {
                drawList.addCircleFilled(ImGui.getMousePosX(), ImGui.getMousePosY(),
                        6.0f, ImColor.intToColor(247, 179, 43, 150));

                // TODO: 08/05/2022 If 2 points doesn't exist, a line can't be drawn
                // Establish a union between 2 point --draw a line between 2 points
                if (ImGui.isMouseClicked(ImGuiMouseButton.Left) || ImGui.isMouseReleased(ImGuiMouseButton.Left)) {
                    ImVec2 pointPos = new ImVec2(ImGui.getMousePosX() - origin.x, ImGui.getMousePosY() - origin.y);
                    pointField.addPoint(new Point(pointPos, 6.0f));
                }
            }

            // Draw the points
            for (Point point : pointField.getPointList()) {
                point.imgui(origin);
            }
        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
