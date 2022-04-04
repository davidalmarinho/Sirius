package jade.editor.components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import jade.SiriusTheFox;
import jade.Window;
import jade.editor.PropertiesWindow;
import jade.input.KeyListener;
import jade.utils.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {

    @Override
    public void editorUpdate(float dt) {
        PropertiesWindow propertiesWindow = SiriusTheFox.getImGuiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjectList = propertiesWindow.getActiveGameObjectList();

        if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_D) && activeGameObject != null) {
            GameObject newObj = activeGameObject.copy();
            newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            SiriusTheFox.getCurrentScene().addGameObject(newObj);
            propertiesWindow.setActiveGameObject(newObj);
        } else if (KeyListener.isBindPressed(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_D) && activeGameObjectList.size() > 1) {
            List<GameObject> gameObjectList = new ArrayList<>(activeGameObjectList);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjectList) {
                GameObject copy = go.copy();
                SiriusTheFox.getCurrentScene().addGameObject(copy);
                propertiesWindow.addActiveGameObject(copy);
            }
        } else if (KeyListener.isKeyDown(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjectList) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        }
    }
}
