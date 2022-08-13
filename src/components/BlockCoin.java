package components;

import gameobjects.components.Component;
import sirius.utils.AssetPool;
import org.joml.Vector2f;

public class BlockCoin extends Component {
    private Vector2f topY;
    private float coinSpeed = 1.4f;

    @Override
    public void start() {
        topY = new Vector2f(this.gameObject.getTransform().position).add(0, 0.5f);
        AssetPool.getSound("assets/sounds/coin.ogg").play();
    }

    @Override
    public void update(float dt) {
        if (this.gameObject.getPosition().y < topY.y) {
            this.gameObject.transform(0, dt * coinSpeed);
            this.gameObject.scale(-(0.5f * dt) % -1.0f, 0);
        } else {
            gameObject.destroy();
        }
    }
}
