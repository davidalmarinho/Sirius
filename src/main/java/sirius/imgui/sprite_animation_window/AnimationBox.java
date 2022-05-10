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
    private final int ID;
    private ImString trigger;
    public float x, y;
    private float width, height, lastWidth;

    private boolean mouseAboveAnimationBox;

    private PointField[] pointFields;
    private boolean updatePointFields;
    private boolean mayCheckForUncheckedPoints;

    private final float THICKNESS = 10.0f;
    private final float ROUNDING  = 20.0f;

    public AnimationBox(String trigger, float x, float y) {
        maxId++;
        this.ID = maxId;
        this.trigger = new ImString(trigger, 32);
        this.x       = x;
        this.y       = y;

        this.width       = 128.0f;
        this.height      = 128.0f;
        this.lastWidth   = this.width;
        this.pointFields = new PointField[4];
        setPointFields();
    }

    public AnimationBox(String trigger, ImVec2 position) {
        this(trigger, position.x, position.y);
    }

    private void setPointFields() {
        // up
        float yPointField0     = y - getHeight() / 2;
        float widthPointField0 = getWidth() - THICKNESS * 2 - ROUNDING;

        // right
        float xPointField1      = x + getWidth() / 2;
        float heightPointField1 = getHeight() - ROUNDING * 2;

        // down
        float yPointField2     = y + getHeight() / 2;
        float widthPointField2 = getWidth() - THICKNESS * 2 - ROUNDING;

        // left
        float xPointField3      = x - getWidth() / 2;
        float heightPointField3 = getHeight() - ROUNDING * 2;

        // Create the point fields if they don't exist --areas where we are able to create points
        if (this.pointFields[0] == null) {
            // Up
            this.pointFields[0] = new PointField(x, yPointField0, widthPointField0, THICKNESS);
            // Right
            this.pointFields[1] = new PointField(xPointField1, y, THICKNESS, heightPointField1);
            // Down
            this.pointFields[2] = new PointField(x, yPointField2, widthPointField2, THICKNESS);
            // Left
            this.pointFields[3] = new PointField(xPointField3, y, THICKNESS, heightPointField3);
        } else {
            // Up
            this.pointFields[0].setPosition(x, yPointField0);
            this.pointFields[0].setSize(widthPointField0, THICKNESS);
            // Right
            this.pointFields[1].setPosition(xPointField1, y);
            this.pointFields[1].setSize(THICKNESS, heightPointField1);
            // Down
            this.pointFields[2].setPosition(x, yPointField2);
            this.pointFields[2].setSize(widthPointField2, THICKNESS);
            // Left
            this.pointFields[3].setPosition(xPointField3, y);
            this.pointFields[3].setSize(THICKNESS, heightPointField3);
        }
    }

    private void deleteUnlinkedPoints() {
        if (mayCheckForUncheckedPoints) {
            // We do this check to make sure that the point is unlinked
            if (SpriteAnimationWindow.pointList.size() % 2 == 0) {
                mayCheckForUncheckedPoints = false;
                return;
            }

            // If is an unlinked point, remove it
            for (PointField pointField : pointFields) {
                if (pointField.hasUnLinkedPoint) {
                    // Remove the last points from the lists
                    SpriteAnimationWindow.pointList.remove(SpriteAnimationWindow.pointList.size() - 1);
                    pointField.removeLastPoint();
                    pointField.hasUnLinkedPoint = false;
                }
            }
            mayCheckForUncheckedPoints = false;
        }


        if (ImGui.isMouseReleased(ImGuiMouseButton.Left)) {
            mayCheckForUncheckedPoints = true;
        }
    }

    private void drawAnimationBox(ImVec2 origin, ImVec2 scrolling) {
        ImDrawList drawList = ImGui.getWindowDrawList();

        // Reserve the region to draw the animation box
        ImGui.setCursorPos(x - getWidth() / 2 + scrolling.x, + y - getHeight() / 2 + scrolling.y);
        ImGui.beginChild("box" + ID, width, height, false, ImGuiWindowFlags.AlwaysAutoResize
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
        ImGui.pushID("nodeTrigger: " + ID);

        // Changes the animation box input text box size
        if (charsNumber < 10)
            ImGui.setNextItemWidth(val * 10);
        else
            ImGui.setNextItemWidth(Math.min(currentSize, maxSize));

        ImGui.inputText("", this.trigger, ImGuiInputTextFlags.AutoSelectAll);
        ImGui.popID();

        // Checks if the mouse is above of the AnimationBox
        mouseAboveAnimationBox = ImGui.isWindowHovered();

        ImGui.endChild();
    }

    public void imgui(ImVec2 origin, ImVec2 scrolling) {
        // Draw the outlines of the animation box
        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRect(
                origin.x + x - getWidth() / 2,
                origin.y + y - getHeight() / 2,
                origin.x + x + getWidth() / 2,
                origin.y + y + getHeight() / 2,
                ImColor.intToColor(255, 255, 255, 255), ROUNDING, ImDrawFlags.RoundCornersAll, THICKNESS);

        drawAnimationBox(origin, scrolling);

        // Create points to after join them and form a line, if the left mouse button isn't realised
        boolean mouseReleasedOrClicked =
                ImGui.isMouseClicked(ImGuiMouseButton.Left) || ImGui.isMouseReleased(ImGuiMouseButton.Left);
        for (PointField pointField : pointFields) {
            if (pointField.hasUnLinkedPoint && SpriteAnimationWindow.pointList.size() % 2 == 0)
                pointField.hasUnLinkedPoint = false;

            // pointField.debug(origin);
            if (pointField.isMouseAbove(origin) && !mouseAboveAnimationBox) {
                drawList.addCircleFilled(ImGui.getMousePosX(), ImGui.getMousePosY(),
                        6.0f, ImColor.intToColor(247, 179, 43, 150));

                // Establish a union between 2 point --draw a line between 2 points
                if (mouseReleasedOrClicked) {
                    ImVec2 pointPos = new ImVec2(ImGui.getMousePosX() - origin.x, ImGui.getMousePosY() - origin.y);
                    Point newPoint = new Point(pointPos, 6.0f);
                    pointField.addPoint(newPoint);
                    SpriteAnimationWindow.pointList.add(newPoint);

                    // Mark in which point field a possible unlinked point is
                    if (SpriteAnimationWindow.pointList.size() % 2 != 0)
                        pointField.hasUnLinkedPoint = true;
                }
            }

            // Draw the points
            for (Point point : pointField.getPointList()) {
                point.imgui(origin);
            }
        }

        // When we shrink or enlarge the animation box, we should update the point fields' interactions rectangles
        float dtWidth = 0.0f;
        if (lastWidth != width) {
            dtWidth   = width - lastWidth;
            lastWidth = width;
            updatePointFields = true;
        }

        // Updates the point fields' positions and sizes, because the animation box has been resized
        if (updatePointFields) {
            updatePointFields = false;
            setPointFields();

            // Update points --move them because the animation box has been resized
            for (PointField pointField : pointFields) {
                for (Point point : pointField.getPointList()) {
                    // Move left
                    float moveValue = dtWidth / 4; // honestly, I don't know why I divide by 4, it was a guess,
                    // but it just make it works
                    if (point.position.x < this.x) {
                        point.position.x -= moveValue;
                        SpriteAnimationWindow.pointList.stream()
                                .filter(saPoint -> point.getId() == saPoint.getId())
                                .forEach(saPoint -> saPoint.position.x -= moveValue);
                    // Move right
                    } else {
                        point.position.x += moveValue;
                        SpriteAnimationWindow.pointList.stream()
                                .filter(saPoint -> point.getId() == saPoint.getId())
                                .forEach(saPoint -> saPoint.position.x += moveValue);
                    }
                }
            }
        }

        deleteUnlinkedPoints();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
