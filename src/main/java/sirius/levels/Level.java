package sirius.levels;

import com.sun.istack.internal.NotNull;

public class Level {
    public static int currentLevel = 0;
    private String customName;
    @NotNull
    private String levelName;

    public Level(String levelName) {
        this.customName = levelName;
        this.levelName = "level" + currentLevel + ".txt";
    }

    public Level(String levelName, int currentLevel) {
        this.customName = levelName;
        this.levelName = "level" + currentLevel + ".txt";
    }

    public String getCustomLevelName() {
        return customName;
    }
}
