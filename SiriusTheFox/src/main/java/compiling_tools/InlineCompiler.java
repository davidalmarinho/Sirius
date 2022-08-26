package compiling_tools;

import sirius.utils.JavaFile;
import sirius.utils.Settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.tools.*;

/**
 * Code based in https://stackoverflow.com/a/21544850
 */
public class InlineCompiler {
    public static void compileCode(JavaFile[] srcJavaFiles) {
        File[] files = new File[srcJavaFiles.length];
        for (int i = 0; i < srcJavaFiles.length; i++) {
            files[i] = srcJavaFiles[i].FILE;
        }

        boolean val = true;
        for (File f : files) {
            if (!(f.getParentFile().exists() || f.getParentFile().mkdirs())) {
                val = false;
            }
        }

        if (val) {
            try {
                // Compilation Requirements
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
                fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(new File(Settings.Files.outputDirectory)));

                // This sets up the class path that the compiler will use.
                List<String> optionList = new ArrayList<>();
                optionList.add("-classpath");
                optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/InlineCompiler.jar");

                Iterable<? extends JavaFileObject> compilationUnit
                        = fileManager.getJavaFileObjectsFromFiles(List.of(files));

                JavaCompiler.CompilationTask task = compiler.getTask(
                        null,
                        fileManager,
                        diagnostics,
                        optionList,
                        null,
                        compilationUnit);

                if (task.call()) {
                    // Load and execute
                    for (int i = 0; i < files.length; i++) {
                        System.out.println("Congrats: Compiled '" + files[i].getPath()
                                .replace('\\', '/') + "' script!");

                        CustomClassLoader classLoader = new CustomClassLoader();
                        String prefix = Settings.Files.sourcesDirectory.replace('/', '.') + ".";
                        String packageAndClass = files[i].getPath()
                                .split(".java")[0]
                                .replace('\\', '/')
                                .replace('/', '.')
                                .replace(prefix, "");
                        classLoader.findClass(packageAndClass);
                    }
                } else {
                    // TODO: 22/08/2022 Popup window to show the error
                    System.err.println("⚠ Oh no! Something went wrong! ⚠");
                    System.err.println("Fix the error so we can compile all the modified scripts :)");
                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {

                        // Files still need to be compiled, so they will be marked to be compiled
                        for (JavaFile jFile : srcJavaFiles) {
                            jFile.compile = true;
                        }

                        if (diagnostic != null) {
                            String errInfo = String
                                    .format("Error on line %d in %s%n",
                                            diagnostic.getLineNumber(),
                                            diagnostic.getSource().toUri());

                            System.err.format(errInfo);
                            String err = diagnostic.getMessage(null);
                            System.err.println(err);
                        }
                    }
                }
                fileManager.close();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }

    private static void printSuccessfulCompilations(File[] srcJavaFile) {

    }

    public static void printStart() {
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        System.out.println(">> Starting compilation task...\n");
    }

    public static void printEnd() {
        System.out.println("\n>> Ended compilation task.");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
    }
}
