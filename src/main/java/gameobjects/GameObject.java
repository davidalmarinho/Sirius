package gameobjects;

import gameobjects.components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private static int ID_COUNTER = 0;
    private int uid = -1;

    public final String NAME;
    public List<Component> componentList;
    public Transform transform;
    private int zIndex;

    public GameObject(String name, Transform transform, int zIndex) {
        this.NAME = name;
        componentList = new ArrayList<>();
        this.transform = transform;
        this.zIndex = zIndex;

        this.uid = ID_COUNTER++;
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
        // Set an ID
        component.generateId();

        componentList.add(component);
        component.gameObject = this;
    }

    public void imgui() {
        for (Component c : componentList) {
            c.imgui();
        }
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public int getzIndex() {
        return zIndex;
    }

    public int getUid() {
        return uid;
    }
}
