package fel.cvut.pjv;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Renders Main Menu.
 */
public class MenuView {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final JFrame mainWindow;
    private final JList<String> playerJList;
    private final JList<String> levelJList;
    private final JList<String> scoreJlist;
    private JButton startGameBtn;
    private JButton createPlayerBtn;
    private JButton quitBtn;

    /**
     * MenuView constructor.
     *
     * @param createPlayerListener listener for createPlayer button
     * @param quitListener         listener for quitGame button
     * @param startListener        listener for start button
     */
    public MenuView(ActionListener startListener, ActionListener quitListener, ActionListener createPlayerListener) {
        this.mainWindow = new JFrame(Constants.mainWindowTitle);
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.setMinimumSize(new Dimension(Constants.mainWinMinWidth, Constants.mainWinMinHeight));
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        mainWindow.setMaximumSize(d);
        mainWindow.setSize(d);
        initStartButton(startListener);
        initQuitButton(quitListener);
        initCreatePlayerButton(createPlayerListener);

        this.playerJList = new JList<>();
        this.levelJList = new JList<>();
        this.scoreJlist = new JList<>();
        setJListVisual();
    }

    private void initCreatePlayerButton(ActionListener createPlayerListener) {
        createPlayerBtn = new JButton(Constants.createPlayerBtnTitle);
        createPlayerBtn.addActionListener(createPlayerListener);
        createPlayerBtn.setBackground(new Color(26,83,92));
        createPlayerBtn.setForeground(Color.white);
        createPlayerBtn.setBorder(new LineBorder(Color.black, 2));
    }

    private void initQuitButton(ActionListener quitListener) {
        quitBtn = new JButton(Constants.quitBtnTitle);
        quitBtn.addActionListener(quitListener);
        quitBtn.setBackground(new Color(26,83,92));
        quitBtn.setForeground(Color.white);
        quitBtn.setBorder(new LineBorder(Color.black, 2));
    }

    private void initStartButton(ActionListener al) {
        startGameBtn = new JButton(Constants.startBtnTitle);
        startGameBtn.addActionListener(al);
        startGameBtn.setBackground(new Color(235,81,96));
        startGameBtn.setForeground(Color.white);
        startGameBtn.setBorder(new LineBorder(Color.black, 2));
    }

    private void setJListVisual(){
        Color JListCol = new Color(78,205,196);
        playerJList.setBackground(JListCol);
        levelJList.setBackground(JListCol);
        scoreJlist.setBackground(JListCol);
        Border b = new MatteBorder(0, 2, 2, 2, Color.black);
        scoreJlist.setBorder(b);
        playerJList.setBorder(b);
        levelJList.setBorder(b);
    }

    /**
     * Gets player selected by user.
     *
     * @return returns false if no player was selected or if list is empty.
     */
    public String getSelectedPlayer() {
        if (playerJList == null) {
            return null;
        } else {
            return playerJList.getSelectedValue();
        }
    }

    /**
     * Gets level selected by user.
     *
     * @return returns false if no player was selected or if list is empty.
     */
    public String getSelectedLevel() {
        if (playerJList == null) {
            return null;
        } else {
            return levelJList.getSelectedValue();
        }
    }

