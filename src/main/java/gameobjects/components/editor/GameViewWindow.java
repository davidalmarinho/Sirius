package gameobjects.components.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.Window;
import jade.input.MouseListener;
import org.joml.Vector2f;

public class GameViewWindow {
    private float leftX, rightX, topY, bottomY;

    public void imgui() {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

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

        int textureID = Window.getFramebuffer().getTextureID();
        ImGui.image(textureID, windowSize.x, windowSize.y, 0, 1, 1, 0);

        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

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

        // For safe
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillar box mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        // For safe
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        // Taking the center and subtracting half size of the small rectangle that we can fit inside
        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
        // ImGui.getCursorPosX() to count with the title bar
    }

    public boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX
                && MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }
}
