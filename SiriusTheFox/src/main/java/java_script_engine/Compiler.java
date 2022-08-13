package java_script_engine;

import ch.obermuhlner.scriptengine.java.JavaCompiledScript;
import ch.obermuhlner.scriptengine.java.JavaScriptEngine;
import sirius.SiriusTheFox;
import sirius.editor.imgui.ICustomPropertiesWindow;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

public class Compiler {
    public void compile() {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("java");
            JavaScriptEngine javaScriptEngine = (JavaScriptEngine) engine;

            JavaCompiledScript compiledScript = javaScriptEngine.compile("""
                    
                    """);

            System.out.println(compiledScript.getCompiledInstance());
            ICustomPropertiesWindow myPropertiesWindow = (ICustomPropertiesWindow) compiledScript.getCompiledInstance();
            SiriusTheFox.get().addCustomizedPropertiesWindow(myPropertiesWindow);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}
