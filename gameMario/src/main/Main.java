package main;

import jade.SiriusTheFox;

public class Main {

    public static void main(String[] args) {
        // Get engine
        SiriusTheFox engine = SiriusTheFox.get();

        // Add customized operations
        engine.addCustomizedPropertiesWindow(new MyPropertiesWindow());
        engine.addRuntimeOptionCustomizedPrefabs(new CustomPrefabs());
        engine.addCustomLevelSceneInitializer(new CustomLevelSceneInitializer());
        // engine.setReadyToExport(true);

        // Run engine
        engine.run();
    }
}
