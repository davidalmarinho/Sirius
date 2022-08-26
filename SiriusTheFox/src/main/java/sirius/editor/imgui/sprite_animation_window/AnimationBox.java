package sirius.editor.imgui.sprite_animation_window;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import sirius.animations.Frame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnimationBox {
    public static int maxId = 0;

    private final int ID;
    private String title;
    public float x, y;
    private float width;
    private transient float height, lastWidth;

    private List<Frame> frameList;
    public boolean doesLoop = false;
    private boolean flag;

    private transient boolean mouseAboveAnimationBox;
    private transient boolean movingAnimationBox;

    private PointField[] pointFields;
    private transient boolean updatePointFields;
    private transient boolean mayCheckForUncheckedPoints;
    public transient boolean checkPointsSameBox;

    private transient final float THICKNESS = 10.0f;
    private transient final float ROUNDING  = 20.0f;

    private transient boolean selected;

    private transient boolean mayOpenPopupWindow;

    public AnimationBox(int id, String title, float x, float y, float width) {
        this.ID = id;
        this.frameList = new ArrayList<>();
        this.title     = title;
        this.x         = x;
        this.y         = y;

        this.width       = width;
        this.height      = 128.0f;
        this.lastWidth   = this.width;

        this.pointFields = new PointField[4];
        setPointFields();
    }

    public AnimationBox(String title, float x, float y) {
        this(maxId, title, x, y, 128.0f);
        maxId++;
    }

    public AnimationBox(String title, ImVec2 position) {
        this(title, position.x, position.y);
        maxId++;
    }

    public AnimationBox(AnimationBox newAnimationBox) {
        this(newAnimationBox.ID, newAnimationBox.title, newAnimationBox.x, newAnimationBox.y, newAnimationBox.width);
        for (Frame frame : newAnimationBox.getFrameList()) {
            frameList.add(new Frame(frame));
        }
        this.flag        = newAnimationBox.flag;
        this.doesLoop    = newAnimationBox.doesLoop;
        this.pointFields = newAnimationBox.pointFields;
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
            this.pointFields[0] = new PointField("up", x, yPointField0, widthPointField0, THICKNESS);
            // Right
            this.pointFields[1] = new PointField("right", xPointField1, y, THICKNESS, heightPointField1);
            // Down
            this.pointFields[2] = new PointField("down", x, yPointField2, widthPointField2, THICKNESS);
            // Left
            this.pointFields[3] = new PointField("left", xPointField3, y, THICKNESS, heightPointField3);
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

    private void delUnlinkedPoints() {
        if (mayCheckForUncheckedPoints) {
            //List<Point> pointList = SpriteAnimationWindow.getAnimator().animationBlueprint.pointList;

            // If is an unlinked point, remove it
            for (PointField pointField : pointFields) {
                if (pointField.hasUnLinkedPoint) {
                    // Remove the last points from the lists
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

    /**
     * Checks if a point has the same id of a point part of a point field.
     *
     * @param point Point that we want to check to see if it has the same id of a point part of a point field.
     * @return true if the point has the same id of a point part of a point field.
     */
    private boolean isSameId(Point point) {
        for (PointField pointField : pointFields) {
            if (isSameId(pointField.getPointList(), point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a point has the same id of a point part of a points' list.
     *
     * @param pointList List that we will check if it has a point with the same id has the desired point.
     * @param point Desired point that we want to check to see if it has the same id of a point part of a point field.
     * @return true if the point has the same id of a point part of a point field.
     */
    private boolean isSameId(List<Point> pointList, Point point) {
        return pointList.stream().anyMatch(point1 -> point.getId() == point1.getId());
    }

    /**
     * To maintain the state machine integrity, 2 points can't be linked in the same animation box,
     * because we don't want go, for example, from idle_state to idle_state.
     */
    private void delPointsSameBox() {
        if (!checkPointsSameBox) return;
        checkPointsSameBox = false;

        // Get the 2 last points added to rendering
        Wire lastWire = SpriteAnimationWindow.getAnimator().animationBlueprint.getLastWire();

        if (lastWire == null) return;

        // If samePoint var reaches 2, we will have to delete the 2 lastPoints added, because
        // that means that the 2 last added points are located in the same animation box.
        int samePoint = 0;
        if (isSameId(lastWire.getStartPoint()))
            samePoint++;
        if (isSameId(lastWire.getEndPoint()))
            samePoint++;

        if (samePoint == 2) {
            // Search for where are the 2 last points added located, and erase them.
            for (PointField pointField : pointFields) {
                if (isSameId(pointField.getPointList(), lastWire.getEndPoint()))
                    pointField.removeLastPoint();

                if (isSameId(pointField.getPointList(), lastWire.getStartPoint()))
                    pointField.removeLastPoint();
            }

            // Remove the 2 last points / last wire added to the rendering
            SpriteAnimationWindow.getAnimator().animationBlueprint.removeLastWire();
        }
    }

    /**
     * Instructions to move the box when we drag the mouse.
     */
    private void moveBox(ImVec2 origin) {
        if (mouseAboveAnimationBox && ImGui.isMouseDragging(ImGuiMouseButton.Left))
            movingAnimationBox = true;

        if (movingAnimationBox) {
            this.x = ImGui.getMousePosX() - origin.x;
            this.y = ImGui.getMousePosY() - origin.y;
            List<ImVec2> deltaPoints = new ArrayList<>();

            // Calculate how many x and y units that a point is from its position and from the point field
            // that it is located
            for (PointField pointField : pointFields) {
                for (Point point : pointField.getPointList()) {
                    float pointX = point.position.x;
                    float pointY = point.position.y;
                    float deltaPointX = pointX - pointField.getPosition().x;
                    float deltaPointY = pointY - pointField.getPosition().y;
                    deltaPoints.add(new ImVec2(deltaPointX, deltaPointY));
                }
            }

            // Set point fields' positions
            setPointFields();

            // Replace the points positions with the correct position after the animation box was moved
            int counter = 0;
            for (PointField pointField : pointFields) {
                for (Point point : pointField.getPointList()) {
                    point.position.set(pointField
                            .getPosition().x + deltaPoints.get(counter).x, pointField.getPosition().y + deltaPoints.get(counter).y);
                    counter++;
                }
            }

            // Load the new points positions to the drawing list in Sprite Animation Window
            for (PointField pointField : pointFields) {
                for (Point p : pointField.getPointList()) {
                    for (Wire wire : SpriteAnimationWindow.getAnimator().animationBlueprint.wireList) {
                        if (wire.getEndPoint().getId() == p.getId()) {
                            wire.getEndPoint().position.set(new ImVec2(p.position));
                        } else if (wire.getStartPoint().getId() == p.getId()) {
                            wire.getStartPoint().position.set(new ImVec2(p.position));
                        }
                    }
                }
            }

            // If we aren't dragging the mouse, we can't be moving the window
            if (!ImGui.isMouseDragging(ImGuiMouseButton.Left))
                movingAnimationBox = false;
        }
    }

    private void popupMenu() {
        if (mayOpenPopupWindow) {
            if (ImGui.beginPopupContextWindow("popup_animation_box_ctx")) {
                // TODO: 10/06/2022 Give info with ? imgui
                if (ImGui.menuItem("Set flag")) {
                    for (AnimationBox animationBox : SpriteAnimationWindow.getAnimator().getAnimationBoxList()) {
                        animationBox.flag = false;
                    }
                    this.flag = true;
                }
                if (ImGui.menuItem("Delete current animation box", "del")) {
                    SpriteAnimationWindow.getAnimator().delAnimationBox(ID);
                }
                ImGui.endPopup();
            }
        }
    }

    private void drawAnimationBox() {
        ImDrawList drawList = ImGui.getWindowDrawList();
        ImVec2 origin       = SpriteAnimationWindow.getAnimator().getOrigin();
        ImVec2 scrolling    = SpriteAnimationWindow.getAnimator().getScrolling();

        // Don't want boundaries inside imgui child
        int boundariesColor;
        if (!selected) {
            boundariesColor = ImColor.intToColor(255, 255, 255, 255);
        } else {
            boundariesColor = ImColor.intToColor(200, 200, 200, 255);
        }

        drawList.addRect(
                origin.x + x - getWidth() / 2,
                origin.y + y - getHeight() / 2,
                origin.x + x + getWidth() / 2,
                origin.y + y + getHeight() / 2,
                boundariesColor, ROUNDING, ImDrawFlags.RoundCornersAll, THICKNESS);

        // Reserve the region to draw the animation box
        ImGui.setCursorPos(x - getWidth() / 2 + scrolling.x, + y - getHeight() / 2 + scrolling.y);
        ImGui.beginChild("box" + ID, width, height, false, ImGuiWindowFlags.AlwaysAutoResize
                | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoScrollbar);

        if (selected && ImGui.isMouseClicked(ImGuiMouseButton.Right)) {
            mayOpenPopupWindow = !mayOpenPopupWindow;
        }

        // Menu popup properties
        popupMenu();

        // Check what size the animation box will have --it changes depending on how many characters we have in text field
        final float BREAKER_WIDTH = 48f;
        float val = 11.8f;
        int charsNumber = this.title.length();
        float currentSize = (charsNumber + 1) * val; // +1 to maintain the integrity of this logic
        float maxSize = 20.8f * val;

        // Changes the animation box size
        if (charsNumber < 10)
            this.width = val * 10 + BREAKER_WIDTH;
        else
            this.width = Math.min(currentSize + BREAKER_WIDTH, maxSize + BREAKER_WIDTH);

        // ImDrawList drawList = ImGui.getWindowDrawList();
        // drawList.addRectFilled(
        //         origin.x + x - getWidth() / 2 * Animator.zoom,
        //         origin.y + y - getHeight() / 2 * Animator.zoom,
        //         origin.x + x + getWidth() / 2 * Animator.zoom,
        //         origin.y + y + getHeight() / 2 * Animator.zoom,
        //         ImColor.intToColor(112, 16, 20, 255), ROUNDING);

        int boxColor = ImColor.intToColor(112, 16, 20, 255);
        if (flag)
            boxColor = ImColor.intToColor(124,252,0, 255);

        drawList.addRectFilled(
                origin.x + x - getWidth() / 2,
                origin.y + y - getHeight() / 2,
                origin.x + x + getWidth() / 2,
                origin.y + y + getHeight() / 2,
                boxColor, ROUNDING);

        // Center the animation box input text box
        ImGui.setCursorPos(ImGui.getCursorPosX() + 22, ImGui.getCursorPosY() + getHeight() / 3);
        ImGui.pushID("nodeTrigger: " + ID);

        // Changes the animation box input text box size
        if (charsNumber < 10) {
            ImGui.setNextItemWidth(val * 10);
        } else {
            ImGui.setNextItemWidth(Math.min(currentSize, maxSize));
        }

        this.title = inputText(this.title);

        // Change points' origin variable if the title is changed
        Arrays.stream(pointFields)
                .forEach(pointField -> pointField.getPointList()
                        .forEach(point -> {
                            if (!point.getOrigin().equals(this.title)) {
                                point.setOrigin(this.title);

                                for (Wire wire : SpriteAnimationWindow.getAnimator().animationBlueprint.wireList) {
                                    if (wire.getStartPoint().getId() == point.getId()) {
                                        wire.getStartPoint().setOrigin(this.title);
                                    }

                                    if (wire.getEndPoint().getId() == point.getId()) {
                                        wire.getEndPoint().setOrigin(this.title);
                                    }
                                }
                            }
                        }));

        // System.out.println("MinMax zoom: " + Animator.zoom); // 0.79

        // Select the box if the text input field was activated
        if (ImGui.isItemActivated())
            selected = true;

        ImGui.popID();

        // Checks if the mouse is above of the AnimationBox
        mouseAboveAnimationBox = ImGui.isWindowHovered();

        // Box is selected if the mouse left is pressed and if the mouse is inside the box
        if (mouseAboveAnimationBox && ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            // If there are any animation boxes selected, we will want them to become unselected
            SpriteAnimationWindow.getAnimator().unselectAllBoxes();

            selected = true;
            // Box can't be unselected if the mouse left isn't inside the animator
        } else if (SpriteAnimationWindow.getAnimator().isHovered()
                && !mouseAboveAnimationBox && ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            selected = false;
        }

        moveBox(origin);

        ImGui.endChild();
    }

    public void imgui() {
        // Draw the outlines of the animation box

        // if (!selected) {
        //     drawList.addRect(
        //             origin.x + x - getWidth() / 2 * Animator.zoom,
        //             origin.y + y - getHeight() / 2 * Animator.zoom,
        //             origin.x + x + getWidth() / 2 * Animator.zoom,
        //             origin.y + y + getHeight() / 2 * Animator.zoom,
        //             ImColor.intToColor(255, 255, 255, 255), ROUNDING, ImDrawFlags.RoundCornersAll, THICKNESS);
        // } else {
        //     drawList.addRect(
        //             origin.x + x - getWidth() / 2 * Animator.zoom,
        //             origin.y + y - getHeight() / 2 * Animator.zoom,
        //             origin.x + x + getWidth() / 2 * Animator.zoom,
        //             origin.y + y + getHeight() / 2 * Animator.zoom,
        //             ImColor.intToColor(200, 200, 200, 255), ROUNDING, ImDrawFlags.RoundCornersAll, THICKNESS);
        // }

        drawAnimationBox();

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

            List<Wire> wireList = SpriteAnimationWindow.getAnimator().animationBlueprint.wireList;

            // Update points --move them because the animation box has been resized
            for (PointField pointField : pointFields) {
                for (Point point : pointField.getPointList()) {
                    float moveValue = dtWidth / 2;

                    // Move left
                    if (point.position.x < this.x) {
                        point.position.x -= moveValue;
                        for (Wire wire : wireList) {
                            if (wire.getStartPoint().getId() == point.getId()) {
                                wire.getStartPoint().position.x -= moveValue;
                            } else if (wire.getEndPoint().getId() == point.getId()) {
                                wire.getEndPoint().position.x -= moveValue;
                            }
                        }

                    // Move right
                    } else {
                        point.position.x += moveValue;
                        for (Wire wire : wireList) {
                            if (wire.getStartPoint().getId() == point.getId()) {
                                wire.getStartPoint().position.x += moveValue;
                            } else if (wire.getEndPoint().getId() == point.getId()) {
                                wire.getEndPoint().position.x += moveValue;
                            }
                        }
                    }
                }
            }
        }

        delUnlinkedPoints();
        delPointsSameBox();
    }

    private String inputText(String text) {
        ImString outString = new ImString(text, 32);

        if (ImGui.inputText("", outString, ImGuiInputTextFlags.AutoSelectAll)) {
            return outString.get();
        }

        return text;
    }

    public boolean isMovingAnimationBox() {
        return movingAnimationBox;
    }

    public boolean isMouseAboveAnimationBox() {
        return mouseAboveAnimationBox;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return ID;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public PointField[] getPointFields() {
        return pointFields;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public List<Frame> getFrameList() {
        return frameList;
    }

    public Frame getFrame(int index) {
        return frameList.get(index);
    }

    public void setFrame(int index, Frame newFrame) {
        frameList.set(index, newFrame);
    }

    public int getFrameListSize() {
        return frameList.size();
    }

    public void addFrame(Frame frame) {
        this.frameList.add(frame);
    }

    public void removeFrame(Frame frame) {
        this.frameList.remove(frame);
    }

    public void removeFrame(int index) {
        this.frameList.remove(index);
    }

    public void clearFrameList() {
        this.frameList.clear();
    }
}
