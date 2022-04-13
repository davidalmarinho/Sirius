package jade;

import jade.editor.GameViewWindow;
import jade.editor.MenuBar;
import jade.editor.PropertiesWindow;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import jade.editor.SceneHierarchy;
import jade.input.KeyListener;
import jade.input.MouseListener;
import jade.rendering.PickingTexture;
import jade.scenes.Scene;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ImGuiLayer {
    private long glfwWindow;

    // LWJGL3 renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3;
    private final ImGuiImplGlfw imGuiGlfw;

    private GameViewWindow gameViewWindow;
    private final PropertiesWindow propertiesWindow;
    private MenuBar menuBar;
    private SceneHierarchy sceneHierarchy;

    public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
        this.imGuiGl3 = new ImGuiImplGl3();
        this.imGuiGlfw = new ImGuiImplGlfw();
        this.glfwWindow = glfwWindow;
        this.gameViewWindow = new GameViewWindow();
        this.propertiesWindow = PropertiesWindow.get(pickingTexture);
        this.menuBar = new MenuBar();
        this.sceneHierarchy = new SceneHierarchy();
    }

    public void edit(long glfwWindow) {
        this.glfwWindow = glfwWindow;
        this.gameViewWindow = new GameViewWindow();
    }

    // Initialize Dear ImGui.
    public void initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        // io.setIniFilename(null); // We don't want to save .ini file
        io.setIniFilename("imgui.ini"); // We want to save .ini file
        // "imgui.ini will save the last Window's position when we reopen the program"
        // io.setConfigFlags(ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.ViewportsEnable); // Enable docking
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable); // Enable docking
        io.setBackendPlatformName("imgui_java_impl_glfw");

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input

        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            if (!io.getWantCaptureKeyboard()) {
                KeyListener.keyCallback(w, key, scancode, action, mods);
            }
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }

            // Set a personalized callback when we are with the cursor outside an ImGui window
            if (!io.getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
                MouseListener.mouseButtonCallback(w, button, action, mods);
            }
        });

        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);

            // Just use the personalized scroll when inside of game viewport window
            boolean insideGameViewportX = MouseListener.getX() > gameViewWindow.getLeftX()
                    && MouseListener.getX() < gameViewWindow.getRightX();
            boolean insideGameViewportY = MouseListener.getY() > gameViewWindow.getBottomY()
                    && MouseListener.getY() < gameViewWindow.getTopY();

            if (insideGameViewportX && insideGameViewportY)
                MouseListener.mouseScrollCallback(w, xOffset, yOffset);
        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindow, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(glfwWindow);
                if (clipboardString != null) {
                    return clipboardString;
                } else {
                    return "";
                }
            }
        });

        // ------------------------------------------------------------
        // Fonts configuration
        // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

        // ImFontAtlas is like a spritesheet
        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        // Glyphs could be added per-font as well as per config used globally like here
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        // Fonts merge example
        // fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontFromFileTTF("assets/fonts/verdana.ttf", 26, fontConfig);

        // fontAtlas.addFontFromMemoryTTF(loadFromResources("basis33.ttf"), 16, fontConfig);

        fontConfig.destroy(); // After all fonts were added we don't need this config more


        // ------------------------------------------------------------
        // Use freetype instead of stb_truetype to build a fonts texture

        // Method initializes LWJGL3 renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGlfw.init(glfwWindow, false); // False because we already have set callbacks before
        imGuiGl3.init("#version 330 core");
    }

    public void update(float dt, Scene currentScene) {
        startFrame();

        // Any Dear ImGui code SHOULD go between ImGui.newFrame()/ImGui.render() methods
        setupDockSpace();
        currentScene.imgui();
        gameViewWindow.imgui();
        propertiesWindow.imgui();
        sceneHierarchy.imgui();

        // We have to end ImGui before we render ImGui
        endFrame();
    }

    private void startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    private void endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, SiriusTheFox.getWindow().getWidth(), SiriusTheFox.getWindow().getHeight());
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        ImGui.render();

        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        // long backupWindowPtr = glfwGetCurrentContext();
        // ImGui.updatePlatformWindows();
        // ImGui.renderPlatformWindowsDefault();
        // glfwMakeContextCurrent(backupWindowPtr);
    }

    // If you want to clean a room after yourself - do it by yourself
    public void destroyImGui() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }

    private void setupDockSpace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        // Making Docking panel the main viewport
        // ImGuiViewport mainViewport = ImGui.getMainViewport();
        // ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
        // ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        // ImGui.setNextWindowViewport(mainViewport.getID());

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(SiriusTheFox.getWindow().getWidth(), SiriusTheFox.getWindow().getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        // |= same as doing windowFlags = windowFlags | ImGuiWindowFlags.NoTileBar...
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse
                | ImGuiWindowFlags.NoResize
                | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoBringToFrontOnFocus;

        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));

        menuBar.imgui();

        ImGui.end();
    }

    public PropertiesWindow getPropertiesWindow() {
        return propertiesWindow;
    }

    public GameViewWindow getGameViewWindow() {
        return gameViewWindow;
    }
}
