package sirius.levels;

import java.util.ArrayList;
import java.util.List;

public class Levels {
    public static int currentLevel = 0;
    private List<String> levelList;

    public Levels() {
        this.levelList = new ArrayList<>();
    }

    /**
     * Levels MIGHT follow the pattern:
     *     level1.txt
     *     level2.txt
     *     level3.txt
     *     ...
     *
     * @param levelName The level name.
     */
    public void add(String levelName) {
        levelList.add(levelName);
    }
}
