package sirius.editor.imgui.sprite_animation_window;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;
import sirius.utils.JMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Animator will also be called Canvas in the comments
public class Animator {
    // public static float zoom = 1.0f;
    public AnimationBlueprint animationBlueprint;

    public transient boolean showAnimator;

    public transient AnimationBox activeAnimationBox;


    private transient boolean hovered = false;
    private transient boolean addingLine = false;
    private transient boolean mayOpenPopupWindow = false;

    private transient ImVec2 scrolling;
    private transient float thickness = 2.0f;
    private transient Wire fakeWire;
    private transient boolean mayDrawFakeWire;
    private transient Wire bufferWire;

    public Animator() {
        this.showAnimator = true;

        this.fakeWire = new Wire();
        this.scrolling = new ImVec2();

        this.animationBlueprint = new AnimationBlueprint();
    }

    public Animator(AnimationBlueprint animationBlueprint) {
        this.showAnimator = true;

        this.fakeWire = new Wire();
        this.scrolling = new ImVec2();

        if (animationBlueprint != null) {
            this.animationBlueprint = new AnimationBlueprint(animationBlueprint.wireList, animationBlueprint.animationBoxList);
        } else {
            animationBlueprint = new AnimationBlueprint();
            this.animationBlueprint = new AnimationBlueprint();
        }

        // IDS points
        int greatestId = 0;
        for (Wire wire : animationBlueprint.wireList) {
            if (wire.getStartPoint().getId() > greatestId)
                greatestId = wire.getStartPoint().getId();

            if (wire.getEndPoint().getId() > greatestId)
                greatestId = wire.getEndPoint().getId();
        }

        Point.maxId = greatestId + 1;

        // IDs boxes
        greatestId = 0;
        for (AnimationBox animationBox : animationBlueprint.animationBoxList) {
            if (animationBox.getId() > greatestId) {
                greatestId = animationBox.getId();
            }
        }

        AnimationBox.maxId = greatestId + 1;
    }

