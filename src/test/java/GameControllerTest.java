import fel.cvut.pjv.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class GameControllerTest {
    GameController tested;
    @Before
    public void cleanup(){
        tested = new GameController(new MenuController());
        tested.setGameBoardFromFile("emptyL","TestPlayer");
    }
    @Test
    public void testMoveOutOfBounds(){
        int[] orig_pos = new int[]{0,0};
        Vehicle v = new Vehicle(0,0,new int[]{-1,0}, true,1,1,1,1,1, Constants.playerSpriteType);
        tested.moveVehicle(v, new int[]{-1,0});
        int[] newpos = new int[]{v.getRowPos(),v.getColPos()};
        assert Arrays.equals(orig_pos,newpos);
    }

    @Test (expected = NullPointerException.class) // position where vehicle rotated has to be added to gameloop which is null
    public void testRotate(){
        int[] orig_pos = new int[]{0,0};
        int[] new_facing_dir = new int[]{1,0};
        Vehicle v = new Vehicle(0,0,new int[]{-1,0}, true,1,1,1,1,1, Constants.playerSpriteType);
        tested.moveVehicle(v, new_facing_dir);
        int[] newpos = new int[]{v.getRowPos(),v.getColPos()};
        assert Arrays.equals(orig_pos,newpos);
        int[] changed_dir =  v.getFacingDirection();
        assert Arrays.equals(changed_dir, new_facing_dir);
    }

    @Test
    public void testMoveOnOccupied(){
        tested.setGameBoardFromFile("TestLevel", "TestPlayer");
        tested.startGame("newplay");
        int[] orig_blocked_pos = new int[]{0,0};
        Vehicle blocked = new Vehicle(0,0,new int[]{0,1}, true,1,1,1,1,1, Constants.playerSpriteType);
        tested.moveVehicle(blocked, new int[]{0,1});
        int[] new_blocked_pos = new int[]{blocked.getRowPos(), blocked.getColPos()};
        assert Arrays.equals(orig_blocked_pos, new_blocked_pos);
    }

    @Test
    public void testMoveBulletOutOfBounds(){
        tested.setGameBoardFromFile("TestLevel", "TestPlayer");
        tested.startGame("newplay");
        Bullet b = new Bullet(0,0,new int[]{-1,0}, true,1,1,1,1,null);
        tested.moveBullet(b);
        assert b.isHasExploded();
    }

    @Test
    public void testMoveBulletOnOccupied(){
        tested.setGameBoardFromFile("TestLevel", "TestPlayer");
        tested.startGame("newplay");
        Bullet b = new Bullet(0,0,new int[]{0,1}, true,1,1,1,1,null);
        tested.moveBullet(b);
        assert b.isHasExploded();
    }
}
