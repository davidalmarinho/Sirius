package physcis2d.components;

import gameobjects.components.Component;

public class CircleCollider extends Component {
    private float radius = 1.0f;

    /**
     * Gets radius size
     * @return Radius' size
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets radius' size
     * @param radius The size that is wished
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }
}
