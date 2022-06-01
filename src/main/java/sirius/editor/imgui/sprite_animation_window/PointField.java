package sirius.editor.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;

import java.util.ArrayList;
import java.util.List;

public class PointField {
    private String name;
    private ImVec2 position;
    private ImVec2 size;

    private List<Point> pointList;

    // Mark the point field that has an un-linked point --if it has one, we will remove it when we release the
    // mouse left button.
    public transient boolean hasUnLinkedPoint = false;

    public PointField(String name, float x, float y, float width, float height) {
        this.name = name;
        this.position = new ImVec2(x, y);
        this.size = new ImVec2(width, height);
        this.pointList = new ArrayList<>();
    }

    /**
     * Shows the colliders' rectangles that detect if the mouse is above or not.
     * @param objPosToCanvas Position on the canvas.
     */
    public void debug(ImVec2 objPosToCanvas) {
        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(
                objPosToCanvas.x + getPosition().x - getWidth() / 2,
                objPosToCanvas.y + getPosition().y - getHeight() / 2,
                objPosToCanvas.x + getPosition().x + getWidth() / 2,
                objPosToCanvas.y + getPosition().y + getHeight() / 2,
                ImColor.intToColor(0, 255, 0, 255), 0.0f);
    }

    /**
     * Checks if the mouse is above a rectangle field
     *
     * @param canvasPosition Coordinates inside a canvas or something like that
     * @return true if the mouse is above the defined rectangle area
     */
    public boolean isMouseAbove(ImVec2 canvasPosition) {
        return ImGui.isMouseHoveringRect(
                canvasPosition.x + position.x - size.x / 2,
                canvasPosition.y + position.y - size.y / 2,
                canvasPosition.x + position.x + size.x / 2,
                canvasPosition.y + position.y + size.y / 2);
    }

    public String getName() {
        return name;
    }

    /**
     * Checks if the mouse is above a rectangle field
     *
     * @return true if the mouse is above the defined rectangle area
     */
    public boolean isMouseAbove() {
        return isMouseAbove(new ImVec2(0, 0));
    }

    /**
     * Adds a point to the pointList.
     * @param point Desired point to add to the pointList.
     */
    public void addPoint(Point point) {
        this.pointList.add(point);
    }

    public void removeLastPoint() {
        this.pointList.remove(this.pointList.size() - 1);
    }

    public ImVec2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position = new ImVec2(x, y);
    }

    public ImVec2 getSize() {
        return size;
    }

    public void setSize(float width, float height) {
        this.size = new ImVec2(width, height);
    }

    public float getWidth() {
        return this.size.x;
    }

    public void setWidth(float width) {
        this.size.x = width;
    }

    public float getHeight() {
        return this.size.y;
    }

    public void setHeight(float height) {
        this.size.y = height;
    }

    public List<Point> getPointList() {
        return pointList;
    }
}
