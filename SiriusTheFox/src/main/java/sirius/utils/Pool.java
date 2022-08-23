package sirius.utils;

import compiling_tools.InlineCompiler;
import sirius.Sound;
import sirius.editor.imgui.sprite_animation_window.AnimationBlueprint;
import sirius.levels.Level;
import sirius.rendering.Shader;
import sirius.rendering.fonts.Font;
import sirius.rendering.spritesheet.Spritesheet;
import sirius.rendering.spritesheet.Texture;

import java.io.File;
import java.util.*;

public class Pool {
    public static class Assets {
        private static Map<String, Shader> shaders;
        private static Map<String, Texture> textures;
        private static List<Level> levelList;
        private static Map<String, AnimationBlueprint> animationsMap;
        private static Map<String, Spritesheet> spritesheets;
        private static Map<String, Sound> stringSoundHashMap;
        private static Map<String, Font> fontMap;

        static {
            shaders = new HashMap<>();
            textures = new HashMap<>();
            levelList = new ArrayList<>();
            animationsMap = new HashMap<>();
            spritesheets = new HashMap<>();
            stringSoundHashMap = new HashMap<>();
            fontMap = new HashMap<>();
        }

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

        public static Map<String, Spritesheet> getSpritesheetsMap() {
            return spritesheets;
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

        public static void addSound(String soundFile, boolean doesLoop) {
            File file = new File(soundFile);
            if (stringSoundHashMap.containsKey(file.getAbsolutePath())) {
                stringSoundHashMap.get(file.getAbsolutePath());
            } else {
                Sound sound = new Sound(file.getAbsolutePath(), doesLoop);
                Assets.stringSoundHashMap.put(file.getAbsolutePath(), sound);
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

        public static void addAnimation(String filePath, AnimationBlueprint animation) {
            File file = new File(filePath);

            if (!animationsMap.containsKey(file.getPath())) {
                animationsMap.put(file.getPath(), animation);
            }/* else {
                System.err.println("Error: Couldn't add '" + filePath + "'. File already exists.");
            }*/
        }

        public static void removeAnimation(String filePath) {
            File file = new File(filePath);

            /* else {
                System.err.println("Error: Couldn't add '" + filePath + "'. File already exists.");
            }*/
            animationsMap.remove(file.getPath());
        }

        public static AnimationBlueprint getAnimation(String filePath) {
            File file = new File(filePath);

            if (animationsMap.containsKey(file.getPath()))
                return animationsMap.get(file.getPath());
            else
                System.err.println("Error: Couldn't get '" + filePath + "'. File doesn't exist.");

            return null;
        }

        public static void updateAnimation(String filePath, AnimationBlueprint animation) {
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

        public static String[] getAnimationsNames() {
            String[] keys = new String[animationsMap.size()];

            int i = 0;
            for (String key : animationsMap.keySet()) {
                String[] split = key.split("(assets\\\\animations\\\\)|(assets/animations/)");
                keys[i] = split[1].split(".json")[0];
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

        /**
         * Adds a font to the assets' manager.
         *
         * @param filepath Font's filepath.
         * @param fontSize The size of the font. Bigger sizes means better quality but badder performance
         *                 I personally recommend at least a size of 64.
         */
        public static void addFont(String filepath, int fontSize) {
            File file = new File(filepath);
            Font font = new Font(file.getPath(), fontSize);

            if (!fontMap.containsKey(file.getPath())) {
                fontMap.put(file.getPath(), font);
            }
        }

        /**
         * Adds a font to the assets' manager.
         *
         * @param filepath Font's filepath.
         * The size of the font is 64 by default.
         */
        public static void addFont(String filepath) {
            addFont(filepath, 64);
        }

        public static void addAllShaders() {
            File folder = new File(Settings.Files.SHADERS_FOLDER);

            for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (fileEntry.isDirectory()) {
                    for (File file : Objects.requireNonNull(fileEntry.listFiles())) {
                        if (file.getName().endsWith(".glsl"))
                            addShader(file.getPath());
                    }
                }
            }
        }

        public static void addAllFonts() {
            File folder = new File(Settings.Files.FONTS_FOLDER);

            for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
                if (fileEntry.isDirectory()) {
                    for (File file : Objects.requireNonNull(fileEntry.listFiles())) {
                        if (file.getName().endsWith(".ttf") || file.getName().endsWith(".otf"))
                            addFont(file.getPath());
                    }
                }
            }
        }

        public static Font getFont(String filepath) {
            return fontMap.get(new File(filepath).getPath());
        }

        public static String[] getFontsPaths() {
            String[] fontsPaths = new String[fontMap.size()];

            int i = 0;
            for (String key : fontMap.keySet()) {
                fontsPaths[i] = key.replace("\\", "/");
                i++;
            }

            return fontsPaths;
        }

        public static String[] getFontsNames() {
            String[] fontsPaths = new String[fontMap.size()];

            int i = 0;
            File file;
            for (String key : fontMap.keySet()) {
                file = new File(key);
                String name = file.getName();
                fontsPaths[i] = name.split("(\\.)")[0];
                i++;
            }

            Arrays.sort(fontsPaths);

            return fontsPaths;
        }

        public static void clearTextures() {
            spritesheets.clear();
            textures.clear();
        }
    }

    public static class Scripts {
        public static String customPropertiesWindowPath;
        public static String customPropertiesWindowScript;

        public static String customPrefabsPath;
        public static String customPrefabsScript;

        public static Map<File, String> componentFileStringMap;

        private static boolean flag;

        static {
            componentFileStringMap = new HashMap<>();
            flag = true;
        }

        public static void searchForComponentsFiles() {
            File srcDir = new File("src");
            loopDirs(srcDir);
        }

        private static void loopDirs(File directory) {
            for (File file : directory.listFiles()) {
                if (!file.isDirectory()) {
                    // No repeated files to componentScriptList
                    if (componentFileStringMap.keySet().stream().anyMatch(s ->
                            s.getPath().equals(file.getPath()) || file.getPath().endsWith("~"))) {
                        continue;
                    }

                    if (Scanner.hasString(file, "extends Component")) {
                        componentFileStringMap.put(file, Scanner.readFile(file));
                        if (!flag) {
                            InlineCompiler.printStart();
                            InlineCompiler.compileCode(file);
                            InlineCompiler.printEnd();
                        }
                    }

                } else {
                    loopDirs(file);
                }
            }

            if (flag) {
                flag = false;
            }
        }
    }
}
