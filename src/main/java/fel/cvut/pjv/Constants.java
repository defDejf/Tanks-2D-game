package fel.cvut.pjv;

public final class Constants {

    public static final String gameName = "Tanks! 2D";
    public static final String mainWindowTitle = "Tanks! 2D : Main menu";
    public static final String lineStartTitle = "Score board";

    public static final String centerTitle = "Level select";
    public static final String lineEndTitle = "Player select";
    public static final String startBtnTitle = "Start Game!";
    public static final String quitBtnTitle = "Exit Game";
    public static final String createPlayerBtnTitle = "Create player";
    public static final String popUpWarningTitle = "Player or Level not selected";
    public static final String popUpWarningMsg = "Please select both player and level to continue";

    public static final int mainWinMinWidth = 800;
    public static final int mainWinMinHeight = 500;

    public static final String pathToPlayers = "Players";
    public static final String pathToLevels = "Levels";
    public static final String pathToScore = "ScoreBoard.txt";
    public static final String fileTypeSuffix = ".txt";
    public static final int boardSize = 20;
    public static final String pathToSprites = "/Sprites/";
    public static final String playerSpriteType = "playertank";
    public static final String enemyTankSpriteType = "enemytank";
    public static final String healthSpriteType = "repair";
    public static final String armorSpriteType = "armor";
    public static final String rockSpriteType = "rock";
    public static final String bulletSpriteType = "bullet";
    public static final String explosionSpriteType = "explosion";
    public static final String firepowerUpSpriteType = "firepower";
    public static final String mineTypeSprite = "mine";
    public static final String spriteFileType = ".png";
    public static final String backGroundSprite = "grass2.jpg";
    public static final int minmsKeyDelay = 400 * 1000000;
    public static final int bulletDelay = 70 * 1000000;
    public static final int ticksToShow = 25;
    public static final int maxArmor = 20;
    public static final int maxHealth = 100;
    public static final int maxDamage = 50;
    public static final int hitScore = 100;
    public static final int killScore = 500;

    private Constants() {
    }
}
