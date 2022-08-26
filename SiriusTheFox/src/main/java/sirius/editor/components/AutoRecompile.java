package sirius.editor.components;

import ch.obermuhlner.scriptengine.java.JavaCompiledScript;
import compiling_tools.InlineCompiler;
import gameobjects.components.Component;
import sirius.SiriusTheFox;
import sirius.Window;
import sirius.editor.imgui.ICustomPrefabs;
import sirius.editor.imgui.ICustomPropertiesWindow;
import sirius.scenes.ISceneInitializer;
import sirius.utils.JavaFile;
import sirius.utils.Pool;
import sirius.utils.Scanner;
import sirius.utils.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AutoRecompile extends Component {
    private boolean firstTimeRunning;
    private boolean compile;

    public AutoRecompile() {
        this.firstTimeRunning = true;
        this.compile = true;
    }

    private void searchForNewComponents() {
        // Look for all the files that has '.java' extension and has 'extends Component' hierarchy
        File[] possibleJavaFiles = Scanner.lookForFiles(new File(Settings.Files.sourcesDirectory), javaFile ->
                javaFile.getPath().endsWith(".java"));

        // No files have been found, so doesn't exist any component
        if (possibleJavaFiles == null) {
            return;
        }

        // Verifies if the files which have been found have been added already and if not, they will be added
        List<JavaFile> javaFileList = Pool.Scripts.javaFileList;
        for (File file : possibleJavaFiles) {
            if (javaFileList.stream().noneMatch(javaFile -> javaFile.FILE.getPath().equals(file.getPath()))) {
                JavaFile jFile = new JavaFile(file, Scanner.readFile(file));
                if (!firstTimeRunning) {
                    jFile.compile = true;
                }
                javaFileList.add(jFile);
            }
        }
    }

    // Verifies which java files needs to be compiled
    private boolean jFilesNeedCompilation() {
        for (JavaFile javaFile : Pool.Scripts.javaFileList) {
            if (!javaFile.script.equals(Scanner.readFile(javaFile.FILE))) {
                javaFile.compile = true;
            }
        }

        return Pool.Scripts.javaFileList.stream().anyMatch(jFile -> jFile.compile);
    }

    // Compiles the java files that have been modified
    private void compileJavaFiles() {
        List<JavaFile> fileToCompileList = new ArrayList<>();
        for (JavaFile javaFile : Pool.Scripts.javaFileList) {
            if (javaFile.compile) {
                javaFile.compile = false;
                javaFile.script = Scanner.readFile(javaFile.FILE);
                fileToCompileList.add(javaFile);
            }
        }

        JavaFile[] filesToCompile = new JavaFile[fileToCompileList.size()];
        for (int i = 0; i < fileToCompileList.size(); i++) {
            filesToCompile[i] = fileToCompileList.get(i);
        }

        InlineCompiler.compileCode(filesToCompile, file -> {
            if (file.getAbsolutePath().equals(Pool.Scripts.customPropertiesWindowAbsolutePath)) {
                JavaCompiledScript compiledScript = compiling_tools.java_script_engine.Compiler.compile(Pool.Scripts.customPropertiesWindowAbsolutePath);
                ICustomPropertiesWindow myPropertiesWindow = (ICustomPropertiesWindow) compiledScript.getCompiledInstance();
                SiriusTheFox.get().addCustomizedPropertiesWindow(myPropertiesWindow);
            } else if (file.getAbsolutePath().equals(Pool.Scripts.customPrefabsAbsolutePath)) {
                JavaCompiledScript compiledScript = compiling_tools.java_script_engine.Compiler.compile(Pool.Scripts.customPrefabsAbsolutePath);
                ICustomPrefabs iCustomPrefabs = (ICustomPrefabs) compiledScript.getCompiledInstance();
                SiriusTheFox.get().addRuntimeOptionCustomizedPrefabs(iCustomPrefabs);
            } else if (file.getAbsolutePath().equals(Pool.Scripts.customLvlSceneInitAbsolutePath)) {
                JavaCompiledScript compiledScript = compiling_tools.java_script_engine.Compiler.compile(Pool.Scripts.customLvlSceneInitAbsolutePath);
                ISceneInitializer sceneInitializer = (ISceneInitializer) compiledScript.getCompiledInstance();
                SiriusTheFox.get().addCustomLevelSceneInitializer(sceneInitializer);
            }
        });
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
                if (jFilesNeedCompilation()) {
                    InlineCompiler.printStart();
                    compileJavaFiles();
                    InlineCompiler.printEnd();
                }
                compile = false;
            }
        } else {
            compile = true;
        }
    }
}
