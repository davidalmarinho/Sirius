package sirius.imgui.sprite_animation_window;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import org.joml.Vector2f;
import sirius.utils.JMath;

import java.util.ArrayList;
import java.util.List;

// State Machine Child will also be called Canvas in the comments
public class StateMachineChild {
    private boolean hovered;
    public ImBoolean showStateMachineChild;

    public AnimationBox activeAnimationBox;
    public static List<Point> pointList;
    public static boolean lookMessyLines;
    private List<AnimationBox> animationBoxList;
    private List<Wire> wireList;
    private Wire wire;

    private ImVec2 scrolling;

    private boolean addingLine = false;
    private float thickness = 2.0f;

    private boolean mayOpenPopupWindow = false;

    static {
        pointList = new ArrayList<>();
    }

    public StateMachineChild() {
        this.showStateMachineChild = new ImBoolean(true);

        this.wire = new Wire();
        this.animationBoxList = new ArrayList<>();
        this.wireList = new ArrayList<>();
        this.scrolling = new ImVec2();
        animationBoxList.add(new AnimationBox("I'm a box", 300, 300));
    }

    /**
     * Get an animation box based on its id.
     *
     * @param id Animation box's id.
     * @return The animation box with desired id.
     */
    public AnimationBox getAnimationBox(int id) {
        return animationBoxList.stream()
                .filter(animationBox -> animationBox.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private void drawArrows(ImDrawList imDrawList, ImVec2 origin) {
        for (int i = 1; i < pointList.size(); i += 2) {
            if (pointList.size() < 1) break;
            if (i - 1 < 0) continue;

            // Get the middle point of the line
            Point pTo = pointList.get(i);
            Point pFrom = pointList.get(i - 1);

            // middlePoint = (xA + xB) / 2; (yA + yB) / 2
            float middleX = (pTo.position.x + pFrom.position.x) / 2;
            float middleY = (pTo.position.y + pFrom.position.y) / 2;

            Vector2f middleP = new Vector2f(middleX, middleY);

            Vector2f v1 = new Vector2f();
            Vector2f v3 = new Vector2f();

            // m --decline of the straight (y = mx + b)
            // m = (y2 - y1) / (x2 - x1)
            float m = (pFrom.position.y - pTo.position.y) / (pFrom.position.x - pTo.position.x);

            // m = tan(alpha) <=> alpha = tan-1(m)
            float angle = (float) Math.toDegrees(Math.atan(m));

            // Trade some coordinates between v1 and v3 --to draw the triangle as we want, always
            // pointing from pFrom to pTo
            if (pTo.position.x > pFrom.position.x) {
                v1.set(middleX - 10, middleY - 10);
                v3.set(middleX - 10, middleY + 10);
            } else {
                v1.set(middleX + 10, middleY + 10);
                v3.set(middleX + 10, middleY - 10);
            }

            JMath.rotate(v1, angle, middleP);
            JMath.rotate(middleP, angle, middleP);
            JMath.rotate(v3, angle, middleP);

            imDrawList.addTriangleFilled(
                    v1.x + origin.x, v1.y + origin.y,
                    middleP.x + origin.x, middleP.y + origin.y,
                    v3.x + origin.x, v3.y + origin.y,
                    ImColor.intToColor(247, 179, 43, 255));
        }
    }

    public void imgui(ImVec2 contentRegionAvailable) {
        activeAnimationBox = animationBoxList
                .stream()
                .filter(animationBox -> getAnimationBox(animationBox.getId()).isSelected())
                .findFirst()
                .orElse(null);

        ImVec2 canvasSize = contentRegionAvailable;   // Resize canvas to what's available
        ImVec2 canvasP0 = ImGui.getCursorScreenPos(); // ImDrawList API uses screen coordinates!

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ChildBg, ImColor.intToColor(50, 50, 50, 255));

        ImGui.beginChild("canvas", canvasSize.x, canvasSize.y, true,
                ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoScrollbar
                        | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.popStyleColor();
        ImGui.popStyleVar();

        if (canvasSize.x < 50.0f) canvasSize.x = 50.0f;
        if (canvasSize.y < 50.0f) canvasSize.y = 50.0f;
        ImVec2 canvasP1 = new ImVec2(canvasP0.x + canvasSize.x, canvasP0.y + canvasSize.y);

        // Draw border and background color
        ImGuiIO io = ImGui.getIO();
        ImDrawList drawList = ImGui.getWindowDrawList();

        // Check if the cursor is above of sprite animation window
        hovered = ImGui.isWindowHovered();

        // Lock scrolled origin
        ImVec2 origin = new ImVec2(canvasP0.x + scrolling.x, canvasP0.y + scrolling.y);

        // Mouse's position on canvas
        ImVec2 mousePosInCanvas = new ImVec2(io.getMousePos().x - origin.x, io.getMousePos().y - origin.y);

        // Add first and second point
        if (hovered && !addingLine && ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            ImVec2 startPos = new ImVec2(mousePosInCanvas.x + canvasP0.x, mousePosInCanvas.y + canvasP0.y);
            wireList.add(new Wire(startPos, startPos));
            wire.setStart(startPos.x, startPos.y);
            wire.setEnd(startPos.x, startPos.y);
            addingLine = true;
        }
        if (addingLine) {
            final int SIZE_WIRE_LIST = wireList.size();
            wire.setEnd(mousePosInCanvas.x + canvasP0.x, mousePosInCanvas.y + canvasP0.y);
            wireList.get(SIZE_WIRE_LIST - 1).setEnd(mousePosInCanvas.x + canvasP0.x, mousePosInCanvas.y + canvasP0.y);

            if (!ImGui.isMouseDown(ImGuiMouseButton.Left))
                addingLine = false;
        }

        // Pan (we use a zero mouse threshold when there's no context menu)
        // You may decide to make that threshold dynamic based on whether the mouse is hovering something etc.
        float mouseThresholdForPan = -1.0f;
        if (hovered && ImGui.isMouseDragging(ImGuiMouseButton.Middle, mouseThresholdForPan)) {
            scrolling.x += io.getMouseDelta().x;
            scrolling.y += io.getMouseDelta().y;
        }

        // Context menu (under default mouse threshold)
        ImVec2 dragDelta = ImGui.getMouseDragDelta(ImGuiMouseButton.Middle);
        if (dragDelta.x == 0.0f && dragDelta.y == 0.0f) {
            if (ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
                mayOpenPopupWindow = !mayOpenPopupWindow;
            }
        }

        // Draw grid
        drawList.pushClipRect(canvasP0.x, canvasP0.y, canvasP1.x, canvasP1.y, false);
        float GRID_STEP = 64.0f;
        for (float x = JMath.fmodf(scrolling.x, GRID_STEP); x < canvasSize.x; x += GRID_STEP) {
            drawList.addLine(canvasP0.x + x, canvasP0.y, canvasP0.x + x, canvasP1.y,
                    ImColor.intToColor(200, 200, 200, 40));
        }
        for (float y = JMath.fmodf(scrolling.y, GRID_STEP); y < canvasSize.y; y += GRID_STEP) {
            drawList.addLine(canvasP0.x, canvasP0.y + y, canvasP1.x, canvasP0.y + y,
                    ImColor.intToColor(200, 200, 200, 40));
        }

        // Draw animation boxes in SpriteWindowAnimation's canvas
        for (int i = 0; i < animationBoxList.size(); i++) {
            animationBoxList.get(i).imgui(origin, scrolling);
        }

        // Draw linked lines
        for (int i = 0; i < pointList.size(); i += 2) {
            if (pointList.size() <= 1) break;

            // Prevents from IndexOutOfBoundsException
            if (pointList.size() % 2 != 0 && i == pointList.size() - 1)
                continue;

            drawList.addLine(
                    pointList.get(i).position.x + origin.x,
                    pointList.get(i).position.y + origin.y,
                    pointList.get(i + 1).position.x + origin.x,
                    pointList.get(i + 1).position.y + origin.y,
                    ImColor.intToColor(255, 255, 0, 255), thickness);
        }

        // Draw a line when a point is looking for linking
        if (pointList.size() % 2 != 0) {
            drawList.addLine(wire.getStart().x + scrolling.x, wire.getStart().y + scrolling.y,
                    wire.getEnd().x + scrolling.x, wire.getEnd().y + scrolling.y,
                    ImColor.intToColor(255, 255, 0, 255), thickness);

            // Stop drawing the line
            if (ImGui.isMouseReleased(ImGuiMouseButton.Left)) {
                wire.setStart(0, 0);
                wire.setEnd(0, 0);
            }
        }

        drawArrows(drawList, origin);

        drawList.popClipRect();

        // Menu popup properties
        if (mayOpenPopupWindow) {
            if (ImGui.beginPopupContextWindow("context")) {
                addingLine = false;
                if (ImGui.menuItem("Add Animation Box", "")) {
                    animationBoxList.add(new AnimationBox("haha", mousePosInCanvas.x,
                            mousePosInCanvas.y));
                }
                // if (ImGui.menuItem("Remove one", "", false, pointList.size() > 0)) {
                // pointList.remove(pointList.size() - 1);
                // pointList.remove(pointList.size() - 1);
                // }
                ImGui.endPopup();
            }
        }

        ImGui.endChild();
    }

    public boolean isHovered() {
        return hovered;
    }

    public List<AnimationBox> getAnimationBoxList() {
        return animationBoxList;
    }

    public AnimationBox getActiveBox() {
        return activeAnimationBox;
    }

    public boolean isShowStateMachineChild() {
        return showStateMachineChild.get();
    }
}
