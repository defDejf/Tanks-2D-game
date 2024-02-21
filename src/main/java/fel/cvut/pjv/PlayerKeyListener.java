package fel.cvut.pjv;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Class that handles keyboard input from user. Used KeyPressed and KeyReleased - it is possible to give periodic input by holding key down.
 */
public class PlayerKeyListener extends KeyAdapter {
    //UP - 1; RIGHT - 2; DOWN - 3; LEFT - 4; NOTHING - 0;
    private volatile int pressedDirection;
    private boolean shoot;
    private boolean escWasPressed;

    public PlayerKeyListener() {
        super();
        pressedDirection = 0;
        escWasPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyPressed = e.getKeyCode();
        switch (keyPressed) {
            case KeyEvent.VK_W:
                pressedDirection = 1;
                break;
            case KeyEvent.VK_D:
                pressedDirection = 2;
                break;
            case KeyEvent.VK_S:
                pressedDirection = 3;
                break;
            case KeyEvent.VK_A:
                pressedDirection = 4;
                break;
            case KeyEvent.VK_ESCAPE:
                escWasPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                shoot = true;
        }
    }

    /**
     * Resets direction of movement requested by user back to 0.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyPressed = e.getKeyCode();
        switch (keyPressed) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_D:
            case KeyEvent.VK_S:
            case KeyEvent.VK_A:
                pressedDirection = 0;
                break;
            case KeyEvent.VK_SPACE:
                shoot = false;
                break;
        }
    }

    /**
     * Gets direction requested by user.
     *
     * @return 0 means no request,
     * 1 means up,
     * 2 means right,
     * 3 means down,
     * 4 means left.
     */
    public int getPressedDirection() {
        return pressedDirection;
    }

    /**
     * Gets whether user wants to shoot.
     *
     * @return returns true if user wants to shoot.
     */
    public boolean isShoot() {
        return shoot;
    }

    /**
     * Gets whether user pressed Esc.
     *
     * @return returns true if pressed Esc.
     */
    public boolean isEscWasPressed() {
        return escWasPressed;
    }

    /**
     * Resets all keys to default values.
     */
    public void resetKeys() {
        escWasPressed = false;
        pressedDirection = 0;
        shoot = false;
    }
}
