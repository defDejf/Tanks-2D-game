package fel.cvut.pjv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Handles all requests to modify model:
 * Handles creation and movement of bullets.
 * Handles movement of vehicles.
 * Handles collisions.
 * Handles explosions.
 * Is thread safe.
 */
public class GameController {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final MenuController menuController;
    private final GameBoard gameBoard;
    private final GameView gameView;
    private final PlayerKeyListener controls;
    private final LinkedList<EnemyTankDriver> aiList;
    protected boolean gameRan = false;
    String playerName;
    private BulletDriver bulletDriver;
    private PlayerTank player;
    private GameLoop gameLoop;

    /**
     * GameController constructor.
     * Upon initialization creates necessary objects to launch a level.
     *
     * @param menuController MenuController reference to start up main menu after quitting level.
     */
    public GameController(MenuController menuController) {
        this.menuController = menuController;
        gameBoard = new GameBoard(this);
        controls = new PlayerKeyListener();
        gameView = new GameView(gameBoard, controls);
        aiList = new LinkedList<>();
    }

    /**
     * Loads initial game state from level file.
     * Must be called AFTER initializing GameBoard.
     *
     * @param levelName  level to be loaded.
     * @param playerName player to be loaded.
     */
    public void setGameBoardFromFile(String levelName, String playerName) {
        gameBoard.setBoardFromFile(levelName, playerName);
    }

    /**
     * Adds EnemyTankDriver reference to AiList.
     *
     * @param driver driver to be added.
     */
    public void addAi(EnemyTankDriver driver) {
        aiList.add(driver);
    }

    /**
     * Removes driver reference from AiList.
     * Also removes its vehicle from gameboard and adds its last position to be updated in next frame.
     *
     * @param driver driver to be removed.
     */
    public void removeAi(EnemyTankDriver driver) {
        aiList.remove(driver);
        int[] lastPos = driver.getEnemyTankPos();
        gameBoard.removeTileContent(lastPos[0], lastPos[1]);
        gameLoop.addPosition(lastPos);
    }

    /**
     * Gets whether AiList is empty. An empty AiList can be considered victory for the player.
     */
    public boolean isAiListEmpty() {
        return aiList.isEmpty();
    }

    private void startAis() {
        for (EnemyTankDriver e : aiList) {
            Thread ai = new Thread(e);
            ai.start();
        }
    }

    private void pauseAis() {
        for (EnemyTankDriver e : aiList) {
            e.setPaused(true);
        }
    }

    private void resumeAis() {
        for (EnemyTankDriver e : aiList) {
            e.setPaused(false);
        }
    }

    /**
     * Pauses all AIs, GameLoop and BulletDriver thus freezing the game-state.
     */
    public void pauseGame() {
        LOGGER.fine("Game paused");
        pauseAis();
        bulletDriver.setPaused(true);
    }

    /**
     * Resumes all AIs, GameLoop and BulletDriver.
     */
    public void resumeGame() {
        LOGGER.fine("Game resumed");
        resumeAis();
        bulletDriver.setPaused(false);
    }

    private void stopAis() {
        for (EnemyTankDriver e : aiList) {
            e.endAi();
        }
    }

    /**
     * Stops GameLoop, AIs, BulletDriver and terminates GameView.
     */
    public void stopGame() {
        LOGGER.info("Game stopped");
        stopAis();
        gameLoop.setShouldRun(false);
        aiList.clear();
        bulletDriver.setShouldRun(false);
        menuController.setViewVisibility(true);
        controls.resetKeys();
        gameView.terminate();
    }

    /**
     * Starts game. Calls GameView to render GUI, initializes and starts BulletDriver, GameLoop.
     * Starts AIs.
     * Can be called only AFTER initializing GameBoard.
     */
    public void startGame(String playerName) {
        if (gameBoard == null) {
            LOGGER.severe("startGame called without setting up gameBoard");
            throw new RuntimeException("Cannot call start game without setting gameboard first");
        } else {
            LOGGER.info("Game started");
            this.playerName = playerName;
            gameRan = true;
            gameView.initGraphics();
            player = gameBoard.getPlayer();
            gameLoop = new GameLoop(controls, this, gameView, gameBoard, player);
            bulletDriver = new BulletDriver(Constants.bulletDelay, this, gameBoard);
            Thread loop = new Thread(gameLoop);
            Thread bulletMoves = new Thread(bulletDriver);
            bulletMoves.start();
            loop.start();
            startAis();
        }
    }

