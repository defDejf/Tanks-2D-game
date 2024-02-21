package fel.cvut.pjv;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Displays game GUI and handles calls for updates of GUI.
 */
public class GameView {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final GameBoard gameBoard;
    private final JPanel container;
    private final JLabel[] paramLabels;
    private final JLabel[][] displayArray;
    private final ImageIcon[] playerIcons = new ImageIcon[4];
    private final ImageIcon[] enemyTankIcons = new ImageIcon[4];
    private final ImageIcon[] bulletIcons = new ImageIcon[4];
    int imgSize;
    Dimension squareDim;
    private JFrame gameWindow;
    private JLabel scoreLabel;
    private JPanel scorePanel;
    private JLabel gameBoardSquarePanel; //CALLED PANEL BECAUSE IT IS USED AS ONE - ITS LABEL ONLY TO SET BACKGROUND
    private int score = 0;
    private int[] playerParams = new int[]{0, 0, 0};
    private JPanel playerParamsPanel;
    private ImageIcon armorIcon;
    private ImageIcon healthIcon;
    private ImageIcon firePowerIcon;
    private ImageIcon rockIcon;
    private ImageIcon explosionIcon;
    private ImageIcon mineIcon;

    /**
     * GameView constructor.
     *
     * @param gameBoard GameBoard reference to get tile content to render appropriate sprite.
     * @param controls  Reference to key adapter used to take user input.
     */
    public GameView(GameBoard gameBoard, PlayerKeyListener controls) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = getSquareSize(d);
        squareDim = new Dimension(x - x / 12, x - x / 12);
        Dimension gameBoardDim = new Dimension(x - x / 8, x - x / 8);

        initGameWindow();
        gameWindow.setMaximumSize(d);
        gameWindow.setSize(d);

        this.gameBoard = gameBoard;
        container = new JPanel(new GridBagLayout());
        container.setBackground(Color.darkGray);

        displayArray = new JLabel[Constants.boardSize][Constants.boardSize];

        initGameBoardSquarePanel(gameBoardDim);
        container.add(gameBoardSquarePanel);

        imgSize = gameBoardSquarePanel.getWidth() / Constants.boardSize;

        initIcons();

        gameWindow.addKeyListener(controls);

        intiScorePanel();
        gameWindow.add(scorePanel, BorderLayout.EAST);

