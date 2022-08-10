package sirius.rendering.color;

public enum ColorBlindnessCategories {
    NO_COLOR_BLINDNESS("No color blindness"),
    PROTANOPIA("Protanopia"),
    PROTANOMALY("Protanomaly"),
    DEUTERANOPIA("Deuteranopia"),
    DEUTERANOMALY("Deuteranomaly"),
    TRITANOPIA("Tritanopia"),
    TRITANOMALY("Tritanomaly"),
    ACHROMATOPSIA("Achromatopsia"),
    ACHROMATOMALY("Achromatomaly");

    private final String colorBlindnessCategory;

    ColorBlindnessCategories(String colorBlindessCategory) {
        this.colorBlindnessCategory = colorBlindessCategory;
    }
}
