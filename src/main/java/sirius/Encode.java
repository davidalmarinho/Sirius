package sirius;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.NotNull;
import gameobjects.GameObject;
import gameobjects.GameObjectDeserializer;
import gameobjects.components.Component;
import gameobjects.components.ComponentDeserializer;
import sirius.editor.NonPickable;
import sirius.levels.Level;
import sirius.utils.AssetPool;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Encode {

    /**
     * Creates a Gson object with pretended characteristics.
     * @return A Gson object with:
     * <p>
     *         {@link GsonBuilder#setPrettyPrinting()}
     *         {@link GsonBuilder#registerTypeAdapter(Type component, Object componentDeserializer)}
     *         {@link GsonBuilder#registerTypeAdapter(Type gameObject, Object gameObjectDeserializer))}
     *         {@link GsonBuilder#enableComplexMapKeySerialization()}
     * </p>
     */
    public static Gson newGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .enableComplexMapKeySerialization()
                .create();
    }

    /**
     * Saves a game object list into a file.
     *
     * @param gameObjectList Game object list that needs to be saved in a file.
     */
    public static void saveGameObjectListInFile(@NotNull List<GameObject> gameObjectList) {
        Gson gson = newGson();

        try {
            // Save gameObjectList in a txt file
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
     * Get game object list from a file.
     *
     * @param filePath Path to the saved file.
     * @return A game object list.
     */
    public static GameObject[] getGameObjectsFromFile(String filePath) {
        Gson gson = newGson();
        return gson.fromJson(filePath, GameObject[].class);
    }

    /**
     * Gets a copy of a game object.
     *
     * @param gameObject Game object that needs a copy.
     * @return New game object.
     */
    public static GameObject getGameObjectCopy(@NotNull GameObject gameObject) {
        Gson gson = newGson();

        String objAsJson  = gson.toJson(gameObject);

        // Return copy of game object
        return gson.fromJson(objAsJson, GameObject.class);
    }
}
