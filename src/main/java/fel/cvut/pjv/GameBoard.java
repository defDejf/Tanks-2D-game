package fel.cvut.pjv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * Acts as middle man between BoardTile instances and GameController.
 */
public class GameBoard {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final GameController gc;
    protected int[] playerVals;
    protected BoardTile[][] boardArray;
    private PlayerTank player;

    /**
     * GameBoard constructor.
     *
     * @param gc reference to GameController, used to pass it on to drivers that need it.
     */
    public GameBoard(GameController gc) {
        this.gc = gc;
    }

    /**
     * Initializes board prior to starting game. Processes level file and sets entities on appropriate tiles.
     * Creates drivers for entities that need it.
     *
     * @param levelName  level to be loaded.
     * @param playerName player to be loaded.
     */
    public void setBoardFromFile(String levelName, String playerName) {
        boolean playerGenerated = false;
        initBoardArray();
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.pathToLevels + "/" + levelName + Constants.fileTypeSuffix))) {
            String line;
            setPlayerVals(playerName);
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.strip().split("\\s+"); // split by whitespace
                int posRow = 0;
                int posCol = 0;
                try {
                    posRow = Math.min(Integer.parseInt(splitLine[1]), Constants.boardSize-1);
                    posCol = Math.min(Integer.parseInt(splitLine[2]), Constants.boardSize-1);
                } catch (NumberFormatException e){
                    LOGGER.warning("Failed to parse position from entity " + splitLine[0]);
                }

                // get which entity to parse for and create it
                switch (splitLine[0]) {
                    case "enemytank":
                        if (isTileEmpty(posRow, posCol)) {
                            LOGGER.finer("Creating enemy tank");
                            boardArray[posRow][posCol].setContains(createEnemyTank(splitLine));
                        } else {
                            LOGGER.warning("Position " + posRow + " " + posCol + " is already occupied");
                        }
                        break;
                    case "firepowerup":
                        if (isTileEmpty(posRow, posCol)) {
                            LOGGER.finer("Creating firepowerup");
                            boardArray[posRow][posCol].setContains(createFirePowerUp(splitLine));
                        } else {
                            LOGGER.warning("Position " + posRow + " " + posCol + " is already occupied");
                        }
                        break;
                    case "armorup":
                        if (isTileEmpty(posRow, posCol)) {
                            LOGGER.finer("Creating firepowerup");
                            boardArray[posRow][posCol].setContains(createArmorUp(splitLine));
                        } else {
                            LOGGER.warning("Position " + posRow + " " + posCol + " is already occupied");
                        }
                        break;
                    case "healthup":
                        if (isTileEmpty(posRow, posCol)) {
                            LOGGER.finer("Creating healthup");
                            boardArray[posRow][posCol].setContains(createHealthUp(splitLine));
                        } else {
                            LOGGER.warning("Position " + posRow + " " + posCol + " is already occupied");
                        }
                        break;
                    case "rock":
                        if (isTileEmpty(posRow, posCol)) {
                            LOGGER.finer("Creating firepowerup");
                            boardArray[posRow][posCol].setContains(createRock(splitLine));
                        } else {
                            LOGGER.warning("Position " + posRow + " " + posCol + " is already occupied");
                        }
                        break;
                    case "playertank":
                        int playerStartRow = Integer.parseInt(splitLine[1]);
                        int playerStartCol = Integer.parseInt(splitLine[2]);
                        boolean isTransparent = false;
                        int[] facing = directionDecoder("up");
                        if (!playerGenerated){
                            if (isTileEmpty(posRow, posCol)) {
                                LOGGER.finer("Creating playerTank");
                                player = new PlayerTank(playerStartRow, playerStartCol, facing, isTransparent, playerVals[2], playerVals[3], playerVals[4], playerVals[5], playerVals[6]);
                                boardArray[posRow][posCol].setContains(player);
                                playerGenerated = true;
                            } else {
                                LOGGER.severe("Player can not be set on occupied position");
                                throw new RuntimeException("Can not set player on occupied position");
                            }
                        }
                        break;
                    case "mine":
                        int rowPos = Integer.parseInt(splitLine[1]);
                        int colPos = Integer.parseInt(splitLine[2]);
                        int[] dir = directionDecoder("up");
                        int dmg = Integer.parseInt(splitLine[4]);
                        int explosionRadius = Integer.parseInt(splitLine[5]);
                        if (isTileEmpty(posRow, posCol)) {
                            LOGGER.finer("Creating mine");
                            boardArray[posRow][posCol].setContains(new Mine(rowPos, colPos, dir, true, dmg, explosionRadius, gc));
                        } else {
                            LOGGER.warning("Position " + posRow + " " + posCol + " is already occupied");
                        }
                        break;
                    default:
                        LOGGER.warning("Failed to parse any entity - make sure level file is correct");
                        break;
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Error during gameBoard setup due to issues with file " + levelName);
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads saved player values from player file. They are used to create PlayerTank instance during board initialization.
     *
     * @param playerName player to be loaded.
     */
    public void setPlayerVals(String playerName) {
        try (BufferedReader br1 = new BufferedReader(new FileReader(Constants.pathToPlayers + "/" + playerName + Constants.fileTypeSuffix))) {
            String[] splitLine;
            try {
                splitLine = br1.readLine().split("\\s+");
            } catch (NullPointerException e) {
                LOGGER.severe("Error during loading player values, file " + playerName + " is empty.");
                throw new RuntimeException(e);
            }
            playerVals = parseForVehicle(splitLine);
        } catch (IOException e) {
            LOGGER.severe("Error during loading player values due to issues with file " + playerName);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns playerTank reference which is needed for GameLoop and others.
     *
     * @see GameLoop
     */
    protected PlayerTank getPlayer() {
        return player;
    }

    /**
     * Fetches content of tile on specified position.
     *
     * @param row    row in boardArray.
     * @param column column in boardArray.
     * @return Returns null if requested tile is out of bounds or if it is empty. Otherwise, returns GameEntity at requested position.
     */
    public GameEntity getTileContent(int row, int column) {
        if (isOutOfBounds(row, column)) {
            LOGGER.warning("Attempted to get content out of gameBoard at pos " + row + " " + column);
            return null;
        } else {
            return boardArray[row][column].getContains();
        }
    }

    /**
     * Sets content of tile on specified position. If requested position is out of bounds the incident is logged but no error occurs.
     *
     * @param row    row in boardArray.
     * @param column column in boardArray.
     */
    public void setTileContent(int row, int column, GameEntity gameEntity) {
        if (isOutOfBounds(row, column)) {
            LOGGER.warning("Attempted to set content out of gameBoard at pos " + row + " " + column);
        } else {
            boardArray[row][column].setContains(gameEntity);
        }
    }

    /**
     * Removes content of tile on specified position. If requested position is out of bounds the incident is logged but no error occurs.
     *
     * @param row    row in boardArray.
     * @param column column in boardArray.
     */
    public void removeTileContent(int row, int column) {
        if (isOutOfBounds(row, column)) {
            LOGGER.warning("Attempted to remove content out of gameBoard at pos " + row + " " + column);
        } else {
            boardArray[row][column].removeContent();
        }
    }

    /**
     * Gets whether tile on specified position may be moved on. All tiles that are empty or contain a transparent entity may be moved on.
     *
     * @param row    row in boardArray.
     * @param column column in boardArray.
     * @return returns true if tile can be moved on and false if it can not be moved on or is out of bounds.
     */
    public boolean canTileBeMovedOn(int row, int column) {
        if (isOutOfBounds(row, column)) {
            return false;
        } else {
            return boardArray[row][column].getCanBeMovedOn();
        }
    }

    /**
     * Gets whether tile on specified position is empty. Tiles out of bounds are considered empty.
     *
     * @param row    row in boardArray.
     * @param column column in boardArray.
     * @return returns true if tile can be moved on and false if it can not be moved on or is out of bounds.
     */
    public boolean isTileEmpty(int row, int column) {
        if (isOutOfBounds(row, column)) {
            return true;
        } else {
            return boardArray[row][column].isEmpty();
        }
    }

    /**
     * Gets whether tile on specified position is out of bounds.
     *
     * @param row    row in boardArray.
     * @param column column in boardArray.
     * @return returns true if tile is out of bounds
     */
    public boolean isOutOfBounds(int row, int column) {
        // square board size * size
        return (row < 0 || row > Constants.boardSize - 1 || column < 0 || column > Constants.boardSize - 1);
    }

    private void initBoardArray() {
        boardArray = new BoardTile[Constants.boardSize][Constants.boardSize];
        for (int i = 0; i < Constants.boardSize; i++) {
            for (int j = 0; j < Constants.boardSize; j++) {
                boardArray[i][j] = new BoardTile();
            }
        }
    }

    private int[] directionDecoder(String dir) {
        int[] ret = new int[2];
        switch (dir) {
            case "down":
                ret[0] = 1;
                ret[1] = 0;
                break;
            case "left":
                ret[0] = 0;
                ret[1] = -1;
                break;
            case "right":
                ret[0] = 0;
                ret[1] = 1;
                break;
            default:
                ret[0] = -1;
                ret[1] = 0;
                break;
        }
        return ret;
    }

    private EnemyTank createEnemyTank(String[] splitParams) {
        boolean isTransparent = false;
        int[] facing = directionDecoder(splitParams[3]);
        int[] vehicleVals = parseForVehicle(splitParams);
        EnemyTank e = null;
        if (vehicleVals != null) {
            e = new EnemyTank(vehicleVals[0], vehicleVals[1], facing, isTransparent, vehicleVals[2], vehicleVals[3], vehicleVals[4], vehicleVals[5], vehicleVals[6]);
            gc.addAi(new EnemyTankDriver(e, gc));
        }
        return e;
    }

    private ArmorUp createArmorUp(String[] splitParams) {
        boolean isTransparent = true;
        int[] powerUpVals = parseForPowerUp(splitParams);
        ArmorUp ar = null;
        if (powerUpVals != null) {
            ar = new ArmorUp(powerUpVals[0], powerUpVals[1], new int[]{-1, 0}, isTransparent, powerUpVals[2]);
        }
        return ar;
    }

    private HealthUp createHealthUp(String[] splitParams) {
        boolean isTransparent = true;
        int[] powerUpVals = parseForPowerUp(splitParams);
        HealthUp he = null;
        if (powerUpVals != null) {
            he = new HealthUp(powerUpVals[0], powerUpVals[1], new int[]{-1, 0}, isTransparent, powerUpVals[2]);
        }
        return he;
    }

    private FirePowerUp createFirePowerUp(String[] splitParams) {
        boolean isTransparent = true;
        int[] powerUpVals = parseForPowerUp(splitParams);
        FirePowerUp fi = null;
        if (powerUpVals != null) {
            fi = new FirePowerUp(powerUpVals[0], powerUpVals[1], new int[]{-1, 0}, isTransparent, powerUpVals[2]);
        }
        return fi;
    }

    private Rock createRock(String[] splitParams) {
        boolean isTransparent = false;
        int rowPos = Integer.parseInt(splitParams[1]);
        int colPos = Integer.parseInt(splitParams[2]);
        Rock r = null;
        if (!isOutOfBounds(rowPos, colPos)) {
            r = new Rock(rowPos, colPos, new int[]{-1, 0}, isTransparent);
        } else {
            LOGGER.warning("Attempted to set content out of gameBoard at pos " + rowPos + " " + colPos);
        }
        return r;
    }

    private int[] parseForVehicle(String[] splitParams) {
        if (splitParams.length < 8) {
            LOGGER.severe("Vehicle params were the wrong length");
            throw new RuntimeException("ERROR PARSING VEHICLE PARAMETERS - MAKE SURE THEY ARE CORRECT");
        }
        int rowPos;
        int colPos;
        int health;
        int armor;
        int range;
        int damage;
        int explosionRadius;
        try {
            rowPos = Integer.parseInt(splitParams[1]);
            colPos = Integer.parseInt(splitParams[2]);
            health = Integer.parseInt(splitParams[4]);
            armor = Integer.parseInt(splitParams[5]);
            range = Integer.parseInt(splitParams[6]);
            damage = Integer.parseInt(splitParams[7]);
            explosionRadius = Integer.parseInt(splitParams[8]);
        } catch (NumberFormatException e) {
            rowPos = ThreadLocalRandom.current().nextInt(0, Constants.boardSize);
            colPos = ThreadLocalRandom.current().nextInt(0, Constants.boardSize);
            health = 5;
            armor = 5;
            range = 5;
            damage = 5;
            explosionRadius = 0;
            LOGGER.warning("ERROR PARSING VEHICLE PARAMETERS, " +
                    "Vehicle was created with default params on random position which could be occupied");
        }
        health = Math.min(health, Constants.maxHealth);
        armor = Math.min(armor, Constants.maxArmor);
        damage = Math.min(damage, Constants.maxDamage);
        range = Math.max(range, 1);
        explosionRadius = Math.min(explosionRadius, Constants.boardSize/2);

        return new int[]{rowPos, colPos, health, armor, range, damage, explosionRadius};
    }

    private int[] parseForPowerUp(String[] splitParams) {
        if (splitParams.length < 4) {
            LOGGER.severe("Powerup params were the wrong length");
            throw new RuntimeException("ERROR PARSING POWERUP PARAMETERS - MAKE SURE THEY ARE CORRECT");
        }
        int rowPos;
        int colPos;
        int increment;
        try {
            rowPos = Integer.parseInt(splitParams[1]);
            colPos = Integer.parseInt(splitParams[2]);
            increment = Integer.parseInt(splitParams[3]);
        } catch (NumberFormatException e){
            rowPos = ThreadLocalRandom.current().nextInt(0, Constants.boardSize);
            colPos = ThreadLocalRandom.current().nextInt(0, Constants.boardSize);
            increment = 5;
            LOGGER.warning("ERROR PARSING POWERUP PARAMETERS, " +
                    "Vehicle was created with default params on random position which could be occupied");
        }
        increment = Math.max(1, increment);
        return new int[]{rowPos, colPos, increment};
    }

    public BoardTile[][] getBoardArray() {
        return boardArray;
    }

    /**
     * Gets ExplosionTile on specified position. Explosion does NOT count as tile content.
     *
     * @param rowPos row in boardArray.
     * @param colPos column in boardArray.
     * @return returns null if requested pos does is not exploding or if it is out od bounds.
     */
    public ExplosionTile getExplosionAtPos(int rowPos, int colPos) {
        if (isOutOfBounds(rowPos, colPos)) {
            LOGGER.warning("Attempted to get explosion out of bounds at " + rowPos + " " + colPos);
            return null;
        } else {
            return boardArray[rowPos][colPos].getExplosion();
        }
    }

    /**
     * Sets ExplosionTile on specified position. Explosion does NOT count as tile content.
     * If requested position is out of bounds the incident is logged but no error occurs.
     *
     * @param rowPos row in boardArray.
     * @param colPos column in boardArray.
     */
    public void setExplosionAtPos(int rowPos, int colPos, ExplosionTile e) {
        if (isOutOfBounds(rowPos, colPos)) {
            LOGGER.warning("Attempted to set explosion out of bounds at " + rowPos + " " + colPos);
        } else {
            boardArray[rowPos][colPos].setExplosion(e);
        }
    }

    /**
     * Removes ExplosionTile on specified position. Explosion does NOT count as tile content.
     * If requested position is out of bounds the incident is logged but no error occurs.
     * If called on a non-exploding tile nothing happens.
     *
     * @param rowPos row in boardArray.
     * @param colPos column in boardArray.
     */
    public void removeExplosion(int rowPos, int colPos) {
        if (isOutOfBounds(rowPos, colPos)) {
            LOGGER.warning("Attempted to remove explosion out of bounds at " + rowPos + " " + colPos);
        } else {
            boardArray[rowPos][colPos].removeExplosion();
        }
    }

    /**
     * Gets whether tile on specified position is exploding.
     * If requested position is out of bounds the incident is logged but no error occurs.
     *
     * @param rowPos row in boardArray.
     * @param colPos column in boardArray.
     * @return returns false if requested position is out od bound or if it contains no explosion. Else returns true.
     */
    public boolean isTilePosExploding(int rowPos, int colPos) {
        if (isOutOfBounds(rowPos, colPos)) {
            LOGGER.warning("Attempted to get explosion status out of bounds at " + rowPos + " " + colPos);
            return false;
        } else {
            return boardArray[rowPos][colPos].isExploding();
        }
    }
}