    /**
     * Moves vehicle by 1 tile in requested direction. Handles entity activation when they get driven on.
     * If requested direction does not match vehicle direction vehicle is rotated without moving.
     *
     * @param vehicle            vehicle to be moved - must not be null.
     * @param requestedDirection direction which the vehicle shall be moved/rotated.
     */
    public void moveVehicle(Vehicle vehicle, int[] requestedDirection) {
        int original_row_pos = vehicle.getRowPos();
        int original_col_pos = vehicle.getColPos();
        int desired_row_pos = original_row_pos + requestedDirection[0];
        int desired_col_pos = original_col_pos + requestedDirection[1];
        // if vehicle is facing the same direction as requestedDirection, move it. Else rotate it.
        if (Arrays.equals(vehicle.getFacingDirection(), requestedDirection)) {
            if (gameBoard.isOutOfBounds(desired_row_pos, desired_col_pos)) {
                LOGGER.finer(vehicle + " at " + original_row_pos + " " + original_col_pos + " attempted to move out of bounds");
            } else if (!gameBoard.canTileBeMovedOn(desired_row_pos, desired_col_pos)) {
                LOGGER.finer(vehicle + " at " + original_row_pos + " " + original_col_pos + " attempted to move on occupied tile");
            } else {
                // Check for tile content and activate it if necessary.
                // After activation, it gets removed so vehicle can move there
                if (!gameBoard.isTileEmpty(desired_row_pos, desired_col_pos)) {
                    gameBoard.getTileContent(desired_row_pos, desired_col_pos).getDrivenOn(vehicle);
                    gameBoard.removeTileContent(desired_row_pos, desired_col_pos);
                }
                LOGGER.finest(vehicle + " at " + original_row_pos + " " + original_col_pos + " moved to " +
                        desired_row_pos + " " + desired_col_pos);
                moveOnePosition(vehicle, original_row_pos, original_col_pos, desired_row_pos, desired_col_pos);
            }
        } else {
            LOGGER.finest(vehicle + " at " + original_row_pos + " " + original_col_pos + " was rotated");
            vehicle.setFacingDirection(requestedDirection);
            gameLoop.addPosition(new int[]{original_row_pos, original_col_pos});
        }
    }

    private void moveOnePosition(GameEntity entity, int rowPos, int colPos, int newRowPos, int newColPos) {
        gameBoard.removeTileContent(rowPos, colPos);
        gameBoard.setTileContent(newRowPos, newColPos, entity);
        entity.setRowPos(newRowPos);
        entity.setColPos(newColPos);
        gameLoop.addPosition(new int[]{rowPos, colPos});
        gameLoop.addPosition(new int[]{newRowPos, newColPos});
    }

    /**
     * Moves bullet by 1 tile. Handles bullet running out of range and collisions.
     * Bullets that move out of bounds are removed.
     * Handles notifying BulletDriver upon bullet explosion, so they can be removed.
     *
     * @param bullet - bullet to be moved in its facing direction.
     */
    public void moveBullet(Bullet bullet) {
        int original_row_pos = bullet.getRowPos();
        int original_col_pos = bullet.getColPos();
        int desired_row_pos = original_row_pos + bullet.getFacingDirection()[0];
        int desired_col_pos = original_col_pos + bullet.getFacingDirection()[1];
        if (gameBoard.isOutOfBounds(desired_row_pos, desired_col_pos)) {
            bullet.setHasExploded(true);
            gameBoard.removeTileContent(original_row_pos, original_col_pos);
            gameLoop.addPosition(new int[]{original_row_pos, original_col_pos});
            LOGGER.finer(bullet + " at " + original_row_pos + " " + original_col_pos + " was removed after exiting map.");

            // If tile is not empty or bullet ran out of range, explode bullet and hit entity on tile
        } else if (!gameBoard.isTileEmpty(desired_row_pos, desired_col_pos) || bullet.getRemainingRange() <= 0) {
            LOGGER.finer(bullet + " at " + original_row_pos + " " + original_col_pos + " has exploded.");
            bullet.setHasExploded(true);
            gameBoard.removeTileContent(original_row_pos, original_col_pos);
            explodeTiles(desired_row_pos, desired_col_pos, bullet.getExplosionRadius(), bullet.getDamage(), bullet.getAuthor());
            gameLoop.addPosition(new int[]{original_row_pos, original_col_pos});
        } else { // move bullet
            LOGGER.finest(bullet + " at " + original_row_pos + " " + original_col_pos + " was moved.");
            moveOnePosition(bullet, original_row_pos, original_col_pos, desired_row_pos, desired_col_pos);
            bullet.setRemainingRange(bullet.getRemainingRange() - 1);
        }
    }

