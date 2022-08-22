package sirius.editor.components;

import ch.obermuhlner.scriptengine.java.JavaCompiledScript;
import gameobjects.components.Component;
import java_script_engine.Compiler;
import sirius.InlineCompiler;
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

        boolean noChangesInCustomPropertiesWindow =
                currentScriptCustomPropertiesWindow.equals(ScriptsPool.customPropertiesWindowScript);

        String currentPrefabsScript = Encode.readFile(ScriptsPool.customPrefabsPath);
        boolean noChangesInCustomPrefabs =
                currentPrefabsScript.equals(ScriptsPool.customPrefabsScript);

        if (!window.isFocused() && (!noChangesInCustomPropertiesWindow || !noChangesInCustomPrefabs)) {
            ScriptsPool.customPropertiesWindowScript = currentScriptCustomPropertiesWindow;
            ScriptsPool.customPrefabsScript = currentPrefabsScript;
            mayRecompile = true;
        }

        if (window.isFocused() && mayRecompile) {
            ScriptsPool.searchForComponentsFiles();
            for (int i = 0; i < ScriptsPool.componentScriptList.size(); i++) {
                InlineCompiler.compileCode(ScriptsPool.componentScriptList.get(i));
            }

            // Recompile
            JavaCompiledScript compiledScript = Compiler.compile(ScriptsPool.customPropertiesWindowPath);
            ICustomPropertiesWindow myPropertiesWindow = (ICustomPropertiesWindow) compiledScript.getCompiledInstance();
            SiriusTheFox.get().addCustomizedPropertiesWindow(myPropertiesWindow);

            // JavaCompiledScript prefabsCompiledScript = Compiler.compile(ScriptsPool.customPrefabsPath);
            // ICustomPrefabs customPrefabs = (ICustomPrefabs) prefabsCompiledScript.getCompiledInstance();
            // SiriusTheFox.get().addRuntimeOptionCustomizedPrefabs(customPrefabs);

            mayRecompile = false;
        }
    }
}