    /**
     * Get an animation box based on its id.
     *
     * @param id Animation box's id.
     * @return The animation box with desired id.
     */
    public AnimationBox getAnimationBox(int id) {
        return this.animationBlueprint.animationBoxList.stream()
                .filter(animationBox -> animationBox.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Unselects all selected animation boxes.
     */
    public void unselectAllBoxes() {
        for (AnimationBox animationBox : getAnimationBoxList()) {
            if (animationBox.isSelected())
                animationBox.setSelected(false);
        }
    }

    /**
     * Removes an animation box based on its id.
     *
     * @param id Animation box's id.
     */
    public void delAnimationBox(int id) {
        unselectAllBoxes();

        AnimationBox queueRemoveBox = getAnimationBox(id);

        // Get all points that point fields have in the desired animation box to remove
        List<Point> pointsInFieldList = new ArrayList<>();
        for (PointField pointField : queueRemoveBox.getPointFields()) {
            pointsInFieldList.addAll(pointField.getPointList());
        }

        List<Wire> queueRemoveWireList = new ArrayList<>();

        // Remove points in point fields
        for (Point pointInField : pointsInFieldList) {
            animationBlueprint.wireList.stream()
                    .filter(wire -> wire.getEndPoint().getId() == pointInField.getId() || wire.getStartPoint().getId() == pointInField.getId())
                    .forEach(wire -> {

                        // Go throughout each animation box and check its points located in the point fields
                        for (AnimationBox animationBox : animationBlueprint.animationBoxList) {
                            if (animationBox == queueRemoveBox)
                                continue;

                            for (PointField pointField : animationBox.getPointFields()) {
                                for (int i = pointField.getPointList().size() - 1; i >= 0; i--) {
                                    Point p = pointField.getPointList().get(i);
                                    if (wire.getStartPoint().getId() == p.getId() || wire.getEndPoint().getId() == p.getId()) {
                                        queueRemoveWireList.add(wire);
                                        pointField.getPointList().remove(i);
                                    }
                                }
                            }
                        }
                    });

            for (int i = animationBlueprint.wireList.size() - 1; i >= 0; i--) {
                for (int j = 0; j < queueRemoveWireList.size(); j++) {
                    animationBlueprint.wireList.remove(queueRemoveWireList.get(j));
                }
            }
        }

        this.animationBlueprint.animationBoxList.remove(queueRemoveBox);
    }

    private void drawArrows(ImDrawList imDrawList, ImVec2 origin) {
        List<Wire> wireList = animationBlueprint.wireList;
        for (Wire wire : wireList) {

            // Get the middle point of the line
            ImVec2 pFrom = new ImVec2(wire.getStartX(), wire.getStartY());
            ImVec2 pTo   = new ImVec2(wire.getEndX(), wire.getEndY());

            // middlePoint = (xA + xB) / 2; (yA + yB) / 2
            float middleX = (pTo.x + pFrom.x) / 2;
            float middleY = (pTo.y + pFrom.y) / 2;

            Vector2f middleP = new Vector2f(middleX, middleY);

            Vector2f v1 = new Vector2f();
            Vector2f v3 = new Vector2f();

            // m --decline of the straight (y = mx + b)
            // m = (y2 - y1) / (x2 - x1)
            float m = (pFrom.y - pTo.y) / (pFrom.x - pTo.x);

            // m = tan(alpha) <=> alpha = tan-1(m)
            float angle = (float) Math.toDegrees(Math.atan(m));

            // Trade some coordinates between v1 and v3 --to draw the triangle as we want, always
            // pointing from pFrom to pTo
            if (pTo.x > pFrom.x) {
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

    private void createAndJoinPoints(ImVec2 origin) {
        ImDrawList drawList = ImGui.getWindowDrawList();

        // Create points to after join them and form a line, if the left mouse button isn't realised
        boolean clicked = ImGui.isMouseClicked(ImGuiMouseButton.Left);
        boolean released = ImGui.isMouseReleased(ImGuiMouseButton.Left);

        for (AnimationBox animationBox : getAnimationBoxList()) {
            if (animationBox.isMovingAnimationBox()) continue;

            for (PointField pointField : animationBox.getPointFields()) {
                if (pointField.isMouseAbove(origin) && !animationBox.isMouseAboveAnimationBox()) {
                    drawList.addCircleFilled(ImGui.getMousePosX(), ImGui.getMousePosY(),
                            6.0f, ImColor.intToColor(247, 179, 43, 150));

                    // Establish a union between 2 point --draw a line between 2 points
                    ImVec2 pointPos = new ImVec2(ImGui.getMousePosX() - origin.x, ImGui.getMousePosY() - origin.y);

                    if (clicked) {
                        Point newPoint = new Point(animationBox.getTrigger(), pointPos, 6.0f);
                        this.bufferWire = new Wire();
                        this.bufferWire.setStartPoint(new Point(newPoint));
                        pointField.addPoint(newPoint);

                        // Mark in which point field a possible unlinked point is
                        pointField.hasUnLinkedPoint = true;

                        // Give permission to draw the fake wire
                        this.mayDrawFakeWire = true;
                    }

                    if (released && bufferWire != null) {
                        Point newPoint = new Point(animationBox.getTrigger(), pointPos, 6.0f);
                        this.bufferWire.setEndPoint(new Point(newPoint));
                        pointField.addPoint(newPoint);

                        this.animationBlueprint.wireList.add(this.bufferWire);
                        this.bufferWire = null;

                        // Point fields are linked!
                        animationBlueprint.animationBoxList.forEach(animationBox1 ->
                                Arrays.stream(animationBox1.getPointFields()).forEach(pointField1 -> {
                                    if (pointField1.hasUnLinkedPoint) {
                                        pointField1.hasUnLinkedPoint = false;
                                    }
                                }));

                        // Mark check to see if there are 2 points in the same animation box
                        animationBox.checkPointsSameBox = true;
                    }
                }
            }
        }
    }

    // TODO: 27/05/2022 Finish this
    /*private void doJoke() {
        String jokeName = "I'm a box";
        for (int i = 0; i < animationBoxList.size(); i++) {
            if (animationBoxList.stream().anyMatch(animationBox -> animationBox.getTrigger().equals("I'm a box!"))) {
                jokeName = "I'm a box too!";
                break;
            } else if (animationBoxList.stream().anyMatch(animationBox -> animationBox.getTrigger().equals("I'm a box too!"))) {

            }
        }

        animationBoxList.add(new AnimationBox(jokeName, mousePosInCanvas.x,
                mousePosInCanvas.y));
    }*/

    public void imgui(ImVec2 contentRegionAvailable) {
        activeAnimationBox = this.animationBlueprint.animationBoxList
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
        // zoom += io.getMouseWheel() * dt;
        ImDrawList drawList = ImGui.getWindowDrawList();

        // Check if the cursor is above of sprite animation window
        hovered = ImGui.isWindowHovered();

        // Lock scrolled origin
        ImVec2 origin = new ImVec2(canvasP0.x + scrolling.x, canvasP0.y + scrolling.y);

        // Mouse's position on canvas
        ImVec2 mousePosInCanvas = new ImVec2(io.getMousePos().x - origin.x, io.getMousePos().y - origin.y);

        createAndJoinPoints(origin);

        // Add first and second point
        if (!addingLine && mayDrawFakeWire) {
            ImVec2 startPos = new ImVec2(mousePosInCanvas.x, mousePosInCanvas.y);
            fakeWire.setStart(startPos.x + canvasP0.x, startPos.y + canvasP0.y);
            fakeWire.setEnd(startPos.x + canvasP0.x, startPos.y + canvasP0.y);

            addingLine = true;
        }
        if (addingLine) {
            fakeWire.setEnd(mousePosInCanvas.x + canvasP0.x, mousePosInCanvas.y + canvasP0.y);

            if (!ImGui.isMouseDown(ImGuiMouseButton.Left)) {
                mayDrawFakeWire = false;
                addingLine = false;
            }
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

        // for (float x = JMath.fmodf(scrolling.x, GRID_STEP); x < canvasSize.x; x += GRID_STEP * zoom) {
        for (float x = JMath.fmodf(scrolling.x, GRID_STEP); x < canvasSize.x; x += GRID_STEP) {
            drawList.addLine(
                    canvasP0.x + x, canvasP0.y,
                    canvasP0.x + x, canvasP1.y,
                    ImColor.intToColor(200, 200, 200, 40));
        }
        // for (float y = JMath.fmodf(scrolling.y, GRID_STEP); y < canvasSize.y; y += GRID_STEP * zoom) {
        for (float y = JMath.fmodf(scrolling.y, GRID_STEP); y < canvasSize.y; y += GRID_STEP) {
            drawList.addLine(
                    canvasP0.x, canvasP0.y + y,
                    canvasP1.x, canvasP0.y + y,
                    ImColor.intToColor(200, 200, 200, 40));
        }

        // Set which animation box the animation should start
        if (getAnimationBoxList().size() == 1) {
            getAnimationBoxList().get(0).setFlag(true);
        }

        // Draw animation boxes in SpriteWindowAnimation's canvas
        for (int i = 0; i < animationBlueprint.animationBoxList.size(); i++) {
            animationBlueprint.animationBoxList.get(i).imgui(origin, scrolling);
        }

        // Draw linked lines
        for (Wire wire : animationBlueprint.wireList) {
            drawList.addLine(
                    wire.getStartX() + origin.x,
                    wire.getStartY() + origin.y,
                    wire.getEndX() + origin.x,
                    wire.getEndY() + origin.y,
                    ImColor.intToColor(255, 255, 0, 255), thickness);
        }

        // Draw a line when a point is looking for linking
        drawList.addLine(
                fakeWire.getStartX() + scrolling.x,
                fakeWire.getStartY() + scrolling.y,
                fakeWire.getEndX() + scrolling.x,
                fakeWire.getEndY() + scrolling.y,
                ImColor.intToColor(255, 255, 0, 255), thickness);

        // Stop drawing the line
        if (ImGui.isMouseReleased(ImGuiMouseButton.Left)) {
            fakeWire.setStart(0, 0);
            fakeWire.setEnd(0, 0);
        }

        drawArrows(drawList, origin);

        // Draw the points
        animationBlueprint.animationBoxList
                .forEach(animationBox -> Arrays.stream(animationBox.getPointFields())
                        .forEach(pointField -> pointField.getPointList().forEach(point -> point.imgui(origin))));

        drawList.popClipRect();

        // Menu popup properties
        if (mayOpenPopupWindow) {
            if (ImGui.beginPopupContextWindow("context")) {
                addingLine = false;
                if (ImGui.menuItem("Add Animation Box", "")) {
                    animationBlueprint.animationBoxList.add(new AnimationBox("I'm a box!", mousePosInCanvas));
                }
                ImGui.endPopup();
            }
        }

        ImGui.endChild();
    }

    public boolean isHovered() {
        return hovered;
    }

    public List<AnimationBox> getAnimationBoxList() {
        return animationBlueprint.animationBoxList;
    }

    public AnimationBox getActiveBox() {
        return activeAnimationBox;
    }
}
