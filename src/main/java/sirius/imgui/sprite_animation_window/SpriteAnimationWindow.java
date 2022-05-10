package sirius.imgui.sprite_animation_window;

import imgui.*;
import imgui.flag.*;
import imgui.type.ImBoolean;

import java.util.ArrayList;
import java.util.List;

public class SpriteAnimationWindow {
    public static List<Point> pointList;
    private List<AnimationBox> animationBoxList;
    private List<Wire> wireList;
    private Wire wire;

    private ImVec2 scrolling;

    private boolean addingLine = false;
    private float thickness = 2.0f;

    private boolean mayOpenPopupWindow = false;

    public SpriteAnimationWindow() {
        this.wire = new Wire();
        pointList = new ArrayList<>();
        this.animationBoxList = new ArrayList<>();
        this.wireList = new ArrayList<>();
        this.scrolling = new ImVec2();
        animationBoxList.add(new AnimationBox("I'm a box", 300, 300));
    }

    /**
     * Computes the floating-point remainder of a / b.
     *
     * @param a float
     * @param b float
     * @return computed floating-point remainder of a / b
     */
    private float fmodf(float a, float b) {
        int result = (int) Math.floor(a / b);
        return a - result * b;
    }

    public void imgui() {
        if (ImGui.begin("Sprite Animation Window", new ImBoolean(true))) {
            ImGui.text("Mouse Left: drag to add lines,\nMouse Middle: drag to scroll,\nMouse Right: click for context menu.");

            // Using InvisibleButton() as a convenience 1) it will advance the layout cursor and 2) allows us to use IsItemHovered()/IsItemActive()
            ImVec2 canvasP0 = ImGui.getCursorScreenPos();      // ImDrawList API uses screen coordinates!
            ImVec2 canvasSize = ImGui.getContentRegionAvail();   // Resize canvas to what's available

            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
            ImGui.pushStyleColor(ImGuiCol.ChildBg, ImColor.intToColor(50, 50, 50, 255));
            // We will catch all the interactions on this window
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

            boolean isHovered = ImGui.isWindowHovered();

            ImVec2 origin = new ImVec2(canvasP0.x + scrolling.x, canvasP0.y + scrolling.y); // Lock scrolled origin

            ImVec2 mousePosInCanvas = new ImVec2(io.getMousePos().x - origin.x, io.getMousePos().y - origin.y);

            // Add first and second point
            if (isHovered && !addingLine && ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
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
            if (isHovered && ImGui.isMouseDragging(ImGuiMouseButton.Middle, mouseThresholdForPan)) {
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
            for (float x = fmodf(scrolling.x, GRID_STEP); x < canvasSize.x; x += GRID_STEP) {
                drawList.addLine(canvasP0.x + x, canvasP0.y, canvasP0.x + x, canvasP1.y,
                        ImColor.intToColor(200, 200, 200, 40));
            }
            for (float y = fmodf(scrolling.y, GRID_STEP); y < canvasSize.y; y += GRID_STEP) {
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
        ImGui.end();
    }
}
