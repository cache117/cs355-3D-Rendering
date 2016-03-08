package CS355.LWJGL;

import org.lwjgl.input.Keyboard;
import sun.rmi.runtime.Log;

import java.util.Iterator;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import static org.lwjgl.input.Keyboard.isKeyDown;

/**
 * @author Cache Staheli
 */
public class StudentLWJGLController implements CS355LWJGLController
{
    private static final Logger LOGGER = Logger.getLogger(StudentLWJGLController.class.getName());

    private static final float ASPECT = (LWJGLSandbox.DISPLAY_WIDTH / LWJGLSandbox.DISPLAY_HEIGHT);
    private static final float UNIT = 1.0f;
    private static final float FOVY = 65f;
    private static final float CLIPPING = FOVY / 1.3f;
    private static final float Z_NEAR = 1, Z_FAR = 200;

    private final WireFrame model;

    private Point3D cameraLocation;
    private double rotation;
    private float movementAmount;

    /**
     * Initializes the controller.
     */
    public StudentLWJGLController()
    {
        this.model = new HouseModel();
    }

    //This method is called to "resize" the viewport to match the screen.
    //When you first start, have it be in perspective mode.

    /**
     * This is called when the program first starts to initialize the viewport. It resizes the viewport to match the screen.
     */
    @Override
    public void resizeGL()
    {
        cameraLocation = new Point3D(-20, -2, 0);
        rotation = 270.0f;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glViewport(0, 0, LWJGLSandbox.DISPLAY_WIDTH, LWJGLSandbox.DISPLAY_HEIGHT);
        loadPerspective();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        movementAmount = UNIT;
    }

    /**
     * Not currently used.
     */
    @Override
    public void update()
    {

    }

    /**
     * This is called every frame, and is responsible for keyboard updates.
     *
     * @see Keyboard
     */

    @Override
    public void updateKeyboard()
    {
        //a move left
        if (isKeyDown(Keyboard.KEY_A))
        {
            moveLeft();
        }
        //d move right
        else if (isKeyDown(Keyboard.KEY_D))
        {
            moveRight();
        }
        //w move forward
        else if (isKeyDown(Keyboard.KEY_W))
        {
            moveForward();
        }
        //s move backward
        else if (isKeyDown(Keyboard.KEY_S))
        {
            moveBackward();
        }
        //q turn left
        else if (isKeyDown(Keyboard.KEY_Q))
        {
            rotateLeft();
        }
        //e turn right
        else if (isKeyDown(Keyboard.KEY_E))
        {
            rotateRight();
        }
        //r move up
        else if (isKeyDown(Keyboard.KEY_R))
        {
            moveUp();
        }
        //f move down
        else if (isKeyDown(Keyboard.KEY_F))
        {
            moveDown();
        }
        //h return to original "home" position and orientation
        else if (isKeyDown(Keyboard.KEY_H))
        {
            LOGGER.fine("Returning to Home");
            resizeGL();
        }
        //o switch to orthographic projection
        else if (isKeyDown(Keyboard.KEY_O))
        {
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(-CLIPPING, CLIPPING, -CLIPPING, CLIPPING, Z_NEAR, Z_FAR);
        }
        //p switch to perspective projection
        else if (isKeyDown(Keyboard.KEY_P))
        {
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            loadPerspective();
        } else
        {
            cheatCodes();
        }
        buildModelViewMatrix();
    }

    private void cheatCodes()
    {
        if (isKeyDown(Keyboard.KEY_1))
        {
            movementAmount = UNIT * 1.0f;
            LOGGER.info("Normal speed");
        }
        else if (isKeyDown(Keyboard.KEY_2))
        {
            movementAmount = UNIT * 2.0f;
            LOGGER.info("Quicker speed");
        }
        else if (isKeyDown(Keyboard.KEY_3))
        {
            movementAmount = UNIT * 3.0f;
            LOGGER.info("Fast speed");
        }
        else if (isKeyDown(Keyboard.KEY_4))
        {
            movementAmount = UNIT * 4.0f;
            LOGGER.info("Really Fast speed");
        }
        else if (isKeyDown(Keyboard.KEY_5))
        {
            movementAmount = UNIT * 10.0f;
            LOGGER.info("WARNING: Super-sonic speed");
        }
    }

