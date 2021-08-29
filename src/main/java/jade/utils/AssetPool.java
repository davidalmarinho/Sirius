package jade.utils;

import jade.gameobjects.components.Spritesheet;
import jade.renderer.Shader;
import jade.renderer.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    // Estático para as referências não serem limpas pelo coletor de lixo
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, Spritesheet> spritesheets = new HashMap<>();

    public static Shader getShader(String filePath) {
        // Verificar se conseguimos aceder ao ficheiro
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
        File file = new File(filePath);

        if (!textures.containsKey(filePath)) {
            addTexture(file.getAbsolutePath());
        }

        return textures.get(file.getAbsolutePath());
    }

    private static void addTexture(String filePath) {
        textures.put(filePath, new Texture(filePath));
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
}
