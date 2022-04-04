import jade.SiriusTheFox;

public class Main {

    public static void main(String[] args) {
        // Get engine
        SiriusTheFox engine = SiriusTheFox.get();

        // Add customized operations
        engine.addCustomizedPropertiesWindow(new MyPropertiesWindow());

        // Run engine
        engine.run();
    }
}
