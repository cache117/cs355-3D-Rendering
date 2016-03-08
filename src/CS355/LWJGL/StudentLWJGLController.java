package CS355.LWJGL;

import org.lwjgl.input.Keyboard;

import java.util.Iterator;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluPerspective;

/**
 * @author Cache Staheli
 */
public class StudentLWJGLController implements CS355LWJGLController
{
    private static final Logger LOGGER = Logger.getLogger(StudentLWJGLController.class.getName());
    private static final float ASPECT = (LWJGLSandbox.DISPLAY_WIDTH / LWJGLSandbox.DISPLAY_HEIGHT);
    private static final float CLIPPING = ASPECT / 2;
    private static final float ROTATION = 1.5f, UNIT = 1.0f;
    private static final float FOVY = 65f;
    private static final float Z_NEAR = 1, Z_FAR = 1000;

    //This is a model of a house.
    //It has a single method that returns an iterator full of Line3Ds.
    //A "Line3D" is a wrapper class around two Point3Ds.
    //It should all be fairly intuitive if you look at those classes.
    //If not, I apologize.
    private final WireFrame model;

    private Point3D cameraLocation;
    private double rotation;

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
        cameraLocation = new Point3D(0, 0, 0);
        rotation = 0.0;
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glViewport(0, 0, LWJGLSandbox.DISPLAY_WIDTH, LWJGLSandbox.DISPLAY_HEIGHT);
        loadPerspective();
    }

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
        if (Keyboard.isKeyDown(Keyboard.KEY_A))
        {
            moveLeft();
        }
        //d move right
        else if (Keyboard.isKeyDown(Keyboard.KEY_D))
        {
            moveRight();
        }
        //w move forward
        else if (Keyboard.isKeyDown(Keyboard.KEY_W))
        {
            moveForward();
        }
        //s move backward
        else if (Keyboard.isKeyDown(Keyboard.KEY_S))
        {
            moveBackward();
        }
        //q turn left
        else if (Keyboard.isKeyDown(Keyboard.KEY_Q))
        {
            rotateLeft();
        }
        //e turn right
        else if (Keyboard.isKeyDown(Keyboard.KEY_E))
        {
            rotateRight();
        }
        //r move up
        else if (Keyboard.isKeyDown(Keyboard.KEY_R))
        {
            moveUp();
        }
        //f move down
        else if (Keyboard.isKeyDown(Keyboard.KEY_F))
        {
            moveDown();
        }
        //h return to original "home" position and orientation
        else if (Keyboard.isKeyDown(Keyboard.KEY_H))
        {
            LOGGER.info("Returning to Home");
            resizeGL();
            //Do processing
        }
        //o switch to orthographic projection
        else if (Keyboard.isKeyDown(Keyboard.KEY_O))
        {
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(-CLIPPING, CLIPPING, -CLIPPING, CLIPPING, Z_NEAR, Z_FAR);
        }
        //p switch to perspective projection
        else if (Keyboard.isKeyDown(Keyboard.KEY_P))
        {
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            loadPerspective();
        }
        buildModelViewMatrix();
    }

    private void moveForward()
    {
        LOGGER.info("Moving Forward");
        cameraLocation.x -= UNIT * (float) Math.sin(Math.toRadians(rotation));
        cameraLocation.z += UNIT * (float) Math.cos(Math.toRadians(rotation));
    }

    private void moveBackward()
    {
        LOGGER.info("Moving Backward");
        cameraLocation.x += UNIT * (float) Math.sin(Math.toRadians(rotation));
        cameraLocation.z -= UNIT * (float) Math.cos(Math.toRadians(rotation));
    }

    private void moveLeft()
    {
        LOGGER.info("Moving Left");
        cameraLocation.x += UNIT * (float) Math.cos(Math.toRadians(rotation));
        cameraLocation.z -= UNIT * (float) Math.sin(Math.toRadians(rotation));
    }

    private void moveRight()
    {
        LOGGER.info("Moving Right");
        cameraLocation.x -= UNIT * (float) Math.cos(Math.toRadians(rotation));
        cameraLocation.z += UNIT * (float) Math.sin(Math.toRadians(rotation));
    }

    private void moveUp()
    {
        LOGGER.info("Moving Up");
        cameraLocation.y += UNIT;
    }

    private void moveDown()
    {
        LOGGER.info("Moving Down");
        cameraLocation.y -= UNIT;
    }

    private void rotateRight()
    {
        LOGGER.info("Rotating Right");
        rotation += ROTATION;
    }

    private void rotateLeft()
    {
        LOGGER.info("Rotating Left");
        rotation -= ROTATION;
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

    private double calculateRadians(double degrees)
    {
        return Math.toRadians(degrees);
    }

}
