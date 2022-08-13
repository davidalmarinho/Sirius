package sirius.input;

// TODO: 02/08/2022 Probably useless class...
public enum Mods {
    // TODO: 29/07/2022 Test those mods on Linux os
    SHIFT(1),
    CONTROL(2),
    ALT(4),
    HOME(8),
    CONTROL_SHIFT(3),
    CONTROL_ALT(6),
    CONTROL_ALT_SHIT(7),
    ALT_GR(6);

    private final int MOD;

    Mods(int mod) {
        MOD = mod;
    }

    public int get() {
        return MOD;
    }
}
