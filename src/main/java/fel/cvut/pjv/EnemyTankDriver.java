package fel.cvut.pjv;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple AI that submits random moves to GameController. Controls one enemy tank. Necessary to run in its own thread.
 * EnemyTankDriver handles autonomous decision-making of enemies in level.
 * Can be paused individually, but it is recommended to use GameController method pauseAis() to pause all drivers at once.
 */
public class EnemyTankDriver implements Runnable {
    private final EnemyTank enemyTank;
    private final GameController gc;
    private boolean shouldRun;
    private boolean isPaused;

    /**
     * EnemyTankDriver constructor.
     *
     * @param enemyTank Vehicle which the driver will be controlling - must not be null.
     * @param gc        GameController reference to which movement and shooting requests will be submitted.
     * @see GameController
     */
    public EnemyTankDriver(EnemyTank enemyTank, GameController gc) {
        this.enemyTank = enemyTank;
        this.gc = gc;
        this.shouldRun = true;
        this.isPaused = false;
    }

    public int[] getEnemyTankPos() {
        return new int[]{enemyTank.getRowPos(), enemyTank.getColPos()};
    }

    /**
     * Run EnemyTankDriver.
     * <p>
     * Executing run method starts submitting shoot and move requests to game controller.
     * Method automatically finishes when its tanks hp drops to 0.
     * Can be stopped early with endAi().
     * Can be paused with setPaused().
     *
     * @see GameController
     */
    @Override
    public void run() {
        int[][] directions = new int[][]{{-1, 0}, {0, 1}, {1, 0,}, {0, -1}};
        int move = ThreadLocalRandom.current().nextInt(0, 3 + 1);
        int shot;
        while (enemyTank.getHealth() > 0 && shouldRun) {
            if (isPaused) {
                try {
                    Thread.sleep(100, 0);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            move = ThreadLocalRandom.current().nextInt(move, move + 2);
            if (move > 3) {
                move = 3;
            }
            shot = ThreadLocalRandom.current().nextInt(0, 100 + 1);
            try {
                Thread.sleep(600, 0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (enemyTank.getHealth() <= 0) {
                break;
            }
            gc.moveVehicle(enemyTank, directions[move]);
            if (shot > 90) {
                gc.shoot(enemyTank);
            }
            if (move > 2) {
                move = ThreadLocalRandom.current().nextInt(0, 4);
            }
        }
        gc.removeAi(this);
    }

    /**
     * Stops AI and removes this EnemyTankDriver from AiList in GameController.
     */
    public void endAi() {
        this.shouldRun = false;
    }

    /**
     * Pauses execution until further notice. Must be unpaused or thread will never finish.
     */
    public void setPaused(boolean state) {
        isPaused = state;
    }
}
