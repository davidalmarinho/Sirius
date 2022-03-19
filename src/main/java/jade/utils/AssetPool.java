package jade.utils;

import jade.Sound;
import jade.rendering.spritesheet.Spritesheet;
import jade.rendering.Shader;
import jade.rendering.spritesheet.Texture;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    // Estático para as referências não serem limpas pelo coletor de lixo
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();
    public static Map<String, Sound> stringSoundHashMap = new HashMap<>();

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
}
