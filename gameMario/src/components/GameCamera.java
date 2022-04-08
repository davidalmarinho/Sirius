package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import jade.SiriusTheFox;
import jade.rendering.Camera;
import jade.rendering.Color;

public class GameCamera extends Component {
    private transient GameObject player;
    private transient Camera gameCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergroundYLevel;

    // When working below the ground
    private transient float cameraBuffer = 1.5f;
    private transient float playerHeight = 0.25f;

    private Color skyColor = new Color(92f / 255f, 148f / 255f, 252f / 255f);
    private Color undergroundColor = Color.BLACK;

    public GameCamera(Camera camera) {
        this.gameCamera = camera;
    }

    @Override
    public void start() {
        this.player = SiriusTheFox.getCurrentScene().getGameObjectWith(PlayerController.class);
    }
}
