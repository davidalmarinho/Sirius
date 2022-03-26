package physics2d.components;

import gameobjects.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

public class RaycastInfo implements RayCastCallback {
    // The fixture we hit
    public Fixture fixtureHit;
    public Vector2f hitPoint;
    public Vector2f normal;
    public float fraction;
    public boolean hitSomething;
    public GameObject hitObject;
    private GameObject requestingGameObject;

    public RaycastInfo(GameObject go) {
        this.fixtureHit           = null;
        this.hitPoint             = new Vector2f();
        this.normal               = new Vector2f();
        this.fraction             = 0.0f;
        this.hitSomething         = false;
        this.hitObject            = null;
        this.requestingGameObject = go;
    }

    @Override
    public float reportFixture(Fixture fixture, Vec2 hitPoint, Vec2 normal, float fraction) {
        // If we hit the requesting game object, we look for others
        // --because we don't want a reaction with an object that is raycasting itself
        if (fixture.m_userData == requestingGameObject)
            return 1; // When returning 1, JBox2D lib will keep its searching for others requesting game objects

        this.fixtureHit   = fixture;
        this.hitPoint     = new Vector2f(hitPoint.x, hitPoint.y);
        this.normal       = new Vector2f(normal.x, normal.y);
        this.fraction     = fraction;
        this.hitSomething = fraction != 0.0f;
        this.hitObject    = (GameObject) fixture.m_userData;

        return fraction;
    }
}