        paramLabels = new JLabel[3];
        initPlayerParamsPanel();
        gameWindow.add(playerParamsPanel, BorderLayout.WEST);
        gameWindow.add(scorePanel, BorderLayout.EAST);
    }

    private void intiScorePanel() {
        scorePanel = new JPanel(new GridLayout(1, 1));
        scorePanel.setBackground(new Color(78,205,196));
        scoreLabel = new JLabel("SCORE: " + score, JLabel.CENTER);
        scorePanel.add(scoreLabel);
    }

    private void initIcons() {
        try {
            initDirectionalIcons();
            initStaticIcons();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initGameWindow() {
        gameWindow = new JFrame(Constants.gameName);
        gameWindow.setLayout(new BorderLayout());
        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameWindow.setMinimumSize(squareDim);

    }

    /**
     * Set current player parameters to be displayed.
     *
     * @param playerParams Method expects params to be in order {health, armor, damage}.
     */
    public void setPlayerParams(int[] playerParams) {
        this.playerParams = playerParams;
    }

    private void initPlayerParamsPanel() {
        playerParamsPanel = new JPanel(new GridLayout(3, 1));
        playerParamsPanel.setBackground(new Color(78,205,196));

        paramLabels[0] = new JLabel("HEALTH: " + playerParams[0], healthIcon, JLabel.CENTER);
        paramLabels[1] = new JLabel("ARMOR: " + playerParams[1], armorIcon, JLabel.CENTER);
        paramLabels[2] = new JLabel("DAMAGE: " + playerParams[2], firePowerIcon, JLabel.CENTER);

        gameWindow.addComponentListener(new DynamicResizingListener(
                new JComponent[]{paramLabels[0], paramLabels[1], paramLabels[2], scoreLabel},
                container,
                playerParamsPanel,
                75));
        gameWindow.addComponentListener(new SidePanelsDynamicResizer(gameWindow, playerParamsPanel, scorePanel, squareDim));
        playerParamsPanel.add(paramLabels[0]);
        playerParamsPanel.add(paramLabels[1]);
        playerParamsPanel.add(paramLabels[2]);
        gameWindow.revalidate();
    }

    /**
     * Render currently set player parameters to GUI.
     */
    public void updatePlayerParams() {
        paramLabels[0].setText("HEALTH: " + playerParams[0]);
        paramLabels[1].setText("ARMOR: " + playerParams[1]);
        paramLabels[2].setText("DAMAGE: " + playerParams[2]);
    }

    /**
     * Set current player score to be displayed.
     *
     * @param score score to be set.
     */
    public void setScore(int score) {
        this.score = score;
    }

    private int getSquareSize(Dimension screenDim) {
        return (int) (Math.min(screenDim.getHeight(), screenDim.getWidth()));
    }

    private void initGameBoardSquarePanel(Dimension gameBoardDim) {
        gameBoardSquarePanel = new JLabel();
        gameBoardSquarePanel.setBorder(new LineBorder(Color.BLACK, 4));
        ImageIcon ic;
        try {
            ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.backGroundSprite));
        } catch (NullPointerException e) {
            LOGGER.severe("Failed to load background.");
            throw new RuntimeException("Failed to load background, JAR folder might be corrupted.", e);
        }
        gameBoardSquarePanel.setIcon(new ImageIcon(ic.getImage().getScaledInstance(gameBoardDim.width, gameBoardDim.height, Image.SCALE_DEFAULT)));
        gameBoardSquarePanel.setLayout(new GridLayout(Constants.boardSize, Constants.boardSize));
        gameBoardSquarePanel.setPreferredSize(gameBoardDim);
        gameBoardSquarePanel.setSize(gameBoardDim);
    }

    // SPRITES IN ARRAY ARE STORED IN ORDER: UP, RIGHT, DOWN, LEFT
    private void initDirectionalIcons() throws IOException {
        for (int i = 0; i < 4; i++) {
            try {
                ImageIcon ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.playerSpriteType + i + Constants.spriteFileType));
                playerIcons[i] = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));

                ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.enemyTankSpriteType + i + Constants.spriteFileType));
                enemyTankIcons[i] = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));

                ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.bulletSpriteType + i + Constants.spriteFileType));
                bulletIcons[i] = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));
            } catch (NullPointerException e) {
                LOGGER.severe("Failed to load sprite.");
                throw new RuntimeException("Failed to load sprite. JAR folder might be corrupted.", e);
            }
        }
    }

    private void initStaticIcons() throws IOException {
        try {
            ImageIcon ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.rockSpriteType + Constants.spriteFileType));
            rockIcon = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));

            ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.healthSpriteType + Constants.spriteFileType));
            healthIcon = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));

            ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.firepowerUpSpriteType + Constants.spriteFileType));
            firePowerIcon = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));

            ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.explosionSpriteType + Constants.spriteFileType));
            explosionIcon = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));

            ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.armorSpriteType + Constants.spriteFileType));
            armorIcon = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));

            ic = new ImageIcon(getClass().getResource(Constants.pathToSprites + Constants.mineTypeSprite + Constants.spriteFileType));
            mineIcon = new ImageIcon(ic.getImage().getScaledInstance(imgSize, imgSize, Image.SCALE_DEFAULT));
        } catch (NullPointerException e) {
            LOGGER.severe("Failed to load sprite.");
            throw new RuntimeException("Failed to load sprite. JAR folder might be corrupted.", e);
        }
    }

    /**
     * Create and load panel that displays gameboard initial gameboard setup.
     */
    public void initGraphics() {
        loadDisplayArray();
        for (JLabel[] row : displayArray) {
            for (JLabel tile : row) {
                gameBoardSquarePanel.add(tile);
            }
        }
        gameWindow.add(container, BorderLayout.CENTER);
        gameWindow.toFront();
        gameWindow.requestFocus();
        gameWindow.setVisible(true);
    }

    /**
     * Update view to current state of gameBoard.
     *
     * @param toUpdate positions where change occurred, Format is {row, column}
     */
    public synchronized void updateView(ConcurrentLinkedQueue<int[]> toUpdate) {
        for (int[] pos : toUpdate) {
            if (gameBoard.boardArray[pos[0]][pos[1]].isExploding()) {
                displayArray[pos[0]][pos[1]].setIcon(explosionIcon);
            } else if (!gameBoard.boardArray[pos[0]][pos[1]].isEmpty()) {
                displayArray[pos[0]][pos[1]].setIcon(getCorrectIcon(gameBoard.getTileContent(pos[0], pos[1])));
            } else {
                displayArray[pos[0]][pos[1]].setIcon(null);
            }
        }
    }

    /**
     * Render currently set score do GUI.
     */
    public void updateScore() {
        scoreLabel.setText("SCORE: " + score);
    }

    private void loadDisplayArray() {
        for (int i = 0; i < Constants.boardSize; i++) {
            for (int j = 0; j < Constants.boardSize; j++) {
                JLabel jl = new JLabel();
                if (!gameBoard.boardArray[i][j].isEmpty()) {
                    jl.setIcon(getCorrectIcon(gameBoard.boardArray[i][j].getContains()));
                }
                displayArray[i][j] = jl;
            }
        }
    }

    private ImageIcon getCorrectIcon(GameEntity gameEntity) {
        ImageIcon ret = null;
        switch (gameEntity.getSpriteType()) {
            case Constants.healthSpriteType:
                ret = healthIcon;
                break;
            case Constants.armorSpriteType:
                ret = armorIcon;
                break;
            case Constants.firepowerUpSpriteType:
                ret = firePowerIcon;
                break;
            case Constants.rockSpriteType:
                ret = rockIcon;
                break;
            case Constants.mineTypeSprite:
                ret = mineIcon;
                break;
            case Constants.enemyTankSpriteType:
                ret = enemyTankIcons[getSpriteDirection(gameEntity)];
                break;
            case Constants.playerSpriteType:
                ret = playerIcons[getSpriteDirection(gameEntity)];
                break;
            case Constants.bulletSpriteType:
                ret = bulletIcons[getSpriteDirection(gameEntity)];
                break;
        }
        return ret;
    }

    private int getSpriteDirection(GameEntity gameEntity) {
        int ret;
        int[] dir = gameEntity.getFacingDirection();
        if (dir[0] == -1) {
            ret = 0;
        } else if (dir[0] == 1) {
            ret = 2;
        } else if (dir[1] == 1) {
            ret = 1;
        } else {
            ret = 3;
        }
        return ret;
    }

    /**
     * Create pop-up window that gets user input to continue or quit the game.
     *
     * @return return value 0 means user clicked QUIT, all other values mean continue.
     */
    public int createQuitOrContinueDialog() {
        String[] optionList = new String[2];
        optionList[0] = "Save and quit level";
        optionList[1] = "Resume game";
        int value = JOptionPane.showOptionDialog(
                gameWindow,
                "Game paused - do you wish to continue or save and return to main menu?",
                "Game Paused",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionList, optionList[0]);
        return value;
    }

    /**
     * Create pop-up window with a message for user information.
     *
     * @param msg message to be displayed to user.
     */
    public void createInfoPopup(String msg) {
        JOptionPane.showMessageDialog(gameWindow, msg);
    }

    /**
     * Clear GUI of gameboard and hide GameView.
     */
    public void terminate() {
        gameBoardSquarePanel.removeAll();
        gameWindow.setVisible(false);
    }
}
