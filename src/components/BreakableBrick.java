package components;

import sirius.utils.Pool;

public class BreakableBrick extends Block {

    @Override
    void playerHit(PlayerController playerController) {
        if (!playerController.isSmall()) {
            Pool.Assets.getSound("assets/sounds/break_block.ogg").play();
            gameObject.destroy();
        }
    }
}
