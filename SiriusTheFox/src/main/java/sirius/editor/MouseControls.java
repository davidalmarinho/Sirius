package sirius.editor;

import gameobjects.GameObject;
import gameobjects.components.Component;
import gameobjects.components.SpriteRenderer;
import gameobjects.components.text_components.TextBox;
import gameobjects.components.Transform;
import sirius.SiriusTheFox;
import sirius.animations.StateMachine;
import sirius.editor.imgui.Prefabs;
import sirius.editor.imgui.PropertiesWindow;
import sirius.editor.imgui.tool_window.ToolWindow;
import sirius.input.KeyListener;
import sirius.input.MouseListener;
import sirius.rendering.color.Color;
import sirius.rendering.PickingTexture;
import sirius.rendering.debug.DebugDraw;
import sirius.scenes.Scene;
import sirius.utils.Settings;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component {
    public static boolean allComponentsHaveSameType;

    private GameObject holdingGameObject = null;
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
        this.holdingGameObject.setZIndex(Integer.MIN_VALUE);
        SiriusTheFox.getCurrentScene().addGameObject(holdingGameObject);
    }

    public void place() {
        GameObject newObj = this.holdingGameObject.copy();

        if (newObj.hasComponent(StateMachine.class)) newObj.getComponent(StateMachine.class).refreshTextures();

        newObj.getComponent(SpriteRenderer.class).setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
        newObj.removeComponent(NonPickable.class);
        newObj.setZIndex(0);

        SiriusTheFox.getCurrentScene().addGameObject(newObj);
    }

    private boolean isBlockInSquare(float x, float y) {
        PropertiesWindow propertiesWindow = SiriusTheFox.getImGuiLayer().getPropertiesWindow();
        Vector2f start        = new Vector2f(x, y);
        Vector2f end          = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.worldToGameViewport(start);
        Vector2f endScreenf   = MouseListener.worldToGameViewport(end);

        Vector2i startScreen = new Vector2i((int) startScreenf.x + 2, (int) startScreenf.y + 2); // +2 pixels, to ensure that we don't pick objects in a different square
        Vector2i endScreen = new Vector2i((int) endScreenf.x - 2, (int) endScreenf.y - 2);
        float[] gameObjectsIds = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        // Z index compromises this
        for (int i = 0; i < gameObjectsIds.length; i++) {
            if (gameObjectsIds[i] >= 0) {
                GameObject pickedObj = SiriusTheFox.getCurrentScene().getGameObject((int) gameObjectsIds[i]);
                if (!pickedObj.hasComponent(NonPickable.class)) return true;
            }
        }

        return false;
    }

    /**
     * Changes all the game objects in the list based in a blueprint game object.
     * {@link SpriteRenderer} and {@link Transform} components will not be changed.
     *
     * @param blueprintGameObject Game object that all the others game objects will be based.
     * @param gameObjectList List of the all game objects that will be modified based in a game object.
     */
    public void changeAllGameObjects(GameObject blueprintGameObject, List<GameObject> gameObjectList) {
        //Vector2f bluePrintGoLastPosition = new Vector2f(SiriusTheFox.getImGuiLayer().getPropertiesWindow()
        //        .getActiveGameObjectLastPosition());

        for (int i = gameObjectList.size() - 1; i >= 0; i--) {
            GameObject copyGo = blueprintGameObject.copy();

            // Remove some components that we want to modify
            Vector2f copyGoPos = gameObjectList.get(i).getComponent(Transform.class).position;
            copyGo.removeComponent(Transform.class);
            copyGo.removeComponent(SpriteRenderer.class);

            // Modify those components that we have removed
            copyGo.addComponent(blueprintGameObject.getComponent(Transform.class).copy());
            copyGo.setPosition(copyGoPos);

            copyGo.addComponent(gameObjectList.get(i).getComponent(SpriteRenderer.class));
            copyGo.getComponent(SpriteRenderer.class).setDirty(true);
            copyGo.getComponent(SpriteRenderer.class).setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));

            // Mark it with NonPickable component --By this way, it will not be saved if
            // we don't destroy it before saving the scene
            gameObjectList.get(i).addComponent(new NonPickable());

            // Remove current game object and replace it with its copy and some others pretended components
            gameObjectList.get(i).destroy();

            StateMachine stateMachine = copyGo.getComponent(StateMachine.class);
            if (stateMachine != null) {
                stateMachine.refreshTextures();
            }

            SiriusTheFox.getCurrentScene().addGameObject(copyGo);
        }
    }

    /**
     * Checks, in a list of game objects, if all the game objects have the same type of components.
     *
     * @param activeGameObjectList Game objects that will be checked --Each type of its components will be compared.
     * @return true if all game objects have the same component types.
     */
    private boolean areAllComponentsSameType(List<GameObject> activeGameObjectList) {
        boolean notEqual = false;
        for (int i = 0; i < activeGameObjectList.size(); i++) {
            GameObject go = activeGameObjectList.get(i);
            GameObject nextGo;

            if (i + 1 < activeGameObjectList.size())
                nextGo = activeGameObjectList.get(i + 1);
            else
                nextGo = activeGameObjectList.get(0);

            if (!go.hasSameTypeOfComponents(nextGo)) {
                notEqual = true;
                break;
            }
        }

        return !notEqual;
    }

    /**
     * Changes all the game objects that were being selected if they have the same
     * types of components.
     */
    private void selectAndChangeMultipleGameObjects() {
        PropertiesWindow propertiesWindow = SiriusTheFox.getImGuiLayer().getPropertiesWindow();
        List<GameObject> activeGameObjectList = new ArrayList<>(propertiesWindow.getActiveGameObjectList());

        if (activeGameObjectList.isEmpty()) {
            allComponentsHaveSameType = false;
            return;
        } else if (activeGameObjectList.size() == 1) {
            allComponentsHaveSameType = true;
            return;
        }

        if (!allComponentsHaveSameType) {
            if (areAllComponentsSameType(activeGameObjectList)) {
                allComponentsHaveSameType = true;
            }
        }

        if (allComponentsHaveSameType) {
            if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                // activeGameObjectList.get(0) because it is always the first game object in the list
                // that is changed. So we will use it as blueprint to make others game objects
                changeAllGameObjects(activeGameObjectList.get(0), activeGameObjectList);
            }
        }
    }

    private void selectionTool(float dt) {
        selectAndChangeMultipleGameObjects();

        debounce -= dt;

        if (holdingGameObject != null) {
            // Place game object
            holdingGameObject.setPosition(MouseListener.getWorld().x, holdingGameObject.getPosition().y);
            holdingGameObject.setPosition(holdingGameObject.getPosition().x, MouseListener.getWorld().y);

            holdingGameObject.setPosition(
                    ((int) Math.floor(holdingGameObject.getPosition().x / Settings.GRID_WIDTH)
                            * Settings.GRID_WIDTH) + Settings.GRID_WIDTH / 2.0f,
                    holdingGameObject.getPosition().y);
            holdingGameObject.setPosition(holdingGameObject.getPosition().x,
                    ((int) Math.floor(holdingGameObject.getPosition().y / Settings.GRID_HEIGHT)
                            * Settings.GRID_HEIGHT) + Settings.GRID_HEIGHT / 2.0f);

            if (MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth  = Settings.GRID_WIDTH / 2.0f;
                float halfHeight = Settings.GRID_HEIGHT / 2.0f;

                boolean isntBlockInSquare = !isBlockInSquare(holdingGameObject.getPosition().x
                        - halfWidth, holdingGameObject.getPosition().y - halfHeight);

                // Fix duplicated game object placement bug
                if (MouseListener.isDragging() && isntBlockInSquare) {
                    place();
                } else if (!MouseListener.isDragging() && isntBlockInSquare) {
                    place();
                    debounce = debounceTime;
                }
            }

            if (KeyListener.isKeyDown(GLFW_KEY_ESCAPE) || KeyListener.isKeyDown(GLFW_KEY_DELETE)) {
                // SiriusTheFox.getCurrentScene()

                // Destroy game object
                holdingGameObject.destroy();
                holdingGameObject = null;
            }

        } else if (!MouseListener.isDragging() && MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            PickingTexture pickingTexture = SiriusTheFox.getImGuiLayer().getPropertiesWindow().getPickingTexture();
            Scene currentScene = SiriusTheFox.getCurrentScene();
            PropertiesWindow propertiesWindow = SiriusTheFox.getImGuiLayer().getPropertiesWindow();

            int x = (int) MouseListener.getGameViewportX();
            int y = (int) MouseListener.getGameViewportY();
            int gameObjectId = pickingTexture.readPixel(x, y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectId);
            if (pickedObj != null && !pickedObj.hasComponent(NonPickable.class)) {
                propertiesWindow.setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()) {
                propertiesWindow.clearSelected();
            }
            this.debounce = 0.2f;

            // If we are dragging and pressing the mouse's left button
        } else if (MouseListener.isDragging() && MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)
                && SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObjectList().size() != 1) {
            // If we aren't dragging
            if (!boxSelectSet) {
                SiriusTheFox.getImGuiLayer().getPropertiesWindow().clearSelected();
                boxSelectStart = MouseListener.getGameViewport();
                boxSelectSet = true;
            }

            boxSelectEnd = MouseListener.getGameViewport();
            Vector2f boxSelectStartWorld = MouseListener.screenToGameViewport(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToGameViewport(boxSelectEnd);
            Vector2f halfSize = new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld).mul(0.5f);
            DebugDraw.addBox2D(new Vector2f(boxSelectStartWorld).add(halfSize), new Vector2f(halfSize).mul(2.0f), 0.0f);

        } else if (boxSelectSet
                && SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObjectList().size() != 1) {
            PickingTexture pickingTexture = SiriusTheFox.getImGuiLayer().getPropertiesWindow().getPickingTexture();
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
                GameObject pickedObj = SiriusTheFox.getCurrentScene().getGameObject(gameObjectId);
                if (pickedObj != null && !pickedObj.hasComponent(NonPickable.class))
                    // Means that we can't pick it
                    SiriusTheFox.getImGuiLayer().getPropertiesWindow().addActiveGameObject(pickedObj);
            }
        }

        if (holdingGameObject != null
                && SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObject() != null) {
            SiriusTheFox.getImGuiLayer().getPropertiesWindow().clearSelected();
        }
    }

    private void textTool() {
        boxSelectEnd = MouseListener.getGameViewport();

        // Draw box with mouse
        boolean mayDrawBox = MouseListener.isDragging()
                && MouseListener.isMouseButtonPressed(GLFW_MOUSE_BUTTON_1)
                && SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObjectList().isEmpty();

        if (mayDrawBox) {
            Vector2f boxSelectStartWorld = MouseListener.screenToGameViewport(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screenToGameViewport(boxSelectEnd);
            Vector2f halfSize = new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld).mul(0.5f);

            DebugDraw.addBox2D(new Vector2f(boxSelectStartWorld).add(halfSize), new Vector2f(halfSize).mul(2.0f),
                    0.0f, Color.BLACK);
            boxSelectSet = true;
        }

        if (!MouseListener.isDragging()){
            // Place text box
            if (boxSelectSet) {
                Vector2f boxSelectStartWorld = MouseListener.screenToGameViewport(boxSelectStart);
                Vector2f boxSelectEndWorld = MouseListener.screenToGameViewport(boxSelectEnd);
                Vector2f halfSize = new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld).mul(0.5f);
                Vector2f size = new Vector2f(halfSize).mul(2f);
                Vector2f origin = new Vector2f(boxSelectStartWorld).add(halfSize);
                boxSelectSet = false;

                if (Math.abs(size.x) > Settings.GameObjects.DEFAULT_GAME_OBJECT_SCALE
                        && Math.abs(size.y) > Settings.GameObjects.DEFAULT_GAME_OBJECT_SCALE) {
                    SiriusTheFox.getCurrentScene().addGameObject(Prefabs.generateTextObject(origin.x, origin.y,
                            Math.abs(size.x), Math.abs(size.y)));
                }
            }

            // Reset values
            boxSelectStart.zero();
            boxSelectEnd.zero();
            boxSelectStart = MouseListener.getGameViewport();
        }

        boolean cursorInsideTextBoxField;
        if (SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObjectList().isEmpty()) {
            for (GameObject gameObject : SiriusTheFox.getCurrentScene().getGameObjectList()) {
                if (gameObject.hasComponent(TextBox.class)) {
                    TextBox textBox = gameObject.getComponent(TextBox.class);

                    cursorInsideTextBoxField =
                            MouseListener.getWorld().x >= gameObject.getPosition().x - textBox.getWidth() / 2.0f
                                    && MouseListener.getWorld().x <= gameObject.getPosition().x + textBox.getWidth() / 2.0f
                                    && MouseListener.getWorld().y >= gameObject.getPosition().y - textBox.getHeight() / 2.0f
                                    && MouseListener.getWorld().y <= gameObject.getPosition().y + textBox.getHeight() / 2.0f;

                    if (cursorInsideTextBoxField && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
                        SiriusTheFox.getImGuiLayer().getPropertiesWindow().setActiveGameObject(gameObject);
                        break;
                    }
                }
            }
        } else if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_1) && !MouseListener.isDragging()) {
            for (GameObject gameObject : SiriusTheFox.getCurrentScene().getGameObjectList()) {
                if (gameObject.hasComponent(TextBox.class)) {
                    TextBox textBox = gameObject.getComponent(TextBox.class);

                    cursorInsideTextBoxField =
                            MouseListener.getWorld().x >= gameObject.getPosition().x - textBox.getWidth() / 2.0f
                                    && MouseListener.getWorld().x <= gameObject.getPosition().x + textBox.getWidth() / 2.0f
                                    && MouseListener.getWorld().y >= gameObject.getPosition().y + textBox.getHeight() / 2.0f
                                    && MouseListener.getWorld().y <= gameObject.getPosition().y - textBox.getHeight() / 2.0f;

                    if (!cursorInsideTextBoxField) {
                        SiriusTheFox.getImGuiLayer().getPropertiesWindow().clearSelected();
                    }
                }
            }
        }

        allComponentsHaveSameType = areAllComponentsSameType(SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObjectList());
    }

    @Override
    public void editorUpdate(float dt) {
        ToolWindow toolWindow = SiriusTheFox.getImGuiLayer().getToolWindow();
        switch (toolWindow.getCurrentTool()) {
            case SELECTION_TOOL -> selectionTool(dt);
            case TEXT_TOOL -> textTool();
        }
    }

    public GameObject getHoldingGameObject() {
        return holdingGameObject;
    }
}
