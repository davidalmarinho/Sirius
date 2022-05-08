package sirius.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class PointField {
    private Vector2f position;
    private Vector2f size;

    private List<Point> pointList;

    public PointField(float x, float y, float width, float height) {
        this.position = new Vector2f(x, y);
        this.size = new Vector2f(width, height);
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
                ImColor.intToColor(0, 0, 0, 255), 0.0f);
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

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getSize() {
        return size;
    }

    public float getWidth() {
        return this.size.x;
    }

    public float getHeight() {
        return this.size.y;
    }

    public List<Point> getPointList() {
        return pointList;
    }
}
