package sirius.levels;

public class Level {
    public static int currentLevel = 1;

    private String levelName;
    private String levelPath;
    private int id = 0;

    public Level(String levelName, String levelPath, int currentLevel) {
        this.levelName = levelName;
        this.levelPath = levelPath;
        this.id = currentLevel;
    }

    public String getName() {
        return levelName;
    }

    public String getPath() {
        return levelPath;
    }

    public int getId() {
        return id;
    }
}
