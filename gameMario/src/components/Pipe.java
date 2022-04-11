package components;

import gameobjects.GameObject;
import gameobjects.components.Component;
import jade.SiriusTheFox;
import jade.input.KeyListener;
import jade.utils.AssetPool;
import main.Direction;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component {
    private Direction direction;
    private String connectingPipeName = "";
    private boolean entrance;
    private transient GameObject connectingPipe = null;
    private transient float entranceVectorTolerance = 0.6f;
    private transient PlayerController collidingPlayer = null;

    public Pipe(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null) {
            switch (direction) {
                case UP:
                    if (hitNormal.y < entranceVectorTolerance)
                        return;
                    break;
                case RIGHT:
                    if (hitNormal.x < entranceVectorTolerance)
                        return;
                    break;
                case DOWN:
                    if (hitNormal.y > -entranceVectorTolerance)
                        return;
                    break;
                case LEFT:
                    if (hitNormal.x > -entranceVectorTolerance)
                        return;
                    break;
            }

            collidingPlayer = playerController;
        }
    }

    @Override
    public void endCollision(GameObject collidingObject, Contact contact, Vector2f hitNormal) {
        PlayerController playerController = collidingObject.getComponent(PlayerController.class);
        if (playerController != null)
            collidingPlayer = null;
    }

    @Override
    public void start() {
        connectingPipe = SiriusTheFox.getCurrentScene().getGameObject(connectingPipeName);
    }

    @Override
    public void update(float dt) {
        if (connectingPipe == null) return;
        boolean playerEntering = false;

        if (collidingPlayer != null) {
            switch (direction) {
                case UP:
                    if (KeyListener.isKeyPressed(GLFW_KEY_DOWN) || KeyListener.isKeyPressed(GLFW_KEY_S) && entrance)
                        playerEntering = true;
                    break;

                case LEFT:
                    if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D) && entrance)
                        playerEntering = true;
                    break;

                case DOWN:
                    if (KeyListener.isKeyPressed(GLFW_KEY_UP) || KeyListener.isKeyPressed(GLFW_KEY_W) && entrance)
                        playerEntering = true;
                    break;

                case RIGHT:
                    if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A) && entrance)
                        playerEntering = true;
                    break;
            }

            if (playerEntering) {
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                collidingPlayer.setPosition(getPlayerPosition(connectingPipe));
                GameCamera gameCamera = SiriusTheFox.getCurrentScene()
                        .getGameObject("GameCamera").getComponent(GameCamera.class);
                gameCamera.mayChangeHighestX(true);
            }
        }
    }

    private Vector2f getPlayerPosition(GameObject pipe) {
        Pipe pipeComponent = pipe.getComponent(Pipe.class);
        switch (pipeComponent.direction) {
            case UP:
                return new Vector2f(pipe.transform.position).add(0.0f, 0.5f);
            case LEFT:
                return new Vector2f(pipe.transform.position).add(-0.5f, 0.0f);
            case DOWN:
                return new Vector2f(pipe.transform.position).add(0.0f, -0.5f);
            case RIGHT:
                return new Vector2f(pipe.transform.position).add(0.5f, 0.0f);
        }

        return new Vector2f();
    }
}
