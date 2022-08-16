package sirius.editor.components;

import ch.obermuhlner.scriptengine.java.JavaCompiledScript;
import gameobjects.components.Component;
import java_script_engine.Compiler;
import sirius.SiriusTheFox;
import sirius.Window;
import sirius.editor.imgui.ICustomPropertiesWindow;
import sirius.encode_tools.Encode;
import sirius.utils.ScriptsPool;

public class AutoRecompile extends Component {
    private boolean mayRecompile;

    @Override
    public void editorUpdate(float dt) {
        Window window = SiriusTheFox.getWindow();
        String currentScriptCustomPropertiesWindow = Encode.readFile(ScriptsPool.customPropertiesWindowPath);
        boolean sameContentCustomPropertiesWindow =
                currentScriptCustomPropertiesWindow.equals(ScriptsPool.customPropertiesWindowScript);
        if (!window.isFocused() && !sameContentCustomPropertiesWindow) {
            ScriptsPool.customPropertiesWindowScript = currentScriptCustomPropertiesWindow;
            mayRecompile = true;
        }

        if (window.isFocused() && mayRecompile) {
            // Recompile
            JavaCompiledScript compiledScript = Compiler.compile(ScriptsPool.customPropertiesWindowPath);
            ICustomPropertiesWindow myPropertiesWindow = (ICustomPropertiesWindow) compiledScript.getCompiledInstance();
            SiriusTheFox.get().addCustomizedPropertiesWindow(myPropertiesWindow);

            mayRecompile = false;
        }
    }
}
