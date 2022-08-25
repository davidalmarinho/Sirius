package sirius.editor.components;

import gameobjects.components.Component;
import sirius.SiriusTheFox;
import sirius.Window;
import sirius.utils.JavaFile;
import sirius.utils.Pool;
import sirius.utils.Scanner;
import sirius.utils.Settings;

import java.io.File;
import java.util.List;

public class AutoRecompile extends Component {
    private boolean firstTimeRunning;
    private boolean compile;

    public AutoRecompile() {
        this.firstTimeRunning = true;
        compile = true;
    }

    private void searchForNewComponents() {
        // Look for all the files that has '.java' extension and has 'extends Component' hierarchy
        File[] possibleJavaFiles = Scanner.lookForFiles(new File(Settings.Files.sourcesDirectory), javaFile ->
                javaFile.getPath().endsWith(".java"));

        // No files have been found, so doesn't exist any component
        if (possibleJavaFiles == null) {
            return;
        }

        // Verify if the files which have been found have been added already and if not, it will be added
        List<JavaFile> javaFileList = Pool.Scripts.javaFileList;
        for (File file : possibleJavaFiles) {
            if (javaFileList.stream().noneMatch(javaFile -> javaFile.FILE.getPath().equals(file.getPath()))) {
                Pool.Scripts.javaFileList.add(new JavaFile(file, Scanner.readFile(file)));
                // if (!firstTimeRunning)
                    System.out.println("Added component:" + file.getPath());
            }
        }
    }

    private void compileComponents() {
        // Verify which java files needs to be compiled
        for (JavaFile javaFile : Pool.Scripts.javaFileList) {
            if (!javaFile.script.equals(Scanner.readFile(javaFile.FILE))) {
                javaFile.compile = true;
            }
        }
    }

    @Override
    public void editorUpdate(float dt) {
        Window window = SiriusTheFox.getWindow();

        // Add all the components that have been compiled when the program started running
        if (firstTimeRunning) {
            searchForNewComponents();
            firstTimeRunning = false;
        }

        if (window.isFocused()) {
            if (compile) {
                searchForNewComponents();
                // compileComponents();
                compile = false;
            }
        } else {
            compile = true;
        }
    }
}