    private void moveForward()
    {
        LOGGER.fine("Moving Forward");
        cameraLocation.x -= movementAmount * sin(rotation);
        cameraLocation.z += movementAmount * cos(rotation);
    }

    private void moveBackward()
    {
        LOGGER.fine("Moving Backward");
        cameraLocation.x += movementAmount * sin(rotation);
        cameraLocation.z -= movementAmount * cos(rotation);
    }

    private void moveLeft()
    {
        LOGGER.fine("Moving Left");
        cameraLocation.x += movementAmount * cos(rotation);
        cameraLocation.z += movementAmount * sin(rotation);
    }

    private void moveRight()
    {
        LOGGER.fine("Moving Right");
        cameraLocation.x -= movementAmount * cos(rotation);
        cameraLocation.z -= movementAmount * sin(rotation);
    }

    private void moveUp()
    {
        LOGGER.fine("Moving Up");
        cameraLocation.y += movementAmount;
    }

    private void moveDown()
    {
        LOGGER.fine("Moving Down");
        cameraLocation.y -= movementAmount;
    }

    private void rotateRight()
    {
        LOGGER.fine("Rotating Right");
        rotation += (movementAmount * 1.5f);
    }

    private void rotateLeft()
    {
        LOGGER.fine("Rotating Left");
        rotation -= (movementAmount * 1.5f);
    }

    private void loadPerspective()
    {
        gluPerspective(FOVY, ASPECT, Z_NEAR, Z_FAR);
    }

    private void buildModelViewMatrix()
    {
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        rotate((float) rotation);
        translateByPoint(cameraLocation);
        LOGGER.fine(cameraLocation.toString());
    }

    private void translateByPoint(Point3D point)
    {
        glTranslatef((float) point.x, (float) point.y, (float) point.z);
    }

    private void rotate(float rotation)
    {
        glRotatef(rotation, 0.0f, 1.0f, 0.0f);
    }

    //This method is the one that actually draws to the screen.
    @Override
    public void render()
    {
        //This clears the screen.
        glClear(GL_COLOR_BUFFER_BIT);

        drawNeighborhood();
    }

    private void drawNeighborhood()
    {
        final float houseSeparation = 30;
        for (float i = 0.0f; i < 10.f; i += 1.0f)
        {
            for (float j = 0.0f; j <= 1.0f; j += 1.0f)
            {
                glPushMatrix();
                glMatrixMode(GL_MODELVIEW);
                useRandomColor(i, j);
                glTranslatef(j * houseSeparation, 0, i * -houseSeparation);
                float rotation = (j == 1) ? 270.0f : 90.0f;
                rotate(rotation);
                drawHouse();
                glPopMatrix();
            }
        }
    }

    private void drawHouse()
    {
        Iterator<Line3D> iterator = model.getLines();
        while (iterator.hasNext())
        {
            Line3D line = iterator.next();
            drawLine(line.start, line.end);
        }
    }

    private void drawLine(Point3D start, Point3D end)
    {
        glBegin(GL_LINES);
        glVertex3d(start.x, start.y, start.z);
        glVertex3d(end.x, end.y, end.z);
        glEnd();
    }

    private void useRandomColor(float i, float j)
    {
        float red = 1.0f - ((i + 1.0f) * 0.1f);
        float green = 0.5f - (j * 0.25f);
        float blue = ((i + 1.0f) * 0.1f);
        glColor3f(red, green, blue);
    }

    private float cos(double rotation)
    {
        return (float) Math.cos(Math.toRadians(rotation));
    }

    private float sin(double rotation)
    {
        return (float) Math.sin(Math.toRadians(rotation));
    }

}
