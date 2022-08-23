package sirius.utils;

import java.io.File;

public class Settings {
    public static float GRID_WIDTH  = 0.25f;
    public static float GRID_HEIGHT = 0.25f;

    public static final float DEFAULT_FRAME_TIME = 0.25f;

    public static class GameObjects {
        public static final String GENERATED_NAME = "Sprite_Object_Gen";
        public static final float DEFAULT_GAME_OBJECT_SCALE = 0.25f;
        public static final int DEFAULT_FONT_SIZE = 64;
        public static final float DEFAULT_FONT_SCALE = 0.005f;
    }

    public static class Files {
        public static String sourcesDirectory;
        public static String outputDirectory;

        public static final String ANIMATIONS_FOLDER = "assets/animations/";
        public static final String LEVELS_FOLDER = "assets/levels";
        public static final String FONTS_FOLDER = "assets/fonts";
        public static final String FONTS_PROPERTIES_FOLDER = "assets/fonts/cache";
        public static final String SHADERS_FOLDER = "assets/shaders";
        public static final String GUI_VISIBILITY_SETTINGS = "settings/guiSettings.txt";

        // TODO: 14/07/2022 Remove this lines
        // public static final String PIXEL_ARIAL_A_FONT = "assets/fonts/Pixel Arial/Pixel_Arial_A.ttf";
        // public static final String PIXEL_ARIAL_B_FONT = "assets/fonts/Pixel Arial/Pixel_Arial_B.ttf";

        // public static final String MABOOK_FONT = "assets/fonts/Mabook/Mabook.ttf";

        // public static final String PIXEL_VERDANA_FONT = "assets/fonts/PixelFjVerdana/pix-pixelfjverdana12pt.regular.ttf";

        public static final String FOLKS_BOLD_FONT = "assets/fonts/folks/Folks-Bold.ttf";
        public static final String FOLKS_HEAVY_FONT = "assets/fonts/folks/Folks-Heavy.ttf";
        public static final String FOLKS_LIGHT_FONT = "assets/fonts/folks/Folks-Light.ttf";
        public static final String FOLKS_BLACK_FONT = "assets/fonts/folks/FolksBlack.ttf";
        public static final String FOLKS_NORMAL_FONT = "assets/fonts/folks/Folks-Normal.ttf";
        public static final String FOLKS_SHADES_FONT = "assets/fonts/folks/FolksShades.ttf";
        public static final String FOLKS_XXHEAVY_FONT = "assets/fonts/folks/FolksXXHeavy.ttf";

        // public static final String MINERCRAFTORY_FONT = "assets/fonts/minercraftory/Minercraftory.ttf";

        // // TODO: 16/07/2022 Get work around to fix 'p' char in this fonts
        public static final String DYNO_BOLD_FONT = "assets/fonts/dyno_sans/Dyno Bold.ttf";
        public static final String DYNO_BOLD_ITALIC_FONT = "assets/fonts/dyno_sans/Dyno Bold Italic.ttf";
        // public static final String HORROR_FONT = "assets/fonts/horror/HorrorFont-Regular.ttf";

        public static final String DYNO_ITALIC_FONT = "assets/fonts/dyno_sans/Dyno Italic.ttf";
        public static final String DYNO_REGULAR_FONT = "assets/fonts/dyno_sans/Dyno Regular.ttf";

        public static final String DEFAULT_FONT_PATH = FOLKS_LIGHT_FONT;

        /**
         * Locates and saves the sources and the output directories
         */
        public static void searchForSrcAndOutDirectories() {
            File mainFile = Settings.Files.lookForMainFile(new File(System.getProperty("user.dir")));
            File outputMainFile = Settings.Files.lookForClassFile(new File(System.getProperty("user.dir")),
                    mainFile.getName().split(".java")[0]);

            // Transform the slashes, '\' or '/' due to the operating system, into points, '.'
            // Also remove file's extension.
            String parsedMainSrcFilePath = mainFile.getPath()
                    .replace('\\', '/')
                    .replace('/', '.')
                    .split(".java")[0];
            String parsedMainOutFilePath = outputMainFile.getPath()
                    .replace('\\', '/')
                    .replace('/', '.')
                    .split(".class")[0];

            String[] splitParsedMainSrcFilePath = parsedMainSrcFilePath.split("\\.");
            String[] splitParsedMainOutFilePath = parsedMainOutFilePath.split("\\.");

            // Build the path of the output directory
            StringBuilder outputDirPath = new StringBuilder();
            for (String s : splitParsedMainOutFilePath) {
                if (parsedMainSrcFilePath.contains(s)) {
                    continue;
                }

                if (!outputDirPath.isEmpty())
                    outputDirPath.append("/");

                outputDirPath.append(s);
            }

            // Build the path of the sources directory
            StringBuilder sourcesDirPath = new StringBuilder();
            for (String s : splitParsedMainSrcFilePath) {
                if (parsedMainOutFilePath.contains(s)) {
                    continue;
                }

                if (!sourcesDirPath.isEmpty())
                    sourcesDirPath.append("/");

                sourcesDirPath.append(s);
            }

            Settings.Files.outputDirectory = outputDirPath.toString();
            Settings.Files.sourcesDirectory = sourcesDirPath.toString();
        }

        /**
         * Locates the file with the 'main' method.
         * @param currentDirectory Directory where the search is going to begin
         * @return The file with the 'main' method.
         */
        private static File lookForMainFile(File currentDirectory) {
            return Scanner.lookForFile(currentDirectory, file ->
                    Scanner.hasString(file, "public static void main")
                            && !file.getName().equals("Settings.java")
                            && !file.getName().endsWith(".class")
            );
        }

        /**
         * Locates the compiled class with the 'main' method.
         * This method depends on the {@link Settings.Files#lookForMainFile(File)}
         *
         * @param currentDirectory Directory where the search is going to begin
         * @param desiredOutputClassName The name of the file found in {@link Settings.Files#lookForMainFile(File)} method.
         * @return The compiled class with the 'main' method.
         */
        private static File lookForClassFile(File currentDirectory, String desiredOutputClassName) {
            return Scanner.lookForFile(currentDirectory, file ->
                    file.getName().endsWith(desiredOutputClassName + ".class"));
        }
    }
}
