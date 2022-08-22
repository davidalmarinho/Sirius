package sirius.editor.components;

import ch.obermuhlner.scriptengine.java.JavaCompiledScript;
import gameobjects.components.Component;
import compiling_tools.java_script_engine.Compiler;
import compiling_tools.InlineCompiler;
import sirius.SiriusTheFox;
import sirius.Window;
import sirius.editor.imgui.ICustomPropertiesWindow;
import sirius.encode_tools.Encode;
import sirius.input.KeyListener;
import sirius.utils.ScriptsPool;

import java.io.File;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;

public class AutoRecompile extends Component {
    private boolean mayRecompileComponents;
    private boolean mayRecompileInterfaces;

    @Override
    public void editorUpdate(float dt) {
        Window window = SiriusTheFox.getWindow();
        String currentScriptCustomPropertiesWindow = Encode.readFile(ScriptsPool.customPropertiesWindowPath);

        String currentPrefabsScript = Encode.readFile(ScriptsPool.customPrefabsPath);

        boolean immutableCustomPropertiesWindow =
                currentScriptCustomPropertiesWindow.equals(ScriptsPool.customPropertiesWindowScript);

        boolean immutableCustomPrefabs =
                currentPrefabsScript.equals(ScriptsPool.customPrefabsScript);

        ScriptsPool.searchForComponentsFiles();

        if (KeyListener.isKeyDown(GLFW_KEY_P)) {
            ScriptsPool.componentFileStringMap.keySet().forEach(f -> System.out.println(f.getPath()));
        }

        if (!window.isFocused()) {
            if (!immutableCustomPropertiesWindow || !immutableCustomPrefabs) {
                ScriptsPool.customPropertiesWindowScript = currentScriptCustomPropertiesWindow;
                ScriptsPool.customPrefabsScript = currentPrefabsScript;
                mayRecompileInterfaces = true;
            }

            // Check if there is some component that needs to be recompiled
            for (Map.Entry<File, String> pair : ScriptsPool.componentFileStringMap.entrySet()) {
                String scriptInScriptsPool = pair.getValue();
                String currentScript = Encode.readFile(pair.getKey());
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
                ScriptsPool.componentFileStringMap.entrySet()
                        .forEach(pair -> {
                            String scriptInScriptsPool = pair.getValue();
                            String currentScript = Encode.readFile(pair.getKey());
                            if (!scriptInScriptsPool.equals(currentScript)) {
                                pair.setValue(currentScript);
                                InlineCompiler.compileCode(pair.getKey());
                            }
                        });
            }

            if (mayRecompileInterfaces) {
                // Recompile
                JavaCompiledScript compiledScript = Compiler.compile(ScriptsPool.customPropertiesWindowPath);
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
