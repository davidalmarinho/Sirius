package jade.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix;
    public Vector3f position;

    public Camera(Vector3f position) {
        this.position = position;
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection();
    }

    private void adjustProjection() {
        /* Aqui vamos normalizar a matriz. Ou seja, os seus valores passam a corresponder a 1.
        * Fazendo com que os cálculos se baseiem nesta matriz.
        */
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
    }

    // Onde a camara está no mundo e para once ela aponta
    public Matrix4f getViewMatrix() {
        // Frente da câmara (onde a câmara está a olhar)
        Vector3f center = new Vector3f(position.x, position.y, position.z);
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

        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
