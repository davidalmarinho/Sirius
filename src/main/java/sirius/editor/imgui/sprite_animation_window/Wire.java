package sirius.editor.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiMouseButton;
import sirius.utils.JMath;

public class Wire {
    private Point from;
    private Point to;
    private transient boolean selected;
    private transient boolean hovered;

    public Wire(Point from, Point to) {
        this.from = from;
        this.to   = to;
    }

    public Wire() {
        this(new Point(), new Point());
    }

    public Wire(Wire newWire) {
        this.from = new Point(newWire.getStartPoint());
        this.to   = new Point(newWire.getEndPoint());
    }

    public void imgui() {
        // ImVec2 mousePosInCanvas = new ImVec2(SpriteAnimationWindow.getAnimator().getMousePosition());
        ImVec2 mousePosInCanvas = SpriteAnimationWindow.getAnimator().getMousePosition();
        // ImVec2 origin = new ImVec2(SpriteAnimationWindow.getAnimator().getOrigin());
        ImVec2 origin = SpriteAnimationWindow.getAnimator().getOrigin();

        ImDrawList drawList = ImGui.getWindowDrawList();
        int color = ImColor.intToColor(255, 255, 0, 255);

        if (selected) {
            color = ImColor.intToColor(0, 255, 0, 255);
        }

        if (!ImGui.isPopupOpen(SpriteAnimationWindow.getAnimator().POPUP_ANIMATOR_MENU)) {
            hovered = JMath.distanceToSegmentSquared(mousePosInCanvas.x, mousePosInCanvas.y,
                    getStartX(), getStartY(), getEndX(), getEndY()) < 50.0f;

            if (hovered) {
                color = ImColor.intToColor(0, 255, 0, 255);
            }

            boolean mouseLeft = ImGui.isMouseClicked(ImGuiMouseButton.Left);
            if ((mouseLeft || ImGui.isMouseClicked(ImGuiMouseButton.Right)) && hovered) {
                this.selected = true;
            }
            if (mouseLeft && !hovered) {
                this.selected = false;
            }
        }

        drawList.addLine(
                getStartX() + origin.x,
                getStartY() + origin.y,
                getEndX() + origin.x,
                getEndY() + origin.y,
                color,
                SpriteAnimationWindow.getAnimator().getThickness());

    }

    public float getStartX() {
        return from.position.x;
    }

    public float getStartY() {
        return from.position.y;
    }

    public void setStart(float startX, float startY) {
        this.from.position.x = startX;
        this.from.position.y = startY;
    }

    public float getEndX() {
        return to.position.x;
    }

    public float getEndY() {
        return to.position.y;
    }

    public void setEnd(float endX, float endY) {
        this.to.position.x = endX;
        this.to.position.y = endY;
    }

    public Point getStartPoint() {
        return this.from;
    }

    public void setStartPoint(Point newPoint) {
        this.from = newPoint;
    }

    public Point getEndPoint() {
        return this.to;
    }

    public void setEndPoint(Point newPoint) {
        this.to = newPoint;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isHovered() {
        return hovered;
    }
}
