package jade.scenes;

// Will contain some functions to maintain the philosophy of using just one Scene class.
public abstract class SceneInitializer {
    public abstract void init(Scene scene);
    public abstract void loadResources(Scene scene);
    public abstract void imgui();
}
