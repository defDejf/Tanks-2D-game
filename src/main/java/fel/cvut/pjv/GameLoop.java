package fel.cvut.pjv;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Handles updating view at set framerate and forwards user input to controller.
 * It is necessary to run GameLoop in its own thread.
 */
public class GameLoop implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final PlayerKeyListener controls;
    private final GameController gameController;
    private final GameBoard gameBoard;
    private final GameView gameView;
    private final PlayerTank playerTank;
    private final ConcurrentLinkedQueue<int[]> positionsToUpdate;
    private boolean shouldRun;
    private boolean paused;

    /**
     * GameLoop constructor
     * It is necessary to run GameLoop in its own thread.
     *
     * @param controls       KeyAdapter which takes user input.
     * @param gameController GameController reference to submit requests based on user input.
     * @param gameView       GameView reference to call updates.
     * @param gameBoard      GameBoard reference to handle explosion displaying.
     * @param playerTank     Player controlled tank.
     */
    public GameLoop(PlayerKeyListener controls, GameController gameController, GameView gameView, GameBoard gameBoard, PlayerTank playerTank) {
        this.controls = controls;
        this.gameController = gameController;
        this.gameView = gameView;
        this.playerTank = playerTank;
        this.gameBoard = gameBoard;
        this.positionsToUpdate = new ConcurrentLinkedQueue<>();
        this.paused = false;
    }

    /**
     * Run GameLoop.
     * It is necessary to run GameLoop in its own thread.
     * GameLoop is set to run at 60fps. It adjusts timeouts on the go based on execution time to keep framerate stable.
     * Handles periodic user input and updates view.
     * Can be paused by user.
     * Can be stopped by user.
     * Stops automatically when all enemies are destroyed or player is destroyed.
     */
    @Override
    public void run() {
        final int desiredFPS = 60;
        final int desiredUPS = 60;
        // (1s in ns)/desired
        final long updateThreshold = 1000000000 / desiredUPS;
        final long drawThreshold = 1000000000 / desiredFPS;

        long lastFPS = 0;
        long lastUPS = 0;

        long lastKeyActivation = System.nanoTime();
        long lastShot = lastKeyActivation;

        shouldRun = !(playerTank == null);

        while (shouldRun) {
            paused = controls.isEscWasPressed();
            if (paused) {
                gameController.pauseGame();
                if (gameView.createQuitOrContinueDialog() == 0) { // option pane is blocking sp this works fine
                    LOGGER.info("User quit the level.");
                    gameController.saveScore();
                    gameController.stopGame();
                    gameController.savePlayerStats(new int[]{playerTank.getHealth(), playerTank.getArmor(), playerTank.getDamage()});
                } else {
                    controls.resetKeys();
                    gameController.resumeGame();
                    paused = false;
                }
                continue;
            }
            // check for game end
            if (gameController.isAiListEmpty()) {
                LOGGER.info("All enemy tanks were destroyed.");
                gameView.createInfoPopup("You Won!");
                gameController.saveScore();
                gameController.stopGame();
                gameController.savePlayerStats(new int[]{playerTank.getHealth(), playerTank.getArmor(), playerTank.getDamage()});
            }

            // send user input and parameters to game controller and decrement explosion ticks
            if ((System.nanoTime() - lastUPS) > updateThreshold) {
                lastUPS = System.nanoTime();
                gameView.setPlayerParams(new int[]{playerTank.getHealth(), playerTank.getArmor(), playerTank.getDamage()});
                gameView.setScore(playerTank.getScore());
                int move = controls.getPressedDirection();
                if (move > 0 && (System.nanoTime() - lastKeyActivation) > Constants.minmsKeyDelay) {
                    lastKeyActivation = System.nanoTime();
                    gameController.moveVehicle(playerTank, directionEncoder(move));
                }
                if (controls.isShoot() && (System.nanoTime() - lastShot) > Constants.minmsKeyDelay) {
                    lastShot = System.nanoTime();
                    gameController.shoot(playerTank);
                }
                checkExplosions();
            }
            // update view
            if ((System.nanoTime() - lastFPS) > drawThreshold) {
                lastFPS = System.nanoTime();
                if (!positionsToUpdate.isEmpty()) {
                    gameView.updateView(positionsToUpdate);
                    clearPosList();
                }
                if (playerTank.getHealth() < 1) {
                    LOGGER.info("User died in level.");
                    gameController.savePlayerStats(new int[]{gameBoard.playerVals[2], gameBoard.playerVals[3], gameBoard.playerVals[5]});
                    gameView.createInfoPopup("You died!\n You will be returned to Main Menu with original stats.");
                    gameController.stopGame();
                }
                gameView.updatePlayerParams();
                gameView.updateScore();
            }

            // Calculate next frame, or skip if we are running behind
            if (!((System.nanoTime() - lastUPS) > updateThreshold || (System.nanoTime() - lastFPS) > drawThreshold)) {
                long nextScheduledUP = lastUPS + updateThreshold;
                long nextScheduledDraw = lastFPS + drawThreshold;

                long minScheduled = Math.min(nextScheduledUP, nextScheduledDraw);

                long nanosToWait = minScheduled - System.nanoTime();

                if (nanosToWait <= 0)
                    continue;

                try {
                    Thread.sleep(nanosToWait / 1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // cleanup in case playertank was null
        if (playerTank == null) {
            LOGGER.severe("No player tank was loaded - exiting level");
            gameController.stopGame();
            gameView.terminate();
        }
    }

    //decrement each explosion tick by 1, remove if 0 remains
    private void checkExplosions() {
        for (int i = 0; i < Constants.boardSize; i++) {
            for (int j = 0; j < Constants.boardSize; j++) {
                if (gameBoard.boardArray[i][j].isExploding()) {
                    ExplosionTile e = gameBoard.boardArray[i][j].getExplosion();
                    if (e.getTicksToShow() == 0) {
                        LOGGER.finest("Removed explosion at " + i + " " + j);
                        gameBoard.removeExplosion(i, j);
                        positionsToUpdate.add(new int[]{i, j});
                    } else {
                        e.setTicksToShow(e.getTicksToShow() - 1);
                    }
                }
            }
        }
    }

    /**
     * Gets whether this is currently paused. Paused GameLoop does not mean termination.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets paused property. Pausing should be done only in pair with pausing BulletDriver and AIs.
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Adds new position where changed occurred and needs to be updated in view.
     * Is thread safe.
     */
    public void addPosition(int[] posToAdd) {
        positionsToUpdate.add(posToAdd);
    }

    /**
     * Removes all position from positionsToUpdate. Should be used only for cleanup.
     */
    private synchronized void clearPosList() {
        positionsToUpdate.clear();
    }

    /**
     * Sets whether loop should be running.
     *
     * @param shouldRun is true after initial call of run method. False will stop make thread finish.
     */
    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    private int[] directionEncoder(int pressedDirection) {
        int[] ret = new int[]{0, 0};
        switch (pressedDirection) {
            case 1:
                ret = new int[]{-1, 0};
                break;
            case 2:
                ret = new int[]{0, 1};
                break;
            case 3:
                ret = new int[]{1, 0};
                break;
            case 4:
                ret = new int[]{0, -1};
                break;
        }
        return ret;
    }
}
