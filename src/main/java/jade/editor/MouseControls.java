package jade.editor;

import gameobjects.GameObject;
import gameobjects.components.Component;
import gameobjects.components.SpriteRenderer;
import jade.Window;
import jade.animations.StateMachine;
import jade.input.KeyListener;
import jade.input.MouseListener;
import jade.rendering.Color;
import jade.rendering.PickingTexture;
import jade.rendering.debug.DebugDraw;
import jade.scenes.Scene;
import jade.utils.Settings;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    GameObject holdingGameObject = null;
    private float debounceTime = 0.2f;
    private float debounce = debounceTime;

    // To tell us if we are dragging
    private boolean boxSelectSet;

    private Vector2f boxSelectStart = new Vector2f();
    private Vector2f boxSelectEnd   = new Vector2f();

    public void pickupObject(GameObject go) {
        if (holdingGameObject != null) holdingGameObject.destroy();

        holdingGameObject = go;
        holdingGameObject.getComponent(SpriteRenderer.class).setColor(new Color(0.8f, 0.8f, 0.8f, 0.8f));
        this.holdingGameObject.addComponent(new NonPickable());
        Window.getCurrentScene().addGameObject(go);
    }

    public void place() {
        GameObject newObj = this.holdingGameObject.copy();

        if (newObj.hasComponent(StateMachine.class)) newObj.getComponent(StateMachine.class).refreshTextures();

        newObj.getComponent(SpriteRenderer.class).setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        newObj.removeComponent(NonPickable.class);
        Window.getCurrentScene().addGameObject(newObj);
    }

    private boolean isBlockInSquare(float x, float y) {
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        Vector2f start        = new Vector2f(x, y);
        Vector2f end          = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf   = MouseListener.worldToScreen(end);

        Vector2i startScreen = new Vector2i((int) startScreenf.x + 2, (int) startScreenf.y + 2); // +2 pixels, to ensure that we don't pick objects in a different square
        Vector2i endScreen = new Vector2i((int) endScreenf.x - 2, (int) endScreenf.y - 2);
        float[] gameObjectsIds = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < gameObjectsIds.length; i++) {
            if (gameObjectsIds[i] >= 0) {
                GameObject pickedObj = Window.getCurrentScene().getGameObject((int) gameObjectsIds[i]);
                if (!pickedObj.hasComponent(NonPickable.class)) return true;
            }
        }

        return false;
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        if (holdingGameObject != null) {
            holdingGameObject.transform.position.x = MouseListener.getWorld().x;
            holdingGameObject.transform.position.y = MouseListener.getWorld().y;

            holdingGameObject.transform.position.x = ((int) Math.floor(holdingGameObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f;
            holdingGameObject.transform.position.y = ((int) Math.floor(holdingGameObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f;

            if (MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth  = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;
                boolean isntBlockInSquare = !isBlockInSquare(holdingGameObject.transform.position.x - halfWidth,
                        holdingGameObject.transform.position.y - halfHeight);

                // Fix duplicated game object placement bug
                if (MouseListener.isDragging() && isntBlockInSquare) {
                    place();
                } else if (!MouseListener.isDragging() && isntBlockInSquare) {
                    place();
                    debounce = debounceTime;
                }
            }

            if (KeyListener.isKeyDown(GLFW_KEY_ESCAPE) || KeyListener.isKeyDown(GLFW_KEY_DELETE)) {
                holdingGameObject.destroy();
                holdingGameObject = null;
            }

        } else if (!MouseListener.isDragging() && MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            PickingTexture pickingTexture = Window.getImGuiLayer().getPropertiesWindow().getPickingTexture();
            Scene currentScene = Window.getCurrentScene();
            PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();

            int x = (int) MouseListener.getScreenX();
            int y = (int) MouseListener.getScreenY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && !pickedObj.hasComponent(NonPickable.class)) {
                propertiesWindow.setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()) {
                propertiesWindow.clearSelected();
            }
            this.debounce = 0.2f;

            // If we are dragging and pressing the mouse's left button
        } else if (MouseListener.isDragging() && MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
            // If we aren't dragging
            if (!boxSelectSet) {
                Window.getImGuiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }

            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screenToWorld(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToWorld(boxSelectEnd);
            Vector2f halfSize = (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D((new Vector2f(boxSelectStartWorld)).add(halfSize), new Vector2f(halfSize).mul(2.0f), 0.0f);

        } else if (boxSelectSet) {
            PickingTexture pickingTexture = Window.getImGuiLayer().getPropertiesWindow().getPickingTexture();
            boxSelectSet = false;
            int screenStartX = (int) boxSelectStart.x;
            int screenStartY = (int) boxSelectStart.y;
            int screenEndX   = (int) boxSelectEnd.x;
            int screenEndY   = (int) boxSelectEnd.y;

            boxSelectStart.zero();
            boxSelectEnd.zero();

            // Swap values
            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] gameObjectsId = pickingTexture.readPixels(screenStartX, screenStartY, screenEndX, screenEndY);

            // Using Set<> to prevent us from adding an id twice
            Set<Integer> uniqueGameObjectIds = new HashSet<>();
            for (float objId : gameObjectsId) {
                uniqueGameObjectIds.add((int) objId);
            }

            for (Integer gameObjectId : uniqueGameObjectIds) {
                GameObject pickedObj = Window.getCurrentScene().getGameObject(gameObjectId);
                if (pickedObj != null && !pickedObj.hasComponent(NonPickable.class))
                    // Means that we can't pick it
                    Window.getImGuiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
            }
        }
    }
}
