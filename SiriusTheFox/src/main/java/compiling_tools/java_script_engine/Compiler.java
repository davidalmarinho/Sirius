package compiling_tools.java_script_engine;

import ch.obermuhlner.scriptengine.java.JavaCompiledScript;
import ch.obermuhlner.scriptengine.java.JavaScriptEngine;
import sirius.utils.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Compiler {
    public static JavaCompiledScript compile(String filepath) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("java");
            JavaScriptEngine javaScriptEngine = (JavaScriptEngine) engine;

            String script = Scanner.readFile(filepath);
            JavaCompiledScript compiledScript = javaScriptEngine.compile(script);
            // System.out.println("Congrats: Recompiled '" + filepath.replace('\\', '/') + "' script!");
            return compiledScript;
        } catch (ScriptException e) {
            e.printStackTrace();
            System.err.println("Error: Couldn't recompile '" + filepath.replace('\\', '/') + "' script.");
        }
        return null;
    }
}
