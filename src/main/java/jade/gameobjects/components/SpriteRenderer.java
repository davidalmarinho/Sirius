package jade.gameobjects.components;

public class SpriteRenderer extends Component {
    private boolean firstTime = true;

    @Override
    public void start() {
        System.out.println("Starting...");
    }

    @Override
    public void update(float dt) {
        if (firstTime) {
            System.out.println("I am updating...");
            firstTime = false;
        }
    }
}
