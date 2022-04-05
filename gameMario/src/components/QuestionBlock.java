package components;

import jade.animations.StateMachine;

public class QuestionBlock extends Block {
    private enum BlockType {
        COIN,
        POWERUP,
        INVINCIBILITY
    }

    public BlockType blockType = BlockType.COIN;

    @Override
    void playerHit(PlayerController playerController) {
        switch (blockType) {
            case COIN:
                doCoin();
                break;
            case POWERUP:
                doPowerUp();
                break;
            case INVINCIBILITY:
                doInvincibility();
                break;
        }

        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine != null) {
            stateMachine.trigger("setInactive");
            this.setActive(false);
        }
    }

    private void doInvincibility() {
    }

    private void doPowerUp() {
    }

    private void doCoin() {
        /*GameObject coin = CustomPrefabs.generateCoin();
        coin.transform.position.set(gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        SiriusTheFox.getCurrentScene().addGameObject(coin);*/
    }
}
