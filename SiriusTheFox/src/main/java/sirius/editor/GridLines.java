package sirius.editor;

import gameobjects.components.Component;
import sirius.SiriusTheFox;
import sirius.rendering.Camera;
import sirius.rendering.color.Color;
import sirius.rendering.debug.DebugDraw;
import sirius.utils.Settings;
import org.joml.Vector2f;

public class GridLines extends Component {

    @Override
    public void editorUpdate(float dt) {
        EditorCamera editorCamera = gameObject.getComponent(EditorCamera.class);
        if (editorCamera.getLevelEditorCamera().getZoom() > 3.8f) {
            return;
        }

        Camera camera = SiriusTheFox.getCurrentScene().getCamera();

        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        // Snap to Grid
        final float firstX = ((int) (cameraPos.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_WIDTH; // -1 to move back one grid space
        final float firstY = ((int) (cameraPos.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        // Number of vertical and horizontal lines
        // int numVerticalLines = (int) (Window.getWidth() / projectionSize.x);
        final int numVerticalLines = (int) ((projectionSize.x * camera.getZoom() + 1) / Settings.GRID_WIDTH); // +1 to move forward one grid space
        final int numHorizontalLines = (int) ((projectionSize.y * camera.getZoom() + 1) / Settings.GRID_HEIGHT);

        final float WIDTH = (int) (projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH * 5;
        final float HEIGHT = (int) (projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT * 5;

        // final int MAX_LINES = numVerticalLines > numHorizontalLines ? numVerticalLines : numHorizontalLines;
        final int MAX_LINES = Math.max(numVerticalLines, numHorizontalLines);
        final Color color = new Color(0.2f, 0.2f, 0.2f, 1.0f);
        for (int i = 0; i < MAX_LINES; i++) {
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numVerticalLines)
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + HEIGHT), color);

            if (i < numHorizontalLines)
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + WIDTH, y), color);
        }
    }
}
