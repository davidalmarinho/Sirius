package sirius.utils;

import java.io.File;

public class JavaFile {
    public final File FILE;
    public String script;
    public boolean compile;
    public boolean delete;

    public JavaFile(String scriptPath, String script) {
        this.FILE = new File(scriptPath);
        this.script = script;
    }

    public JavaFile(File file, String script) {
        this(file.getPath(), script);
    }
}
