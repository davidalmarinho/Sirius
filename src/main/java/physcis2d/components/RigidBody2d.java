package physcis2d.components;

import gameobjects.components.Component;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;
import physcis2d.EBodyType;

public class RigidBody2d extends Component {
    private Vector2f velocity    = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping  = 0.9f;
    private EBodyType eBodyType  = EBodyType.DYNAMIC;
    private float mass           = 0.0f;

    private boolean fixedRotation      = false;
    private boolean continuousCollision = true;

    private Body rawBody = null;

    @Override
    public void update(float dt) {
        if (rawBody != null) {
            gameObject.transform.position.set(
                rawBody.getPosition().x, rawBody.getPosition().y
            );
            gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public EBodyType getEBodyType() {
        return eBodyType;
    }

    public void setEBodyType(EBodyType eBodyType) {
        this.eBodyType = eBodyType;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }

    public boolean isContinuousCollision() {
        return continuousCollision;
    }

    public void setContinuousCollision(boolean continuousCollision) {
        this.continuousCollision = continuousCollision;
    }

    public Body getRawBody() {
        return rawBody;
    }

    public void setRawBody(Body rawBody) {
        this.rawBody = rawBody;
    }
}
