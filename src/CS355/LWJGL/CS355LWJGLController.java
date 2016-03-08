package CS355.LWJGL;

/**
 *
 * @author Brennan Smith
 */
public interface CS355LWJGLController {

    /**
     * Clears and redraws.
     */
    void render();

    /**
     * Called once to setup. Initializes state data (Camera, Projection Matrices, etc).
     */
    void resizeGL();

    /**
     * Some dynamic action for scene. Not needed.
     */
    void update();

    /**
     * Polls for key-presses. Updates the camera position and state variables accordingly, as well as matrices.
     */
    void updateKeyboard();
    
}
