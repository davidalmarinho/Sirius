package gameobjects.components;

import jade.editor.JImGui;
import imgui.ImGui;
import gameobjects.GameObject;
import imgui.type.ImInt;
import jade.rendering.Color;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {
    private transient static int ID_COUNTER = 0;
    private transient int uid = -1;

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

    public void editorUpdate(float dt) {

    }

    public void update(float dt) {

    }

    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void preSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void postSolve(GameObject collidingObject, Contact contact, Vector2f hitNormal) {

    }

    public void imgui() {
        try {
            // Get the variables (field) of some component
            Field[] fields = this.getClass().getDeclaredFields();
            Field[] superFields = this.getClass().getSuperclass().getDeclaredFields();

            for (Field field : fields)
                checkField(field);

            for (Field superField : superFields)
                checkField(superField);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void checkField(Field field) throws IllegalAccessException {
        // We can't change final variables, so we shouldn't dispose them
        boolean isFinal = Modifier.isFinal(field.getModifiers());
        if (isFinal) return;


        // We don't want that transient variables to show up in LevelEditorScene
        boolean isTransient = Modifier.isTransient(field.getModifiers());
        if (isTransient) return;


        // Change the access of private variables to public variables to the program be able to change its data
        boolean isPrivate = Modifier.isPrivate(field.getModifiers());
        boolean isProtected = Modifier.isProtected(field.getModifiers()) || field.getModifiers() == 0;
        if (isPrivate || isProtected)
            field.setAccessible(true);

        // Gets the type of variable
        Class<?> type = field.getType();
        // Gets variable's data
        Object value = field.get(this);
        // Gets variable's name
        String name = field.getName();

        if (type == int.class) {
            int val = (int) value;
            field.set(this, JImGui.dragInt(name, val));
        } else if (type == float.class) {
            float val = (float) value;
            field.set(this, JImGui.dragFloat(name, val));
        } else if (type == boolean.class) {
            boolean val = (boolean) value;
            if (ImGui.checkbox(name + ": ", val)) {
                field.set(this, !val);
            }
        } else if (type == String.class) {
            field.set(this, JImGui.inputText(field.getName() + ": ", (String) value));
        } else if (type == Vector2f.class) {
            Vector2f val = (Vector2f) value;
            JImGui.drawVec2Control(name, val);
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
        } else if (type.isEnum()) {
            String[] enumValues = getEnumValues(type);
            String enumType = ((Enum<?>) value).name();
            ImInt index = new ImInt(indexOf(enumType, enumValues));

            if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                field.set(this, type.getEnumConstants()[index.get()]);
            }
        }

        // Change the access of the formerly private variables to private again
        if (isPrivate || isProtected) {
            field.setAccessible(false);
        }
    }

    private String[] getEnumValues(Class<?> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];

        for (int i = 0; i < enumType.getEnumConstants().length; i++) {
            Object enumIntegerValue = enumType.getEnumConstants()[i];
            enumValues[i] = enumIntegerValue.toString();
        }

        return enumValues;
    }

    private int indexOf(String str, String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (str.equals(arr[i])) return i;
        }

        return -1;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public void destroy() {
        // TODO: 19/02/2022 Some feature to show that they are dead, a sound, visual effect... Or just erase me and do nothing
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
