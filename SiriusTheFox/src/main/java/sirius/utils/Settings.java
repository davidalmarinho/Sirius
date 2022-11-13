package sirius.utils;

import sirius.encode_tools.Encode;

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
        public static String mainSrcFilePath;
        public static String mainSrcFileName;
        public static String mainOutFilePath;
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
         * Tracks and saves the path and the name of the file that contains the main method.
         * They are saved in {@link Settings.Files#mainSrcFilePath} variable
         * and in the {@link Settings.Files#mainSrcFileName} variable.
         */
        public static void trackSrcMainFile() {
            trackSrcMainFile(false);
        }

        /**
         * Tracks and saves the path and the name of the file that contains the main method.
         * They are saved in {@link Settings.Files#mainSrcFilePath} variable
         * and in the {@link Settings.Files#mainSrcFileName} variable.
         * @param forceSearch Mark as true if you want to research for the file.
         */

        public static void trackSrcMainFile(boolean forceSearch) {
            if (!(Files.mainSrcFileName == null || Files.mainSrcFilePath == null
                    || Files.mainSrcFileName.isBlank() || Files.mainSrcFilePath.isBlank())) {
                return;
            }

            File mainFile = Scanner.lookForFile(new File(System.getProperty("user.dir")), file ->
                    Scanner.hasString(file, "public static void main")
                            && !file.getName().equals("Settings.java")
                            && !file.getName().endsWith(".class")
            );

            if (mainFile == null) {
                // todo ERROR
            }

            Files.mainSrcFilePath = mainFile.getPath();
            Files.mainSrcFileName = mainFile.getName();
        }

        /**
         * Transform the slashes, '\' or '/' due to the operating system, into points, '.'
         * Also removes file's extension.
         *
         * @param str String that will be transformed
         * @param ext Extension that is needed to be removed
         * @return The String with the pretended transformations.
         */
        public static String parseMainFile(String str, String ext) {
            return str
                    .replace('\\', '/')
                    .replace('/', '.')
                    .split(ext)[0];
        }

        /**
         * Tracks and saves the output directory in {@link Settings.Files#outputDirectory} variable
         */
        public static void trackOutputDir() {
            Files.mainOutFilePath = Settings.Files.lookForClassFile(new File(System.getProperty("user.dir")),
                    Files.mainSrcFileName.split(".java")[0]).getPath();

            String parsedMainOutFilePath = parseMainFile(Files.mainOutFilePath, ".class");
            String[] splitParsedMainSrcFilePath = parseMainFile(Files.mainSrcFilePath, ".java").split("\\.");
            String[] splitParsedMainOutFilePath = parsedMainOutFilePath.split("\\.");

            // Build the path of the output directory
            StringBuilder outputDirPath = new StringBuilder();

            int comparator = 0;
            boolean useOtherLogic = false;
            String outDirAndOutMainDir = "";

            /* Here we are going to exclude all the paths before.
             * Ex.: Given 'home/user/documents/Project/out/production'
             *      We stay with 'Project/out/production'
             *
             * At this point, userOtherLogic boolean becomes true, and we now use the other way of comparation
             */
            for (String curIndexContent : splitParsedMainOutFilePath) {
                if (!useOtherLogic) {
                    if (!curIndexContent.equals(splitParsedMainSrcFilePath[comparator])) {
                        outputDirPath.append(curIndexContent);
                        useOtherLogic = true;

                        outDirAndOutMainDir = Encode.join(Encode.cutArrString(splitParsedMainSrcFilePath, comparator), '/');
                    }

                    comparator++;

                } else {
                    if (!outDirAndOutMainDir.contains(curIndexContent)) {
                        outputDirPath.append("/");
                        outputDirPath.append(curIndexContent);
                    }
                }
            }

            Settings.Files.outputDirectory = outputDirPath.toString();
        }

        /**
         * Locates and saves the sources directory in {@link Settings.Files#sourcesDirectory}.
         */
        public static void trackSourcesDir() {
            String parsedMainSrcFilePath = parseMainFile(Files.mainSrcFilePath, ".java");

            String[] splitParsedMainSrcFilePath = parsedMainSrcFilePath.split("\\.");

            // Build the path of the sources directory
            StringBuilder sourcesDirPath = new StringBuilder();
            for (String s : splitParsedMainSrcFilePath) {
                if (parseMainFile(Files.mainOutFilePath, ".class").contains(s)) {
                    continue;
                }

                if (!sourcesDirPath.isEmpty())
                    sourcesDirPath.append("/");

                sourcesDirPath.append(s);
            }

            Settings.Files.sourcesDirectory = sourcesDirPath.toString();
        }

        /**
         * Locates the compiled class with the 'main' method.
         * This method depends on the {@link Files#trackSrcMainFile()}
         *
         * @param currentDirectory Directory where the search is going to begin
         * @param desiredOutputClassName The name of the file found in {@link Files#trackSrcMainFile()} (File)} method.
         * @return The compiled class with the 'main' method.
         */
        private static File lookForClassFile(File currentDirectory, String desiredOutputClassName) {
            return Scanner.lookForFile(currentDirectory, file ->
                    file.getName().endsWith(desiredOutputClassName + ".class"));
        }
    }
}
