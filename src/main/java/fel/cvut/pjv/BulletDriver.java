package fel.cvut.pjv;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Handles movement of all bullets on gameboard. Moves bullets by submitting requests to GameController.
 * Delay between moves is common for all bullets, but every bullet keeps its own time of last move.
 * Necessary to run in its own thread.
 */
public class BulletDriver implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final ConcurrentLinkedQueue<Bullet> bulletsToMove;
    private final int bulletDelay;
    private final GameController gc;
    private final GameBoard gb;
    private boolean shouldRun;
    private boolean isPaused;

    /**
     * BulletDriver constructor.
     *
     * @param bulletDelay Dictates the delay between each bullet movement. Every added bullet keeps its own last move time.
     */
    public BulletDriver(int bulletDelay, GameController gameController, GameBoard gb) {
        this.bulletsToMove = new ConcurrentLinkedQueue<>();
        this.bulletDelay = bulletDelay;
        this.gc = gameController;
        this.isPaused = false;
        this.gb = gb;
    }

    /**
     * Sets whether this should be running. If not, thread finishes.
     *
     * @param shouldRun set to true with initial call of run method. False will stop execution.
     */
    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    /**
     * Pauses execution until further notice. Must be unpaused, or it will stay running but not submitting any moves.
     */
    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    /**
     * Adds bullet bulletsToMove. Is thread safe.
     */
    public void addBullet(Bullet b) {
        LOGGER.finest("Added bullet to driver");
        bulletsToMove.add(b);
    }

    /**
     * Run BulletDriver.
     * <p>
     * Starts execution of bullet movements. It is necessary to run BulletDriver in its own thread,
     * and it is recommended to start it up during game setup.
     * Empty queue of bullets will not cause errors.
     */
    @Override
    public void run() {
        shouldRun = true;
        while (shouldRun) {
            if (isPaused) {
                try {
                    Thread.sleep(100, 0);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            double now = System.nanoTime();
            for (Bullet b : bulletsToMove) {
                if (b.isHasExploded()) {
                    LOGGER.finest("Removed bullet from driver.");
                    bulletsToMove.remove(b);
                } else if (now - b.getLastMove() > bulletDelay) {
                    b.setLastMove(now);
                    gc.moveBullet(b);
                }
            }
        }
        // remove all remaining bullets after stopping
        for (Bullet b : bulletsToMove) {
            gb.removeTileContent(b.getRowPos(), b.getColPos());
        }
        bulletsToMove.clear();
    }
}
