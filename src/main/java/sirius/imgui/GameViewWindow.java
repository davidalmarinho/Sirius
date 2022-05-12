package sirius.imgui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import sirius.SiriusTheFox;
import sirius.input.MouseListener;
import observers.EventSystem;
import observers.events.Events;
import observers.events.Event;
import org.joml.Vector2f;

public class GameViewWindow {
    private float leftX, rightX, topY, bottomY;
    private boolean playing;
    private boolean collapsed;

    public void imgui() {
        ImGui.begin("Game Viewport",
                ImGuiWindowFlags.NoScrollbar
                        | ImGuiWindowFlags.NoScrollWithMouse
                        | ImGuiWindowFlags.MenuBar);

        // Play and Stop engine buttons
        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "", playing, !playing)) {
            playing = true;
            EventSystem.notify(null, new Event(Events.GAME_ENGINE_START_PLAY));
        }

        if (ImGui.menuItem("Stop", "", !playing, playing)) {
            playing = false;
            EventSystem.notify(null, new Event(Events.GAME_ENGINE_STOP_PLAY));
        }
        ImGui.endMenuBar();

        // Size of the game viewport
        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        // Gets viewport size
        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

        int textureID = SiriusTheFox.getWindow().getFramebuffer().getTextureID();
        ImGui.image(textureID, windowSize.x, windowSize.y, 0, 1, 1, 0);

        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        this.collapsed = !ImGui.isWindowHovered();

        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport() {
        /*
         *      Required to know:
         *          ratio = width / height
         *          width = ratio / height
         *          height = width / ratio
         */
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / SiriusTheFox.getWindow().getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillar box mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * SiriusTheFox.getWindow().getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        // Taking the center and subtracting half size of the small rectangle that we can fit inside
        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
        // ImGui.getCursorPosX() to count with the title bar
    }

    public boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX
                && MouseListener.getY() >= bottomY && MouseListener.getY() <= topY
                && !collapsed;
    }

    public float getLeftX() {
        return leftX;
    }

    public float getRightX() {
        return rightX;
    }

    public float getTopY() {
        return topY;
    }

    public float getBottomY() {
        return bottomY;
    }
}
