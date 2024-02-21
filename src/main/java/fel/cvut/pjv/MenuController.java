package fel.cvut.pjv;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Handles user actions in main menu. Initializes GameController and MenuView.
 */
public class MenuController {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final MenuView mw;
    private final GameController gc;
    private ActionListener startListener;
    private ActionListener quitListener;
    private ActionListener createPlayerListener;

    public MenuController() {
        initStartListener();
        initQuitListener();
        initCreateListener();
        mw = new MenuView(startListener, quitListener, createPlayerListener);
        gc = new GameController(this);
    }

    /**
     * Creates and renders MainMenu window. Can be considered start the whole application for user.
     */
    public void setViewWindow() {
        mw.setMainWindow();
    }

    private void initStartListener() {
        startListener = actionEvent -> {
            String level = mw.getSelectedLevel();
            String player = mw.getSelectedPlayer();
            if (level == null || player == null) {
                LOGGER.fine("User didnt choose player or level");
                mw.createWarningPopup(Constants.popUpWarningMsg, Constants.popUpWarningTitle);
            } else {
                gc.setGameBoardFromFile(level, player);
                gc.startGame(player);
                mw.setVisibility(false);
            }
        };
    }

    private void initQuitListener() {
        quitListener = actionEvent -> {
            int ret = mw.createExitDialog();
            if (ret == 0) {
                if (gc.gameRan) { // cleanup just in case
                    gc.stopGame();
                }
                LOGGER.info("Game exited through main menu Quit button");
                System.exit(0);
            }
        };
    }

    private void initCreateListener() {
        createPlayerListener = actionEvent -> {
            String newPlayerName = mw.createPlayerCreationDialog();
            if (newPlayerName != null) {
                createPlayer(newPlayerName);
            } else {
                LOGGER.fine("User entered empty/only whitespace player name");
            }
        };
    }

    /**
     * Sets menu visibility. Refreshes lists of Players, Levels and Scores.
     */
    public void setViewVisibility(boolean b) {
        mw.initJlists();
        mw.setVisibility(b);
    }

    /**
     * Creates new basic player. Checks for duplicit names or only-whitespace names.
     * Creates input dialog in MenuView.
     */
    public void createPlayer(String playerName) {
        LOGGER.info("createPlayer was called.");
        if (checkForDuplicates(playerName)) {
            LOGGER.fine("Chosen player name already exists");
            mw.createInfoMsg("Player with this name already exists!" +
                    "\nPlease choose another one");
        } else {
            String playerPath = Constants.pathToPlayers + "/" + playerName + Constants.fileTypeSuffix;
            FileWriter myWriter;
            try {
                myWriter = new FileWriter(playerPath);
                //stats are health; armor; range; damage; explosion radius
                myWriter.write("player 0 0 up 5 6 7 8 1");
                myWriter.close();
            } catch (IOException e) {
                LOGGER.severe("Error opening player file" + playerName);
                throw new RuntimeException(e);
            }
            mw.initJlists();
        }
    }

    private boolean checkForDuplicates(String requestedName) {
        File[] playerList = new File(Constants.pathToPlayers).listFiles();
        for (File file : playerList) {
            if (file.isFile()) {
                String name = file.getName();
                name = name.substring(0, name.lastIndexOf('.'));
                if (name.equals(requestedName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