    /**
     * Sets ExplosionTile in specified radius around explosion position. All explosions have an author because of keeping score.
     * Explosions that would be out of bounds are not created.
     * Handles hitting entities with said explosion upon creation.
     * Handles removing entities if they are destroyed by the explosion.
     *
     * @param rowPos center position of explosion
     * @param colPos center position of explosion
     * @param author creator of explosion - if entity is hit he will get score for it.
     * @param radius how many tiles around requested position are also exploded. 0 explodes only requested position, 1 explodes 3x3 area with requested position in the middle.
     */
    public void explodeTiles(int rowPos, int colPos, int radius, int damage, Vehicle author) {
        for (int y = rowPos - radius; y <= rowPos + radius; y++) {
            for (int x = colPos - radius; x <= colPos + radius; x++) {
                if (!gameBoard.isOutOfBounds(y, x)) {
                    LOGGER.finest("Exploded tile " + y + " " + x);
                    ExplosionTile explosionTile = new ExplosionTile(y, x, new int[]{-1, 0}, true, damage, author);
                    if (!gameBoard.isTileEmpty(y, x)) { // hit entity with explosion
                        LOGGER.finer("Hit entity at " + y + " " + x);
                        if (gameBoard.getTileContent(y, x).getHit(explosionTile)) { // remove entity if destroyed
                            LOGGER.finer("Destroyed entity at " + y + " " + x);
                            gameBoard.removeTileContent(y, x);
                        }
                    }
                    gameBoard.setExplosionAtPos(y, x, explosionTile);
                    gameLoop.addPosition(new int[]{y, x});
                }
            }
        }
    }

    /**
     * Creates new Bullet instance and adds it to BulletDriver.
     * Facing direction of bullet is based on current facing direction of vehicle.
     * Handles attempts to create bullet out of bound or inside another entity.
     *
     * @param vehicle author of bullet.
     */
    public void shoot(Vehicle vehicle) {
        int bulletSpawnRow = vehicle.getRowPos() + vehicle.getFacingDirection()[0];
        int bulletSpawnCol = vehicle.getColPos() + vehicle.getFacingDirection()[1];
        if (gameBoard.isOutOfBounds(bulletSpawnRow, bulletSpawnCol)) {
            LOGGER.fine("Attempted to create bullet out of bounds by " + vehicle);

            // if bullet would spawn inside another entity, explode right away without creating it
        } else if (!gameBoard.isTileEmpty(bulletSpawnRow, bulletSpawnCol)) {
            explodeTiles(bulletSpawnRow, bulletSpawnCol, vehicle.getExplosionRadius(), vehicle.getDamage(), vehicle);
        } else {
            LOGGER.finest("Bullet created by " + vehicle);
            Bullet b = new Bullet(bulletSpawnRow, bulletSpawnCol,
                    vehicle.getFacingDirection(),
                    false,
                    vehicle.getDamage(),
                    vehicle.getExplosionRadius(),
                    vehicle.getRange(), System.nanoTime(), vehicle);

            bulletDriver.addBullet(b);
            gameBoard.setTileContent(bulletSpawnRow, bulletSpawnCol, b);
            gameLoop.addPosition(new int[]{bulletSpawnRow, bulletSpawnCol});
        }
    }

    /**
     * Saves player stats to corresponding playerfile.
     *
     * @param playerStats stats to save for player in format {health, armor, damage}.
     */
    //params are entityname, pos, pos, facing dir, health, armor, range, dmg, explosion radius
    public void savePlayerStats(int[] playerStats) {
        String playerPath = Constants.pathToPlayers + "/" + playerName + Constants.fileTypeSuffix;
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(playerPath);
            myWriter.write("player 0 0 up " + playerStats[0] + " " + playerStats[1] + " " + "7 " + playerStats[2] + " 1");
            myWriter.close();
        } catch (IOException e) {
            LOGGER.severe("Failed to save player stats due to file issue with " + playerName);
            throw new RuntimeException(e);
        }
    }

    /**
     * Increments corresponding playerscore in score file.
     */
    public void saveScore() {
        ArrayList<String[]> scoreList = loadScore();
        boolean matched = false;
        for (String[] line : scoreList) {
            if (line[0].equals(playerName)) {
                int score = Integer.parseInt(line[1]);
                line[1] = Integer.toString(score + player.getScore());
                matched = true;
            }
        }
        if (!matched) {
            scoreList.add(new String[]{playerName, Integer.toString(player.getScore())});
        }
        writeScoreToFile(scoreList);
    }

    private ArrayList<String[]> loadScore() {
        Scanner s;
        try {
            s = new Scanner(new File(Constants.pathToScore));
        } catch (FileNotFoundException e) {
            LOGGER.severe("Failed to find scoreFile.");
            throw new RuntimeException(e);
        }
        ArrayList<String[]> scoreList = new ArrayList<>();
        while (s.hasNextLine()) {
            scoreList.add(s.nextLine().trim().split("\\s+"));
        }
        s.close();
        return scoreList;
    }

    private void writeScoreToFile(ArrayList<String[]> toWrite) {
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(Constants.pathToScore);
            for (String[] line : toWrite) {
                myWriter.write(line[0] + " " + line[1] + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            LOGGER.severe("Failed to write player score to file.");
            throw new RuntimeException(e);
        }
    }
}
