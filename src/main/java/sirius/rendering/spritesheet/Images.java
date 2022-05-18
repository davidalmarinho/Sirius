package sirius.rendering.spritesheet;

public enum Images {
    SPRITE_SHEET("spritesheet.png"),
    PLAYER_BIG_SPRITE_SHEET("bigSpritesheet.png"),
    PIPES("pipes.png"),
    TURTLE("turtle.png"),
    BLEND_IMAGE_1("blendImage1.png"),
    BLEND_IMAGE_2("blendImage2.png"),
    TEST_IMAGE("testImage.png"),
    TEST_IMAGE_2("testImage2.png"),
    GIZMOS("gizmos.png"),
    ITEMS("items.png"),
    DECORATIONS_AND_BLOCKS("decorationsAndBlocks.png"),
    ICONS("icons.png");

    private final String spritesheet;
    // Keeps the default spritesheets' path
    private final String PATH_SPRITESHEET = "assets/images/spritesheets/";
    // Keeps the default textures' path
    private final String PATH_TEXTURES = "assets/images/";

    Images(String spritesheet) {
        this.spritesheet = spritesheet;
    }

    /**
     * Gets the spritesheet's path
     * @return the spritesheet's path
     */
    public String getSpritesheet() {
        return PATH_SPRITESHEET + spritesheet;
    }

    /**
     * Gets the texture's path
     * @return the texture's path
     */
    public String getTexture() {
        return PATH_TEXTURES + spritesheet;
    }
}
