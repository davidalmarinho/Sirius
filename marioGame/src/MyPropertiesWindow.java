import jade.editor.ICustomPropertiesWindow;
import jade.editor.PropertiesWindow;

public class MyPropertiesWindow implements ICustomPropertiesWindow {

    @Override
    public void imgui() {
        PropertiesWindow.addMenuItem("Add Player Controller", new PlayerController());
        PropertiesWindow.addMenuItem("Add Ground", new Ground());
    }
}
