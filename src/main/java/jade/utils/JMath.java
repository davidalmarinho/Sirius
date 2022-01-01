package jade.utils;

import org.joml.Vector2f;

public class JMath {
    /**
     * Rotates a vertex of an object based on object's position.
     *
     * @param vertex the vertex that needs to be rotated
     * @param angleDegrees how much it will be rotated in degrees units
     * @param objectCenter the center/point that the vertex will depend on
     */
    public static void rotate(Vector2f vertex, float angleDegrees, Vector2f objectCenter) {
        /*
         *  Math required:
         *      x' = x * cos (theta) - y * sin(theta)
         *      y' = x * sin (theta) + y * cos(theta)
         */

        // Put the object's center in the origin
        Vector2f origin = new Vector2f(vertex).sub(objectCenter);

        angleDegrees = (float) Math.toRadians(angleDegrees);

        // Calculate the new rotated position
        float xPrime = (float) (origin.x * Math.cos(angleDegrees) - origin.y * Math.sin(angleDegrees));
        float yPrime = (float) (origin.x * Math.sin(angleDegrees) + origin.y * Math.cos(angleDegrees));

        // Put the new position where was the object before
        xPrime += objectCenter.x;
        yPrime += objectCenter.y;
        vertex.set(xPrime, yPrime);
    }
}