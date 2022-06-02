package sirius.utils;

import sirius.Sound;
import sirius.editor.imgui.sprite_animation_window.Animator;
import sirius.levels.Level;
import sirius.rendering.spritesheet.Spritesheet;
import sirius.rendering.Shader;
import sirius.rendering.spritesheet.Texture;

import java.io.File;
import java.util.*;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static List<Level> levelList = new ArrayList<>();
    private static Map<String, Animator> animationsMap = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static Map<String, Sound> stringSoundHashMap = new HashMap<>();


    public static Shader getShader(String filePath) {
        // Verify if we could access the file
        File file = new File(filePath);

        // If we don't have the shader yet, we put it on the list
        if (!shaders.containsKey(filePath)) {
            addShader(file.getAbsolutePath());
        }

        return shaders.get(file.getAbsolutePath());
    }

    private static void addShader(String filePath) {
        Shader shader = new Shader(filePath);
        shader.compile();
        shaders.put(filePath, shader);
    }

    public static Texture getTexture(String filePath) {
        return textures.computeIfAbsent(filePath, Texture::new);
    }

    public static void addSpritesheet(String filePath, Spritesheet spritesheet) {
        File file = new File(filePath);

        if (!spritesheets.containsKey(file.getAbsolutePath())) {
            spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String filePath) {
        File file = new File(filePath);

        if (spritesheets.containsKey(file.getAbsolutePath())) {
            return spritesheets.get(file.getAbsolutePath());
        }

        // In case of not been added to the spritesheetMap
        assert false : "Error: Couldn't access '" + file.getAbsolutePath() + "' because it hasn't been added yet.";
        return spritesheets.getOrDefault(file.getAbsolutePath(), null);
    }

    public static Collection<Sound> getAllSounds() {
        return stringSoundHashMap.values();
    }

    public static Sound getSound(String soundFile) {
        File file = new File(soundFile);
        if (stringSoundHashMap.containsKey(file.getAbsolutePath()))
            return stringSoundHashMap.get(file.getAbsolutePath());
        else assert false : "Sound file not added '" + soundFile + "'";

        return null;
    }

    public static Sound addSound(String soundFile, boolean doesLoop) {
        File file = new File(soundFile);
        if (stringSoundHashMap.containsKey(file.getAbsolutePath()))
            return stringSoundHashMap.get(file.getAbsolutePath());
        else {
            Sound sound = new Sound(file.getAbsolutePath(), doesLoop);
            AssetPool.stringSoundHashMap.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }

    public static void addLevel(Level level) {
        if (levelList.stream().anyMatch(level1 -> level.getPath().equals(level1.getPath()))) {
            return;
        }

        levelList.add(level);
    }

    public static Level getLevel(int id) {
        return levelList.stream().filter(level -> level.getId() == id).findFirst().orElse(null);
    }

    public static void addAnimation(String filePath, Animator animation) {
        File file = new File(filePath);

        if (!animationsMap.containsKey(file.getPath())) {
            animationsMap.put(file.getPath(), animation);
        } else {
            System.err.println("Error: Couldn't add '" + filePath + "'. File already exists.");
        }
    }

    public static Animator getAnimation(String filePath) {
        File file = new File(filePath);

        if (animationsMap.containsKey(file.getPath()))
            return animationsMap.get(file.getPath());
        else
            System.err.println("Error: Couldn't get '" + filePath + "'. File doesn't exist.");

        return null;
    }

    public static void updateAnimation(String filePath, Animator animation) {
        File file = new File(filePath);

        if (!animationsMap.containsKey(file.getPath())) {
            animationsMap.put(file.getPath(), animation);
        } else {
            animationsMap.remove(file.getPath());
            animationsMap.put(file.getPath(), animation);
        }
    }

    public static String[] getAnimationsPaths() {
        String[] keys = new String[animationsMap.size()];

        int i = 0;
        for (String key : animationsMap.keySet()) {
            keys[i] = key;
            i++;
        }

        return keys;
    }

    public static List<Level> getLevelList() {
        return levelList;
    }

    /**
     * Gets the path of all loaded spritesheets.
     *
     * @return An array with all loaded spritesheets.
     */
    public static String[] getSpritesheetsPaths() {
        String[] spritesheetsPaths = new String[spritesheets.size()];

        int i = 0;
        for (String key : spritesheets.keySet()) {
            // TODO: 18/05/2022 Get the path that doesn't depends on the OS
            spritesheetsPaths[i] = key;
            i++;
        }

        return spritesheetsPaths;
    }

    /**
     * Transforms the spritesheet path into a short name.
     * Example:
     *      'assets/spritesheetCharacter.png' turns into 'spritesheetCharacter'
     *      'assets/dungeons.png' turns into 'dungeons'
     *
     *
     * @return The name of the spritesheet.
     */
    public static String[] getSpritesheetsNames() {
        String[] spritesheetsNames = new String[spritesheets.size()];

        int i = 0;
        File file;
        for (String key : spritesheets.keySet()) {
            file = new File(key);
            String name = file.getName();
            spritesheetsNames[i] = name.split("\\.")[0];
            i++;
        }

        return spritesheetsNames;
    }
}
