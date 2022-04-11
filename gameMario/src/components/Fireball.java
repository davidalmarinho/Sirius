package components;

import gameobjects.components.Component;

public class Fireball extends Component {
    private static int fireballCount = 0;
    public transient boolean goingRight = false;

    public static boolean canSpawn() {
        return fireballCount < 4;
    }
}
