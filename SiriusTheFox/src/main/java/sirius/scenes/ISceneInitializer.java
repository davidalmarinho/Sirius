package sirius.scenes;

// Will contain some functions to maintain the philosophy of using just one Scene class.
public interface ISceneInitializer {
    void init(Scene scene);
    void loadResources(Scene scene);
    void imgui();
    ISceneInitializer build();
}
