package sirius.editor.components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import sirius.SiriusTheFox;
import sirius.animations.StateMachine;
import sirius.editor.imgui.PropertiesWindow;
import sirius.input.KeyListener;
import sirius.utils.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    private float debounceTime = 0.2f;
    private float debounce = 0.0f;

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PropertiesWindow propertiesWindow = SiriusTheFox.getImGuiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjectList = propertiesWindow.getActiveGameObjectList();

        // When pressing left shift key, the game object takes "smaller steps" --Moves less
        float multiplayer = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (KeyListener.isBindDown(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_D) && activeGameObject != null) {
            GameObject newObj = activeGameObject.copy();
            newObj.getTransform().position.add(Settings.GRID_WIDTH, 0.0f);

            // Refresh animations
            if (newObj.hasComponent(StateMachine.class))
                newObj.getComponent(StateMachine.class).refreshTextures();

            SiriusTheFox.getCurrentScene().addGameObject(newObj);
            propertiesWindow.setActiveGameObject(newObj);
        } else if (KeyListener.isBindDown(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_D) && activeGameObjectList.size() > 1) {
            List<GameObject> gameObjectList = new ArrayList<>(activeGameObjectList);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjectList) {
                GameObject copy = go.copy();
                SiriusTheFox.getCurrentScene().addGameObject(copy);

                // Refresh animations
                if (copy.hasComponent(StateMachine.class)) {
                    copy.getComponent(StateMachine.class).refreshTextures();
                }

                propertiesWindow.addActiveGameObject(copy);
            }
        } else if (KeyListener.isKeyDown(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjectList)
                go.destroy();

            propertiesWindow.clearSelected();
        // Page-up increases z index
        } else if (KeyListener.isKeyDown(GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList)
                go.zIndex(1);

        // Page-down decreases z index
        } else if (KeyListener.isKeyDown(GLFW_KEY_PAGE_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList)
                go.zIndex(-1);

        // Move game object up
        } else if (KeyListener.isKeyDown(GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList)
                go.transform(0, Settings.GRID_HEIGHT * multiplayer);

        // Move game object left
        } else if (KeyListener.isKeyDown(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList)
                go.transform(-Settings.GRID_WIDTH * multiplayer, 0);

        // Move game object right
        } else if (KeyListener.isKeyDown(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList)
                go.transform(Settings.GRID_WIDTH * multiplayer, 0);

        // Move game object down
        } else if (KeyListener.isKeyDown(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList)
                go.transform(0, -Settings.GRID_HEIGHT * multiplayer);
        }
    }
}
