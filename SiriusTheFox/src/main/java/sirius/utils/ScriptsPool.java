package sirius.utils;

import compiling_tools.InlineCompiler;
import sirius.encode_tools.Encode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ScriptsPool {
    public static String customPropertiesWindowPath;
    public static String customPropertiesWindowScript;

    public static String customPrefabsPath;
    public static String customPrefabsScript;

    public static Map<File, String> componentFileStringMap;

    private static boolean flag;

    static {
        componentFileStringMap = new HashMap<>();
        flag = true;
    }

    public static void searchForComponentsFiles() {
        File srcDir = new File("src");
        loopDirs(srcDir);
    }

    private static void loopDirs(File directory) {
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                // No repeated files to componentScriptList
                if (componentFileStringMap.keySet().stream().anyMatch(s ->
                        s.getPath().equals(file.getPath()) || file.getPath().endsWith("~"))) {
                    continue;
                }

                if (Encode.hasString(file, "extends Component")) {
                    componentFileStringMap.put(file, Encode.readFile(file));
                    if (!flag) {
                        InlineCompiler.printStart();
                        InlineCompiler.compileCode(file);
                        InlineCompiler.printEnd();
                    }
                }

            } else {
                loopDirs(file);
            }
        }

        if (flag) {
            flag = false;
        }
    }
}
