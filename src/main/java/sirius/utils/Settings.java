package sirius.utils;

public class Settings {
    public static float GRID_WIDTH  = 0.25f;
    public static float GRID_HEIGHT = 0.25f;

    public static final float DEFAULT_FRAME_TIME = 0.25f;

    public static class GameObjects {
        public static final String GENERATED_NAME = "Sprite_Object_Gen";
        public static final int DEFAULT_FONT_SIZE = 64;
        public static final float DEFAULT_FONT_SCALE = 0.05f;
    }

    public static class Files {
        public static final String ANIMATIONS_FOLDER = "assets/animations/";
        public static final String LEVELS_FOLDER = "assets/levels";
        public static final String FONTS_FOLDER = "assets/fonts";
        public static final String FONTS_PROPERTIES_FOLDER = "assets/fonts/cache";
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

        public static final String CURRENT_FONT_PATH = FOLKS_LIGHT_FONT;
    }
}
