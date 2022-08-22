package sirius.encode_tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameobjects.GameObject;
import gameobjects.components.Component;
import sirius.editor.NonPickable;
import sirius.editor.imgui.sprite_animation_window.AnimationBlueprint;
import sirius.editor.imgui.sprite_animation_window.Animator;
import sirius.levels.Level;
import sirius.rendering.fonts.Font;
import sirius.utils.AssetPool;

import java.io.*;
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

    public static Gson animatorGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Animator.class, new AnimatorDeserializer())
                .enableComplexMapKeySerialization()
                .create();
    }

    public static Gson defaultGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .create();
    }

    /**
     * Saves a {@link GameObject} list into a file.
     *
     * @param gameObjectList {@link GameObject} list that needs to be saved in a file.
     */
    public static void saveGameObjectListInFile(List<GameObject> gameObjectList) {
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
     * Saves a {@link AnimationBlueprint} object into a file.
     *
     * @param animationBlueprint Object needed to be saved in the file.
     * @param filePath Path to the saved file.
     */
    public static void saveAnimation(AnimationBlueprint animationBlueprint, String filePath) {
        Gson gson = animatorGson();

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(gson.toJson(animationBlueprint));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFont(Font font, String filepath) {
        Gson gson = defaultGson();

        try {
            FileWriter writer = new FileWriter(filepath);
            writer.write(gson.toJson(font));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a game object's animation from a file.
     *
     * @param filePath Path to the saved file.
     * @return A {@link Animator} object.
     */
    public static AnimationBlueprint getAnimation(String filePath) {
        Gson gson = animatorGson();
        return gson.fromJson(readFile(filePath), AnimationBlueprint.class);
    }

    public static Font getFontProperty(String filePath) {
        Gson gson = defaultGson();
        return gson.fromJson(readFile(filePath), Font.class);
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
    public static String readFile(String filePath) {
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
    public static GameObject getGameObjectCopy(GameObject gameObject) {
        Gson gson = gameObjectsGson();

        String objAsJson  = gson.toJson(gameObject);

        // Return copy of game object
        return gson.fromJson(objAsJson, GameObject.class);
    }

    /**
     * Saves an array of Strings and values in a file.
     * Each String element corresponds to an object element by numerical order, being separated from each other by ':'.
     * Example of output:
     *      carBrand:Mercedes/
     *      playerPosX:60.6f/
     *      playerPosY:50.0f/
     *
     * To load the content of a file you might use the {@link Encode#loadFromFile(String, int)} method.
     *
     * @param outputFilePath File path of the file --make sure to include the file, not just the directory.
     * @param encode Put 0 to don't encode the file or, we recommend putting 20 to encode the file.
     */
    public static void saveInFile(String[] opt, Object[] value, String outputFilePath, int encode) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < opt.length; i++) {
            StringBuilder saveCurrent = new StringBuilder(opt[i]);
            saveCurrent.append(":");
            char[] curCharToConvert = String.valueOf(value[i]).toCharArray();

            for (int j = 0; j < curCharToConvert.length; j++) {
                curCharToConvert[j] += encode;
                saveCurrent.append(curCharToConvert[j]);
            }
            try {
                assert null != writer;
                writer.write(String.valueOf(saveCurrent));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            assert null != writer;
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all the content of a file and put it in a single line.
     * This method might be used with {@link Encode#saveInFile(String[], Object[], String, int)} method.
     *
     * @param filepath Filepath of the file.
     * @param encode Should be the same used when called {@link Encode#saveInFile(String[], Object[], String, int)} method.
     * @return A String with all the content of a file.
     */
    public static String loadFromFile(String filepath, int encode) {
        File file = new File(filepath);
        StringBuilder current = new StringBuilder();

        if (file.exists()) {
            String analyser;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(filepath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                while (true) {
                    assert reader != null;
                    if ((analyser = reader.readLine()) == null) break;
                    String[] navigator = analyser.split(":");
                    char[] decode = navigator[1].toCharArray();
                    navigator[1] = "";
                    for (int i = 0; i < decode.length; i++) {
                        decode[i] -= encode;
                        navigator[1] += decode[i];
                    }

                    current.append(navigator[0]);
                    current.append(":");
                    current.append(navigator[1]);
                    current.append("/");
                }
            } catch (IOException ioE) {
                ioE.printStackTrace();
            }
        }
        return String.valueOf(current);
    }

    /**
     * Formats a String.
     * @param str Desired String to format
     * @return The first letter capitalized, all the rest of the words goes lower case, 'A' -> 'a'
     * and all underscores are replaces with white spaces, '_' -> ' '.
     */
    public static String formatString(String str) {
        StringBuilder formattedSentence = new StringBuilder();

        String firstLetterCapitalized = String.valueOf(str).substring(0, 1).toUpperCase();
        formattedSentence.append(firstLetterCapitalized);

        String restWordFormatted = String.valueOf(str).substring(1)
                .replace('_', ' ')
                .toLowerCase();
        formattedSentence.append(restWordFormatted);

        return formattedSentence.toString();
    }

    public static boolean hasString(File file, String str) {
        String content = Encode.readFile(file.getPath());
        return content.contains(str);
    }
}
