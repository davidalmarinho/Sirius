package components;

import gameobjects.GameObject;
import gameobjects.IPrefabs;
import jade.SiriusTheFox;
import jade.animations.StateMachine;
import main.CustomPrefabs;

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
                doPowerUp(playerController);
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

    private void spawnItem(IPrefabs iPrefabs) {
        GameObject item = iPrefabs.generate(null, 0.25f, 0.25f);
        item.transform.position.set(gameObject.transform.position);
        item.transform.position.y += 0.25f;
        SiriusTheFox.getCurrentScene().addGameObject(item);
    }

    private void spawnMushroom() {
        spawnItem(CustomPrefabs::generateMushroom);
    }

    private void spawnFlower() {
        spawnItem(CustomPrefabs::generateFlower);
    }

    private void doPowerUp(PlayerController playerController) {
        if (playerController.isSmall())
            spawnMushroom();
        else
            spawnFlower();
    }

    private void doCoin() {
        GameObject coin = CustomPrefabs.generateCoin();
        coin.transform.position.set(gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        SiriusTheFox.getCurrentScene().addGameObject(coin);
    }
}
