package sirius.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private static List<File> recursiveFileList;

    static {
        recursiveFileList = new ArrayList<>();
    }

    public interface IConditions {
        boolean isConditionRespected(File file);
    }

    /**
     * Reads a file.
     *
     * @param filePath Path to the saved file.
     * @return All the content of the file in a String.
     */
    public static String readFile(String filePath) {
        File file = new File(filePath);
        String inFile = "";
        if (file.exists()) {
            try {
                inFile = new String(Files.readAllBytes(Paths.get(file.getPath())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return inFile;
    }

    /**
     * Reads a file.
     *
     * @param file The file itself.
     * @return All the content of the file in a String.
     */
    public static String readFile(File file) {
        return readFile(file.getPath());
    }

    /**
     * Checks if the content of a file has a sequence of characters.
     *
     * @param file The file that has the desired content
     * @param str The characters sequence that will be checked if exists in file's content.
     * @return true, if the characters sequence exists in file's content.
     */
    public static boolean hasString(File file, String str) {
        String content = readFile(file.getPath());
        return content.contains(str);
    }

    public static File lookForFile(File currentDirectory, IConditions iConditions) {
        Scanner.loopInDirs(currentDirectory, iConditions);

        if (recursiveFileList.get(0) != null) {
            File returnFile = new File(recursiveFileList.get(0).getPath());
            recursiveFileList.clear();
            return returnFile;
        }

        return null;
    }

    public static File[] lookForFiles(File currentDirectory, IConditions iConditions) {
        Scanner.loopInDirs(currentDirectory, iConditions);

        if (!recursiveFileList.isEmpty()) {
            File[] returnFiles = new File[recursiveFileList.size()];
            for (int i = 0; i < recursiveFileList.size(); i++) {
                returnFiles[i] = new File(recursiveFileList.get(i).getPath());
            }
            recursiveFileList.clear();
            return returnFiles;
        }

        return null;
    }

    private static void loopInDirs(File currentDirectory, IConditions iConditions) {
        File[] listFiles = currentDirectory.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                loopInDirs(file, iConditions);
            } else {
                // Check if all custom parameters are respected
                if (iConditions.isConditionRespected(file)) {
                    recursiveFileList.add(file);
                }
            }
        }
    }
}
