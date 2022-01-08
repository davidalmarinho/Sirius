package gameobjects.components;

import imgui.ImGui;
import gameobjects.GameObject;
import jade.rendering.Color;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    private static int ID_COUNTER = 0;
    private int uid = -1;

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
                // We can't change final variables, so we shouldn't dispose them
                boolean isFinal = Modifier.isFinal(field.getModifiers());
                if (isFinal) {
                    continue;
                }

                // We don't want that transient variables to show up in LevelEditorScene
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) {
                    continue;
                }

                // Change the access of private variables to public variables to the program be able to change its data
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                boolean isProtected = Modifier.isProtected(field.getModifiers()) | field.getModifiers() == 0;
                if (isPrivate || isProtected) {
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
                } else if (type == float.class) {
                    float val = (float) value;
                    float[] imFloat = {val};
                    if (ImGui.dragFloat(name + ": ", imFloat)) {
                        field.set(this, imFloat[0]);
                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    float[] imVec = {val.x, val.y};
                    if (ImGui.dragFloat2(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1]);
                    }
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                } else if (type == Color.class) {
                    Vector4f vec4Color = ((Color) value).getColor();
                    float[] colors = {vec4Color.x, vec4Color.y, vec4Color.z, vec4Color.w};
                    if (ImGui.dragFloat4(name + ": ", colors)) {
                        ((Color) value).setColor(colors[0], colors[1], colors[2], colors[3]);
                    }
                }

                // Change the access of the formerly private variables to private again
                if (isPrivate || isProtected) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public void generateId() {
        if (this.uid == -1) {
            this.uid = ID_COUNTER++;
        }
    }

    public int getUid() {
        return uid;
    }
}
