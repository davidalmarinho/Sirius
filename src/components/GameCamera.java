package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import sirius.SiriusTheFox;
import sirius.rendering.Camera;
import sirius.rendering.color.Color;

public class GameCamera extends Component {
    private transient GameObject player;
    private transient Camera gameCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float undergroundYLevel;

    // When working below the ground
    private transient float cameraBuffer = 1.5f;
    private transient float playerHeight = 0.25f;

    private boolean changeHighestX = false;

    private Color skyColor = new Color(92f / 255f, 148f / 255f, 252f / 255f);
    private Color undergroundColor = Color.BLACK;

    public GameCamera(Camera camera) {
        this.gameCamera = camera;
    }

    @Override
    public void start() {
        this.player = SiriusTheFox.getCurrentScene().getGameObjectWith(PlayerController.class);
        this.gameCamera.setClearColor(skyColor);
        this.undergroundYLevel = this.gameCamera.position.y - this.gameCamera.getProjectionSize().y - this.cameraBuffer;
    }

    @Override
    public void update(float dt) {
        if (player == null) return;

        if (!player.getComponent(PlayerController.class).hasWon()) {
            gameCamera.position.x = Math.max(player.getPosition().x - 2.5f, highestX);
            highestX              = Math.max(highestX, gameCamera.position.x);

            if (changeHighestX) {
                highestX = player.getPosition().x - 2.5f;
                changeHighestX = false;
            }

            if (player.getPosition().y < -playerHeight) {
                this.gameCamera.position.y = undergroundYLevel;
                this.gameCamera.setClearColor(undergroundColor);
            } else if (player.getPosition().y >= 0.0f) {
                this.gameCamera.position.y = 0.0f;
                this.gameCamera.setClearColor(skyColor);
            }
        }
    }

    public void mayChangeHighestX(boolean mayChange) {
        changeHighestX = mayChange;
    }

    public boolean isUnderground() {
        return player.getPosition().y < -playerHeight;
    }

    public float getCameraBuffer() {
        return cameraBuffer;
    }
}
