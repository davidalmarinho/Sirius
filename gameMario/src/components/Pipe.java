package components;

import gameobjects.components.Component;
import main.Direction;

public class Pipe extends Component {
    private Direction direction;

    public Pipe(Direction direction) {
        this.direction = direction;
    }
}
