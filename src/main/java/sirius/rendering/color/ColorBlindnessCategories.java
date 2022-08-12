package sirius.rendering.color;

public enum ColorBlindnessCategories {
    NO_COLOR_BLINDNESS("No color blindness"),
    PROTANOPIA_A("Protanopia A"),
    PROTANOPIA_B("Protanopia B"),
    PROTANOMALY("Protanomaly"),
    DEUTERANOPIA_A("Deuteranopia A"),
    DEUTERANOPIA_B("Deuteranopia B"),
    DEUTERANOMALY("Deuteranomaly"),
    TRITANOPIA_A("Tritanopia A"),
    TRITANOPIA_B("Tritanopia B"),
    TRITANOMALY("Tritanomaly"),
    ACHROMATOPSIA("Achromatopsia"),
    ACHROMATOMALY("Achromatomaly");

    private final String colorBlindnessCategory;

    ColorBlindnessCategories(String colorBlindnessCategory) {
        this.colorBlindnessCategory = colorBlindnessCategory;
    }

    public String getColorBlindnessCategory() {
        return colorBlindnessCategory;
    }
}
