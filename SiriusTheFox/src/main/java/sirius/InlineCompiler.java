package sirius;

import sirius.encode_tools.Encode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.*;

/**
 * Code based in https://stackoverflow.com/a/21544850
 */
public class InlineCompiler {
    public static void compileCode(File srcJavaFile) {
        // File srcJavaFile = new File(srcFile);
        String script = Encode.readFile(srcJavaFile.getPath());

        if (srcJavaFile.getParentFile().exists() || srcJavaFile.getParentFile().mkdirs()) {
            try {
                Writer writer = null;
                try {
                    //writer = new FileWriter(srcJavaFile);
                    //writer.write(script);
                    //writer.flush();
                } finally {
                    try {
                        // writer.close();
                    } catch (Exception e) {
                    }
                }


                // Compilation Requirements
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
                fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File("out/production/SiriusProject")));

                // This sets up the class path that the compiler will use.
                // I've added the .jar file that contains the DoStuff interface within in it...
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
                    System.out.println("Compiled '" + srcJavaFile.getPath() + "' with successfully!");

                    /*
                    // Create a new custom class loader, pointing to the directory that contains the compiled
                    // classes, this should point to the top of the package structure!
                    URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()});
                    // Load the class from the classloader by name....
                    Class<?> loadedClass = classLoader.loadClass("components.HelloWorld");
                    // Create a new instance...
                    Object obj = loadedClass.newInstance();
                    // Santity check
                    if (obj instanceof DoStuff) {
                        // Cast to the DoStuff interface
                        DoStuff stuffToDo = (DoStuff)obj;
                        // Run it baby
                        stuffToDo.doStuff();
                    }
                    */

                    CustomClassLoader classLoader = new CustomClassLoader();
                    System.out.println("CLASSPATH");
                    System.out.println(srcJavaFile.getPath());

                    String packageAndClass = srcJavaFile.getPath()
                            .split(".java")[0]
                            .replace('\\', '/')
                            .replace('/', '.')
                            .replace("src.", "");
                    System.out.println(packageAndClass);
                    System.out.println("--CLASSPATH");

                    classLoader.findClass(packageAndClass);

                } else {
                    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                        System.out.format("Error on line %d in %s%n",
                                diagnostic.getLineNumber(),
                                diagnostic.getSource().toUri());
                    }
                }
                fileManager.close();
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }
}
