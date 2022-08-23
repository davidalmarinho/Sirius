package sirius.editor.components;

import ch.obermuhlner.scriptengine.java.JavaCompiledScript;
import gameobjects.components.Component;
import compiling_tools.java_script_engine.Compiler;
import compiling_tools.InlineCompiler;
import sirius.SiriusTheFox;
import sirius.Window;
import sirius.editor.imgui.ICustomPropertiesWindow;
import sirius.utils.Pool;
import sirius.utils.Scanner;

import java.io.File;
import java.util.Map;

public class AutoRecompile extends Component {
    private boolean mayRecompileComponents;
    private boolean mayRecompileInterfaces;

    @Override
    public void editorUpdate(float dt) {
        Window window = SiriusTheFox.getWindow();
        String currentScriptCustomPropertiesWindow = Scanner.readFile(Pool.Scripts.customPropertiesWindowPath);

        String currentPrefabsScript = Scanner.readFile(Pool.Scripts.customPrefabsPath);

        boolean immutableCustomPropertiesWindow =
                currentScriptCustomPropertiesWindow.equals(Pool.Scripts.customPropertiesWindowScript);

        boolean immutableCustomPrefabs =
                currentPrefabsScript.equals(Pool.Scripts.customPrefabsScript);

        Pool.Scripts.searchForComponentsFiles();

        if (!window.isFocused()) {
            if (!immutableCustomPropertiesWindow || !immutableCustomPrefabs) {
                Pool.Scripts.customPropertiesWindowScript = currentScriptCustomPropertiesWindow;
                Pool.Scripts.customPrefabsScript = currentPrefabsScript;
                mayRecompileInterfaces = true;
            }

            // Check if there is some component that needs to be recompiled
            for (Map.Entry<File, String> pair : Pool.Scripts.componentFileStringMap.entrySet()) {
                String scriptInScriptsPool = pair.getValue();
                String currentScript = Scanner.readFile(pair.getKey());
                if (!scriptInScriptsPool.equals(currentScript)) {
                    mayRecompileComponents = true;
                    break;
                }
            }
        }

        if (window.isFocused()) {
            if (mayRecompileComponents || mayRecompileInterfaces) {
                InlineCompiler.printStart();
            }

            if (mayRecompileComponents) {
                Pool.Scripts.componentFileStringMap.entrySet()
                        .forEach(pair -> {
                            String scriptInScriptsPool = pair.getValue();
                            String currentScript = Scanner.readFile(pair.getKey());
                            if (!scriptInScriptsPool.equals(currentScript)) {
                                pair.setValue(currentScript);
                                InlineCompiler.compileCode(pair.getKey());
                            }
                        });
            }

            if (mayRecompileInterfaces) {
                // Recompile
                JavaCompiledScript compiledScript = Compiler.compile(Pool.Scripts.customPropertiesWindowPath);
                ICustomPropertiesWindow myPropertiesWindow = (ICustomPropertiesWindow) compiledScript.getCompiledInstance();
                SiriusTheFox.get().addCustomizedPropertiesWindow(myPropertiesWindow);

                // JavaCompiledScript prefabsCompiledScript = Compiler.compile(ScriptsPool.customPrefabsPath);
                // ICustomPrefabs customPrefabs = (ICustomPrefabs) prefabsCompiledScript.getCompiledInstance();
                // SiriusTheFox.get().addRuntimeOptionCustomizedPrefabs(customPrefabs);
            }

            if (mayRecompileComponents || mayRecompileInterfaces) {
                mayRecompileInterfaces = false;
                mayRecompileComponents = false;
                InlineCompiler.printEnd();
            }
        }
    }
}
