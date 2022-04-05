import gameobjects.GameObject;
import jade.SiriusTheFox;
import jade.editor.ICustomPropertiesWindow;
import jade.editor.PropertiesWindow;

public class MyPropertiesWindow implements ICustomPropertiesWindow {

    @Override
    public void imgui(GameObject go) {
        // GameObject go = SiriusTheFox.getImGuiLayer().getPropertiesWindow().getActiveGameObject();
        PropertiesWindow.addMenuItem(go, "Add Player Controller", new PlayerController());
        PropertiesWindow.addMenuItem(go, "Add Ground", new Ground());
    }
}
