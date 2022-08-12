package sirius.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import sirius.rendering.color.Color;
import sirius.rendering.color.ColorBlindness;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    public Vector2f position;
    private float projectionWidth = 6f;
    private float projectionHeight = 3f;
    private Color clearColor;
    private Vector2f projectionSize = new Vector2f(projectionWidth, projectionHeight);
    private float zoom = 1.0f;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.clearColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);

        /* Since gl_Position = viewMatrix * projectionMatrix * aPos, we can use this formula to convert
         * screen coordinates to world coordinates doing:
         *
         *      world_coordinates = gl_Position * (viewMatrix) ^ -1 * (projectionMatrix) ^ -1
         *
         * This is why we will keep the inverseProjection and the inverseView, to do this math
         * when it is needed.
         * */
        this.inverseProjection = new Matrix4f();
        this.inverseView = new Matrix4f();
        adjustProjection();
    }

    public void adjustProjection() {
        // Here, we will normalize the matrix. So, its values become corresponding to 1
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom,
                0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjection);
    }

    // Where the camera is in the world and where it is pointing to
    public Matrix4f getViewMatrix() {
        // Front of the camera --Where the camera is looking to
        Vector3f center = new Vector3f(position.x, position.y, 1);
        Vector3f cameraForward = new Vector3f(0.0f, 0.0f, -1.0f); // x's direction
        center.add(cameraForward);

        // To the top of where is the camera pointing
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f); // y's direction

        this.viewMatrix.identity();

        viewMatrix.lookAt(
                new Vector3f(position.x, position.y, 20.0f), // Where the camera is positioned
                center, // To where it is looking
                cameraUp
        );

        inverseView = new Matrix4f(viewMatrix.invert(inverseView));

        return this.viewMatrix;
    }

    public void addZoom(float value) {
        this.zoom += value;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjection() {
        return inverseProjection;
    }

    public Matrix4f getInverseView() {
        return inverseView;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public Color getClearColor() {
        return ColorBlindness.adaptColorBlindness(clearColor);
    }

    public void setClearColor(Color c) {
        this.clearColor.setColor(c);
    }
}
