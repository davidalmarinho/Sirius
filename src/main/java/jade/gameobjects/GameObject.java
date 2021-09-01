package jade.gameobjects;

import jade.gameobjects.components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    public final String NAME;
    public List<Component> componentList;
    public Transform transform;

    public GameObject(String name) {
        this.NAME = name;
        componentList = new ArrayList<>();
        this.transform = new Transform();
    }

    public GameObject(String name, Transform transform) {
        this.NAME = name;
        componentList = new ArrayList<>();
        this.transform = transform;
    }

    public void start() {
        for (Component c : componentList) {
            c.start();
        }
    }

    public void update(float dt) {
        for (Component c : componentList) {
            c.update(dt);
        }
    }

    public <T extends Component> T getComponent(Class<T> ComponentClass) {
        for (Component component : componentList) {
            if (ComponentClass.isAssignableFrom(component.getClass())) {
                try {
                    return ComponentClass.cast(component);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Couldn't cast component";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> ComponentClass) {
        for (int i = 0; i < componentList.size(); i++) {
            Component component = componentList.get(i);

            if (ComponentClass.isAssignableFrom(component.getClass())) {
                componentList.remove(ComponentClass.cast(component));
            }
        }
    }

    public void addComponent(Component component) {
        componentList.add(component);
        component.gameObject = this;
    }
}
