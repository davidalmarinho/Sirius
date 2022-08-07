package sirius;

import sirius.editor.imgui.ICustomPrefabs;
import sirius.editor.imgui.ICustomPropertiesWindow;
import sirius.editor.imgui.ImGuiLayer;
import sirius.input.KeyListener;
import sirius.input.MouseListener;
import sirius.rendering.FrameBuffer;
import sirius.rendering.PickingTexture;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    // TODO: 05/04/2022 Should go to imGuiLayer
    // Custom modifications
    private ICustomPropertiesWindow iCustomPropertiesWindow;
    private ICustomPrefabs iCustomPrefabs;

    private int width, height;
    private int maxWidth, maxHeight;
    private final String title;
    private long glfwWindow; // Will act as a pointer
    private long monitor;
    private boolean fullscreen = false;
    private ImGuiLayer imGuiLayer;
    private FrameBuffer frameBuffer;
    private PickingTexture pickingTexture;

    public int maxFps = 60;
    private boolean vsync = true;

    public Window(String title, int width, int height) {
        this.title  = title;
        this.width  = 1920;
        this.height = 1080;
    }

    public Window(String title) {
        this(title, 1920, 1080);
    }

    public void init() {
        // Callback error --if there are errors, we will show them here.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Error: GLFW couldn't be Initialized.");
        }

        // Configure the GLFW --resizable window, close operation...)
        glfwDefaultWindowHints(); // Put all default first

        // For linux compatibility, we have to specify the context version and make an opengl profile
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,  2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // For now, we want the window invisible
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        monitor = glfwGetPrimaryMonitor();

        // TODO: 13/09/2021 Define WIDTH and HEIGHT
        glfwWindow = glfwCreateWindow(1920, 1080, this.title, NULL, NULL);

        if (glfwWindow == NULL) {
            throw new IllegalStateException("Error: GLFW windows couldn't be initialized");
        }

        // Configure mouse's callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            setWidth(newWidth);
            setHeight(newHeight);
        });

        // Configure keyboard's callbacks
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetCharCallback(glfwWindow, KeyListener::characterCallback);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // Center window
            GLFWVidMode vidMode = glfwGetVideoMode(monitor);
            assert vidMode != null;

            // Register the max size the window can take
            maxWidth = vidMode.width();
            maxHeight = vidMode.height();

            glfwSetWindowPos(glfwWindow,
                    (vidMode.width() / 2 - pWidth.get(0) / 2),
                    (vidMode.height() / 2 - pHeight.get(0) / 2));

            if (fullscreen)
                glfwSetWindowMonitor(glfwWindow, monitor, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate());
        }

        // OpenGL context
        glfwMakeContextCurrent(glfwWindow);

        // Turn on the v-sync --Uses how many hz the monitor has to refresh the frames
        setVsync(this.vsync);

        // Turn the window visible
        glfwShowWindow(glfwWindow);
    }

    public void start() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // TODO: 02/01/2022 Gets the full window size and put that here
        frameBuffer = new FrameBuffer(getWidth(), getHeight());
        pickingTexture = new PickingTexture(getWidth(), getHeight());

        glViewport(0, 0, getWidth(), getHeight());

        imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        imGuiLayer.initImGui();

        glfwFocusWindow(glfwWindow);
    }

    public void update() {
        if (KeyListener.isBindDown(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_F)) {
            fullscreen = !fullscreen;
            GLFWVidMode glfwVidMode = glfwGetVideoMode(monitor);

            if (fullscreen) {
                glfwSetWindowMonitor(glfwWindow, monitor, 0, 0, glfwVidMode.width(), glfwVidMode.height(), glfwVidMode.refreshRate());
            } else {
                glfwSetWindowMonitor(glfwWindow, NULL, 0, 0, 1920, 1080, glfwVidMode.refreshRate());
            }
        }
    }

    public void destroy() {
        imGuiLayer.destroyImGui();
        glfwDestroyWindow(glfwWindow);
    }

    /**
     * Destroys the Window and frees the memory.
     * Also terminates the executions of the window
     */
    public void freeMemory() {
        // Free the memory
        glfwFreeCallbacks(glfwWindow); // Clean up the errors --Gives reset to the window errors
        glfwDestroyWindow(glfwWindow); // Destroys the window

        // Finish window's executions
        glfwTerminate(); // Terminates glfwWindowHint library
        glfwSetErrorCallback(null).free(); // Free the error callback
    }

    public void dispose() {
        glfwSwapBuffers(glfwWindow);
    }

    public boolean isVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        if (this.vsync != vsync) {
            this.vsync = vsync;

            if (vsync) {
                glfwSwapInterval(1);
            } else {
                glfwSwapInterval(0);
            }

            maxFps = Objects.requireNonNull(glfwGetVideoMode(monitor)).refreshRate();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public float getUpdateFps() {
        return 1.0f / maxFps;
    }

    public boolean isWindowClosed() {
        return glfwWindowShouldClose(glfwWindow);
    }

    public float getTargetAspectRatio() {
        return (float) getWidth() / getHeight();
    }

    public FrameBuffer getFramebuffer() {
        return frameBuffer;
    }

    public ImGuiLayer getImGuiLayer() {
        return imGuiLayer;
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }

    public int getMaxFps() {
        return maxFps;
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }

    public ICustomPropertiesWindow getICustomPropertiesWindow() {
        return iCustomPropertiesWindow;
    }

    public void setICustomPropertiesWindow(ICustomPropertiesWindow iCustomPropertiesWindow) {
        this.iCustomPropertiesWindow = iCustomPropertiesWindow;
    }

    public ICustomPrefabs getICustomPrefabs() {
        return iCustomPrefabs;
    }

    public void setICustomPrefabs(ICustomPrefabs iCustomPrefabs) {
        this.iCustomPrefabs = iCustomPrefabs;
    }
}