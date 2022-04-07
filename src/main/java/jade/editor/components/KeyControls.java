package jade.editor.components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import jade.SiriusTheFox;
import jade.animations.StateMachine;
import jade.editor.PropertiesWindow;
import jade.input.KeyListener;
import jade.utils.Settings;

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
        float multiplayer = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_D) && activeGameObject != null) {
            GameObject newObj = activeGameObject.copy();
            newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);

            // Refresh animations
            if (newObj.hasComponent(StateMachine.class)) {
                newObj.getComponent(StateMachine.class).refreshTextures();
            }

            SiriusTheFox.getCurrentScene().addGameObject(newObj);
            propertiesWindow.setActiveGameObject(newObj);
        } else if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_D) && activeGameObjectList.size() > 1) {
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
            for (GameObject go : activeGameObjectList) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        } else if (KeyListener.isKeyDown(GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.zIndex++;
            }
        } else if (KeyListener.isKeyDown(GLFW_KEY_PAGE_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.zIndex--;
            }
        } else if (KeyListener.isKeyDown(GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.position.y += Settings.GRID_HEIGHT * multiplayer;
            }
        } else if (KeyListener.isKeyDown(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.position.x -= Settings.GRID_WIDTH * multiplayer;
            }
        } else if (KeyListener.isKeyDown(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.position.x += Settings.GRID_HEIGHT * multiplayer;
            }
        } else if (KeyListener.isKeyDown(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjectList) {
                go.transform.position.y -= Settings.GRID_HEIGHT * multiplayer;
            }
        }
    }
}
