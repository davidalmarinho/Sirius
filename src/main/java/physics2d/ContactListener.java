package physics2d;

import gameobjects.GameObject;
import gameobjects.components.Component;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

public class ContactListener implements org.jbox2d.callbacks.ContactListener {
    // Updating order: preSolve -> beginContact -> endContact -> postSolve

    @Override
    public void beginContact(Contact contact) {
        // Get collider
        GameObject objA = (GameObject) contact.getFixtureA().m_userData;
        GameObject objB = (GameObject) contact.getFixtureB().m_userData;

        // Get info about the world --gameobjects's positions...
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        // Notify each component
        for (Component c : objA.componentList) {
            c.beginContact(objB, contact, aNormal);
        }

        for (Component c : objB.componentList) {
            c.beginContact(objA, contact, bNormal);
        }
    }

    @Override
    public void endContact(Contact contact) {
        // Get collider
        GameObject objA = (GameObject) contact.getFixtureA().m_userData;
        GameObject objB = (GameObject) contact.getFixtureB().m_userData;

        // Get info about the world --gameobjects's positions...
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        // Notify each component
        for (Component c : objA.componentList) {
            c.endContact(objB, contact, aNormal);
        }

        for (Component c : objB.componentList) {
            c.endContact(objA, contact, bNormal);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        // Get collider
        GameObject objA = (GameObject) contact.getFixtureA().m_userData;
        GameObject objB = (GameObject) contact.getFixtureB().m_userData;

        // Get info about the world --gameobjects's positions...
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        // Notify each component
        for (Component c : objA.componentList) {
            c.preSolve(objB, contact, aNormal);
        }

        for (Component c : objB.componentList) {
            c.preSolve(objA, contact, bNormal);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        // Get collider
        GameObject objA = (GameObject) contact.getFixtureA().m_userData;
        GameObject objB = (GameObject) contact.getFixtureB().m_userData;

        // Get info about the world --gameobjects's positions...
        WorldManifold worldManifold = new WorldManifold();
        contact.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x, worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();

        // Notify each component
        for (Component c : objA.componentList) {
            c.postSolve(objB, contact, aNormal);
        }

        for (Component c : objB.componentList) {
            c.postSolve(objA, contact, bNormal);
        }
    }
}
