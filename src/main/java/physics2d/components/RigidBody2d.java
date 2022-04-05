package physics2d.components;

import gameobjects.components.Component;
import jade.SiriusTheFox;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.joml.Vector2f;
import physics2d.BodyTypes;

public class RigidBody2d extends Component {
    private Vector2f velocity = new Vector2f();
    private float angularDamping = 0.8f;
    private float linearDamping = 0.9f;
    private BodyTypes bodyType = BodyTypes.DYNAMIC;
    private float mass = 0.0f;
    private float friction = 0.1f;
    private float angularVelocity = 0.0f;
    private float gravityScale = 1.0f;
    private boolean sensor = false;

    private boolean fixedRotation = false;
    private boolean continuousCollision = true;

    private transient Body rawBody = null;

    public void addVelocity(Vector2f forceToAdd) {
        if (rawBody == null) return;

        rawBody.applyForceToCenter(new Vec2(velocity.x, velocity.y));
    }

    public void addImpulse(Vector2f impulse) {
        if (rawBody == null) return;

        rawBody.applyLinearImpulse(new Vec2(velocity.x, velocity.y), rawBody.getWorldCenter());
    }

    @Override
    public void update(float dt) {
        if (rawBody != null) {
            if (this.bodyType == BodyTypes.DYNAMIC || this.bodyType == BodyTypes.KINEMATIC) {
                gameObject.transform.position.set(
                        rawBody.getPosition().x, rawBody.getPosition().y
                );
                gameObject.transform.rotation = (float) Math.toDegrees(rawBody.getAngle());
                Vec2 vel = rawBody.getLinearVelocity();
                this.velocity.set(vel.x, vel.y);

            // In this body type, the game object can't be moved
            } else if (this.bodyType == BodyTypes.STATIC) {
                this.rawBody.setTransform(new Vec2(
                        this.gameObject.transform.position.x, this.gameObject.transform.position.y),
                        this.gameObject.transform.rotation);
            }
        }
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);

        if (rawBody != null) this.rawBody.setLinearVelocity(new Vec2(velocity.x, velocity.y));
    }

    public float getAngularDamping() {
        return angularDamping;
    }

    public void setAngularDamping(float angularDamping) {
        this.angularDamping = angularDamping;

        if (rawBody != null) rawBody.setAngularDamping(angularDamping);
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public void setLinearDamping(float linearDamping) {
        this.linearDamping = linearDamping;
    }

    public BodyTypes getEBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyTypes bodyTypes) {
        this.bodyType = bodyTypes;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
        if (rawBody != null) rawBody.setAngularVelocity(angularVelocity);
    }

    public float getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(float gravityScale) {
        this.gravityScale = gravityScale;
        if (rawBody != null) rawBody.setGravityScale(gravityScale);
    }

    public boolean isSensor() {
        return sensor;
    }

    public void setSensor(boolean sensor) {
        this.sensor = sensor;
        if (rawBody == null) return;

        SiriusTheFox.getPhysics().setSensor(this, sensor);
    }

    public boolean isFixedRotation() {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
        if (rawBody != null) rawBody.setFixedRotation(fixedRotation);
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
