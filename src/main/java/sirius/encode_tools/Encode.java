package sirius.encode_tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.NotNull;
import gameobjects.GameObject;
import gameobjects.components.Component;
import sirius.editor.NonPickable;
import sirius.editor.imgui.sprite_animation_window.StateMachineChild;
import sirius.levels.Level;
import sirius.utils.AssetPool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Encode {

    /**
     * Creates a {@link Gson} object with pretended characteristics.
     * @return A {@link Gson} object with:
     * <p>
     *         {@link GsonBuilder#setPrettyPrinting()}
     *         {@link GsonBuilder#registerTypeAdapter(Type component, Object componentDeserializer)}
     *         {@link GsonBuilder#registerTypeAdapter(Type gameObject, Object gameObjectDeserializer))}
     *         {@link GsonBuilder#enableComplexMapKeySerialization()}
     * </p>
     */
    public static Gson gameObjectsGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();
    }

    public static Gson stateMachineChildGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(StateMachineChild.class, new StateMachineChildDeserializer())
                .enableComplexMapKeySerialization()
                .create();
    }

    /**
     * Saves a {@link GameObject} list into a file.
     *
     * @param gameObjectList {@link GameObject} list that needs to be saved in a file.
     */
    public static void saveGameObjectListInFile(@NotNull List<GameObject> gameObjectList) {
        Gson gson = gameObjectsGson();

        try {
            // Save gameObjectList in a .json file
            FileWriter writer = new FileWriter(AssetPool.getLevel(Level.currentLevel).getPath());
            List<GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject obj : gameObjectList) {

                // Bug fix --Don't save game objects that are attached to the cursor
                if (obj.hasComponent(NonPickable.class)) continue;

                if (obj.isDoSerialization()) {
                    objsToSerialize.add(obj);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a {@link StateMachineChild} object into a file.
     *
     * @param stateMachineChild Object needed to be saved in the file.
     * @param filePath Path to the saved file.
     */
    public static void saveAnimation(@NotNull StateMachineChild stateMachineChild, @NotNull String filePath) {
        Gson gson = stateMachineChildGson();

        try {
            // Save state machine child in a .json file
            FileWriter writer = new FileWriter(filePath);
            writer.write(gson.toJson(stateMachineChild));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a game object's animation from a file.
     *
     * @param filePath Path to the saved file.
     * @return A {@link StateMachineChild} object.
     */
    public static StateMachineChild getAnimation(@NotNull String filePath) {
        Gson gson = stateMachineChildGson();
        return gson.fromJson(readFile(filePath), StateMachineChild.class);
    }

    /**
     * Get game object list from a file.
     *
     * @param filePath Path to the saved file.
     * @return A {@link GameObject} list.
     */
    public static GameObject[] getGameObjectsFromFile(String filePath) {
        Gson gson = gameObjectsGson();
        return gson.fromJson(readFile(filePath), GameObject[].class);
    }

    /**
     * Reads a file.
     *
     * @param filePath Path to the saved file.
     * @return All the content of the file in a String.
     */
    public static String readFile(@NotNull String filePath) {
        File file = new File(filePath);
        String inFile = "";
        if (file.exists()) {
            try {
                inFile = new String(Files.readAllBytes(Paths.get(file.getPath())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return inFile;
    }

    /**
     * Gets a copy of a game object.
     *
     * @param gameObject {@link GameObject} that needs a copy.
     * @return New {@link GameObject}.
     */
    public static GameObject getGameObjectCopy(@NotNull GameObject gameObject) {
        Gson gson = gameObjectsGson();

        String objAsJson  = gson.toJson(gameObject);

        // Return copy of game object
        return gson.fromJson(objAsJson, GameObject.class);
    }
}
