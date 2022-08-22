package compiling_tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.*;

/**
 * Code based in https://stackoverflow.com/a/21544850
 */
public class InlineCompiler {
    public static void compileCode(File srcJavaFile) {
        if (srcJavaFile.getParentFile().exists() || srcJavaFile.getParentFile().mkdirs()) {
            try {
                // Compilation Requirements
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
                fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File("out/production/SiriusProject")));

                // This sets up the class path that the compiler will use.
                List<String> optionList = new ArrayList<>();
                optionList.add("-classpath");
                optionList.add(System.getProperty("java.class.path") + File.pathSeparator + "dist/InlineCompiler.jar");

                Iterable<? extends JavaFileObject> compilationUnit
                        = fileManager.getJavaFileObjectsFromFiles(List.of(srcJavaFile));

                JavaCompiler.CompilationTask task = compiler.getTask(
                        null,
                        fileManager,
                        diagnostics,
                        optionList,
                        null,
                        compilationUnit);

                if (task.call()) {
                    // Load and execute
                    System.out.println("Congrats: Compiled '" + srcJavaFile.getPath() + "' script!");

                    CustomClassLoader classLoader = new CustomClassLoader();
                    String packageAndClass = srcJavaFile.getPath()
                            .split(".java")[0]
                            .replace('\\', '/')
                            .replace('/', '.')
                            .replace("src.", "");
                    classLoader.findClass(packageAndClass);

                } else {
                    // TODO: 22/08/2022 Popup window to show the error
                    System.err.println("⚠ Oh no! Something when wrong! ⚠");
                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                        String errInfo = String
                                .format("Error on line %d in %s%n",
                                        diagnostic.getLineNumber(),
                                        diagnostic.getSource().toUri());
                        System.err.format(errInfo);
                        String err = diagnostic.getMessage(null);
                        System.err.println(err);
                    }
                }
                fileManager.close();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
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
