package jade.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseProjection, inverseView;
    public Vector2f position;
    private Vector2f projectionSize = new Vector2f(32.0f * 40.0f, 32.0f * 21.0f);
    private float zoom = 1.0f;

    public Camera(Vector2f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();

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
        /* Aqui vamos normalizar a matriz. Ou seja, os seus valores passam a corresponder a 1.
        * Fazendo com que os cálculos se baseiem nesta matriz.
        */
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom,
                0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjection);
    }

    // Onde a camara está no mundo e para once ela aponta
    public Matrix4f getViewMatrix() {
        // Frente da câmara (onde a câmara está a olhar)
        Vector3f center = new Vector3f(position.x, position.y, 1);
        Vector3f cameraForward = new Vector3f(0.0f, 0.0f, -1.0f); // x's direction
        center.add(cameraForward);

        // Para onde o topo da câmara está a apontar
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f); // y's direction

        this.viewMatrix.identity();

        viewMatrix = viewMatrix.lookAt(
                new Vector3f(position.x, position.y, 20.0f), // Onde a camara está posicionada
                center, // Para onde está a olhar
                cameraUp
        );

        viewMatrix.invert(inverseView);

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
}
