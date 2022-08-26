package sirius.utils;

public class OperatingSystem {
    public static boolean isWindows() {
        return System.getProperty("os.name").equals("Windows");
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").equals("Linux");
    }

    public static boolean checkOS(String os) {
        return os.equals(System.getProperty("os.name"));
    }
}
