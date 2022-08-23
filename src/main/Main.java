package main;

import sirius.SiriusTheFox;

public class Main {

    public static void main(String[] args) {
        // Get engine
        SiriusTheFox engine = SiriusTheFox.get();

        // Add customized operations
        engine.addCustomizedPropertiesWindow(new MyPropertiesWindow());
        engine.addRuntimeOptionCustomizedPrefabs(new CustomPrefabs());
        engine.addCustomLevelSceneInitializer(new CustomLevelSceneInitializer());

        // Run engine
        engine.run();
    }
}
