package sirius.utils;

import sirius.encode_tools.Encode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScriptsPool {
    public static String customPropertiesWindowPath;
    public static String customPropertiesWindowScript;

    public static String customPrefabsPath;
    public static String customPrefabsScript;

    public static List<File> componentScriptList;

    static {
        componentScriptList = new ArrayList<>();
    }

    public static void searchForComponentsFiles() {
        File srcDir = new File("src");
        loopDirs(srcDir);
    }

    private static void loopDirs(File directory) {
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                // No repeated files to componentScriptList
                if (componentScriptList.stream().anyMatch(s -> s.getPath().equals(file.getPath())))
                    continue;

                if (Encode.hasString(file, "extends Component"))
                    componentScriptList.add(file);

            } else {
                loopDirs(file);
            }
        }
    }
}
