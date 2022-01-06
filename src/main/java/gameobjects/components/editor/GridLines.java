package gameobjects.components.editor;

import gameobjects.components.Component;
import jade.Window;
import jade.rendering.Camera;
import jade.rendering.Color;
import jade.rendering.debug.DebugDraw;
import jade.utils.Settings;
import org.joml.Vector2f;

public class GridLines extends Component {


    @Override
    public void update(float dt) {
        Camera camera = Window.getCurrentScene().getCamera();

        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        // Snap to Grid
        final int firstX = ((int) (cameraPos.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_WIDTH; // -1 to move back 1 grid space
        final int firstY = ((int) (cameraPos.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        // Number of vertical and horizontal lines
        // int numVerticalLines = (int) (Window.getWidth() / projectionSize.x);
        final int numVerticalLines = (int) (projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        final int numHorizontalLines = (int) (projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        final int WIDTH = (int) (projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH * 2;
        final int HEIGHT = (int) (projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT * 2;

        // final int MAX_LINES = numVerticalLines > numHorizontalLines ? numVerticalLines : numHorizontalLines;
        final int MAX_LINES = Math.max(numVerticalLines, numHorizontalLines);
        final Color color = new Color(0.2f, 0.2f, 0.2f);
        for (int i = 0; i < MAX_LINES; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i);
            int y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numVerticalLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + HEIGHT), color);
            }

            if (i < numHorizontalLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + WIDTH, y), color);
            }
        }
    }
}