    private File[] listSaves(String folderName) {
        File folder = new File(folderName);
        if (!folder.exists()){
            try {
                Files.createDirectories(Paths.get(folderName));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return folder.listFiles();
    }

    /**
     * Loads GUI lists of levels, players and loads and sorts scoreboard.
     */
    public void initJlists() {
        File[] playerFiles = listSaves(Constants.pathToPlayers);
        File[] levelFiles = listSaves(Constants.pathToLevels);
        DefaultListModel playerModel = new DefaultListModel<>(); // get clear models
        DefaultListModel levelModel = new DefaultListModel<>();
        for (File playerFile : playerFiles) { // load model with all current player names
            if (playerFile.isFile()) {
                String name = playerFile.getName();
                name = name.substring(0, name.lastIndexOf('.'));
                playerModel.addElement(name);
            }
        }
        for (File levelFile : levelFiles) { // load model with all current level names
            if (levelFile.isFile()) {
                String name = levelFile.getName();
                name = name.substring(0, name.lastIndexOf('.'));
                levelModel.addElement(name);
            }
        }
        playerJList.setModel(playerModel);
        levelJList.setModel(levelModel);
        setSortedScoreJlist();
    }

    private void setSortedScoreJlist() {
        DefaultListModel scoreModel = new DefaultListModel<>();
        Scanner s;
        File scoreFile = new File(Constants.pathToScore);
        if (!scoreFile.exists()){
            try {
                Files.createFile(Paths.get(Constants.pathToScore));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            s = new Scanner(scoreFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> scoreList = new ArrayList<>();
        while (s.hasNextLine()) {
            scoreList.add(s.nextLine().trim());
        }
        s.close();
        // sort score list by score descending
        scoreList.sort(Comparator.comparing(t -> -Integer.parseInt((t.split("\\s+"))[1])));
        for (String score : scoreList) { // add score to model
            scoreModel.addElement(score);
        }
        scoreJlist.setModel(scoreModel);
        scoreJlist.setSelectionModel(new NoSelectionModel()); // score is only for display, selecting forbidden
    }

    private JScrollPane createScrollableJlist(JList<?> jl) {
        DefaultListCellRenderer tmpRen = (DefaultListCellRenderer) jl.getCellRenderer();
        tmpRen.setHorizontalAlignment(SwingConstants.CENTER);
        return new JScrollPane(jl);
    }

    /**
     * Sets and displays main window. After calling this method user can start interaction with the app.
     */
    public void setMainWindow() {
        initJlists();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JLabel[] titles = initTitles();
        mainPanel.addComponentListener(new DynamicResizingListener(titles, mainPanel, mainPanel, 55));

        mainPanel.add(createPageTop(titles), BorderLayout.NORTH);

        mainPanel.add(createPageMiddle(mainPanel), BorderLayout.CENTER);

        mainPanel.addComponentListener(new DynamicResizingListener(new JButton[]{startGameBtn, quitBtn, createPlayerBtn}, mainPanel, mainPanel, 55));
        mainPanel.add(createPageEnd(), BorderLayout.SOUTH);
        mainWindow.add(mainPanel);
        mainWindow.toFront();
        mainWindow.requestFocus();
        mainWindow.setVisible(true);

    }


    private JPanel createPageMiddle(JPanel mainPanel) {
        JPanel middleGrid = new JPanel(new GridLayout(1, 3));
        middleGrid.addComponentListener(new DynamicResizingListener(new JList[]{playerJList, levelJList, scoreJlist}, middleGrid, mainPanel, 75));
        middleGrid.add(createScrollableJlist(scoreJlist));
        middleGrid.add(createScrollableJlist(levelJList));
        middleGrid.add(createScrollableJlist(playerJList));
        return middleGrid;
    }

    private JPanel createPageTop(JLabel[] titles) {
        JPanel topGrid = new JPanel(new GridLayout());
        topGrid.add(titles[0]);
        topGrid.add(titles[1]);
        topGrid.add(titles[2]);
        return topGrid;
    }

    private JLabel[] initTitles(){
        Border b = new MatteBorder(0, 2, 2, 2, Color.black);
        JLabel[] titleLabels = {new JLabel(Constants.lineStartTitle, SwingConstants.CENTER),
                           new JLabel(Constants.centerTitle, SwingConstants.CENTER),
                           new JLabel(Constants.lineEndTitle, SwingConstants.CENTER)};
        for (JLabel t : titleLabels) {
            t.setBorder(b);
            t.setOpaque(true);
            t.setBackground(new Color(26,83,92));
            t.setForeground(Color.white);
        }
        return titleLabels;
    }

    private JPanel createPageEnd() {
        JPanel endPanel = new JPanel(new GridLayout(1, 3));
        endPanel.add(quitBtn);
        endPanel.add(startGameBtn);
        endPanel.add(createPlayerBtn);
        return endPanel;
    }

    /**
     * Creates warning popup with custom message and title.
     */
    public void createWarningPopup(String message, String title) {
        JOptionPane.showMessageDialog(mainWindow, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Creates dialog exit dialog.
     *
     * @return return value 0 means user clicked EXIT GAME, all other values mean return to main menu
     */
    public int createExitDialog() {
        String[] optionList = new String[2];
        optionList[0] = "Exit Game";
        optionList[1] = "Return to Main Menu";
        int value = JOptionPane.showOptionDialog(
                null,
                "Exiting game - Are you sure??",
                "Exit game?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionList, optionList[0]);
        return value;
    }

    /**
     * Creates input dialog to enter player name.
     *
     * @return if nothing or only whitespaces are entered, returns null.
     */
    public String createPlayerCreationDialog() {
        String ret = JOptionPane.showInputDialog(null,
                "Choose player Name",
                "Create new Player",
                JOptionPane.QUESTION_MESSAGE);
        if (ret != null) { // remove all whitespaces
            ret = ret.replaceAll("\\s+", "");
            if (ret.length() < 1) {
                ret = null;
            }
        }
        return ret;
    }

    /**
     * Creates information popup with custom message.
     */
    public void createInfoMsg(String info) {
        JOptionPane.showMessageDialog(mainWindow, info);
    }

    /**
     * Sets MainMenu visibility. If menu is made visible revalidate() is called.
     */
    public void setVisibility(boolean vis) {
        LOGGER.fine("Menu visibility changed to: " + vis);
        mainWindow.setVisible(vis);
        if (vis) {
            mainWindow.toFront();
            mainWindow.requestFocus();
            mainWindow.revalidate();
        }
    }

    private static class NoSelectionModel extends DefaultListSelectionModel {

        @Override
        public void setAnchorSelectionIndex(final int anchorIndex) {
        }

        @Override
        public void setLeadAnchorNotificationEnabled(final boolean flag) {
        }

        @Override
        public void setLeadSelectionIndex(final int leadIndex) {
        }

        @Override
        public void setSelectionInterval(final int index0, final int index1) {
        }
    }
}
