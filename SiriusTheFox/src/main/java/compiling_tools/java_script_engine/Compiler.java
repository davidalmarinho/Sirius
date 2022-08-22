package compiling_tools.java_script_engine;

import ch.obermuhlner.scriptengine.java.JavaCompiledScript;
import ch.obermuhlner.scriptengine.java.JavaScriptEngine;
import sirius.encode_tools.Encode;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Compiler {
    public static JavaCompiledScript compile(String filepath) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("java");
            JavaScriptEngine javaScriptEngine = (JavaScriptEngine) engine;

            String script = Encode.readFile(filepath);
            JavaCompiledScript compiledScript = javaScriptEngine.compile(script);
            System.out.println("Congrats: Recompiled '" + filepath + "' script!");
            return compiledScript;
        } catch (ScriptException e) {
            e.printStackTrace();
            System.err.println("Error: Couldn't recompile '" + filepath + "' script.");
        }
        return null;
    }
}
