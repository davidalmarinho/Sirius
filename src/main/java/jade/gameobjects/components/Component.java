package jade.gameobjects.components;

import imgui.ImGui;
import jade.gameobjects.GameObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    /* Marked as transient, because each component as its parent game object and each
     * parent game object as its components and that components have, again, a parent game object.
     */
    public transient GameObject gameObject = null;
    /**
     * We have this method, because we can't access the gameObject
     * inside builder's method.
     */
    public void start() {

    }

    public void update(float dt) {

    }

    public void imgui() {
        try {
            // Get the variables (field) of some component
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                // Change the access of private variables to public variables to the program be able to change its data
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }

                // Gets the type of variable
                Class<?> type = field.getType();
                // Gets variable's data
                Object value = field.get(this);
                // Gets variable's name
                String name = field.getName();

                if (type == int.class) {
                    int val = (int) value;
                    int[] imInt = {val};
                    if (ImGui.dragInt(name + ": ", imInt)) {
                        field.set(this, imInt[0]);
                    }
                }

                // Change the access of the formerly private variables to private again
                if (isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
