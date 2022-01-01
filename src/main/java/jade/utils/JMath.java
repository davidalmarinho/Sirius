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

    /**
     * Checks if 2 floats are equal depending on an error margin
     *
     * @param f1 first float
     * @param f2 second float
     * @param epsilon error margin
     * @return true if they are equal depending on this error margin
     */
    public static boolean compare(float f1, float f2, float epsilon) {
        return Math.abs(f1 - f2) <= epsilon * Math.max(1.0f, Math.max(Math.abs(f1), Math.abs(f2)));
    }

    /**
     * Checks if 2 floats are equal depending on an error margin
     *
     * @param f1 first float
     * @param f2 second float
     * @return true if they are equal with a margin of error of Float.MIN_VALUE
     */
    public static boolean compare(float f1, float f2) {
        return compare(f1, f2, Float.MIN_VALUE);
    }

    /**
     * Checks if 2 vectors type float are equal depending on an error margin
     *
     * @param vec1 first type float vector
     * @param vec2 second type float vector
     * @param epsilon required error margin
     * @return true if the 2 vectors are equal depending on the required margin of error
     */
    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    /**
     * Checks if 2 vectors type float are equal depending on an error margin
     *
     * @param vec1 first type float vector
     * @param vec2 second type float vector
     * @return true if the 2 vectors are equal depending on a margin of error of Float.MIN_VALUE
     */
    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1, vec2, Float.MIN_VALUE);
    }
}