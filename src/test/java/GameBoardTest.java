import fel.cvut.pjv.*;
import org.junit.*;
public class GameBoardTest {
    GameBoard tested;
    @Before
    public void cleanUp(){
        tested = new GameBoard(new GameController(new MenuController()));
        tested.setBoardFromFile("emptyL", "newplay");
    }

    @Test(expected = RuntimeException.class)
    public void setUpEmptyPlayer(){
        tested.setBoardFromFile("Level1", "emptyP");
    }

    @Test
    public void setUpEmptyLevel(){
        tested.setBoardFromFile("emptyL", "newplay");
    }
    @Test()
    public void setUpCorruptedPlayer(){
        tested.setBoardFromFile("Level1", "corrup_p");
    }

    @Test(expected = RuntimeException.class)
    public void setUpCorruptedLevel(){
        tested.setBoardFromFile("corrL", "newplay");
    }

    @Test
    public void testConcurrentModificationTile(){
        tested.setBoardFromFile("emptyL", "newplay");
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) {
                    tested.setTileContent(0,0,new HealthUp(0,0,new int[]{-1,0}, false, 1));
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) {
                    tested.setTileContent(0,0,new HealthUp(0,0,new int[]{-1,0}, false, 1));
                }
            }
        });
        t1.start();
        t2.start();
    }

    @Test
    public void testSetExplosionOutOfBounds() {
        tested.setExplosionAtPos(99, 99, new ExplosionTile(0, 0, new int[]{-1, 0}, true, 1, null));
    }

    @Test
    public void testGetExplosionOutOfBounds(){
        tested.getExplosionAtPos(99, 99);
    }

    @Test
    public void testRemoveExplosionOutOfBounds(){
        tested.removeExplosion(99,99);
    }

    @Test
    public void testSetContentOutOfBound(){
        tested.setTileContent(55, 55, new Vehicle(55,55,new int[]{-1,0}, true,1,1,1,1,1, Constants.playerSpriteType));
    }

    public void testGetContentOutOfBound(){
        tested.getTileContent(99,99);
    }

    public void RemoveContentOutOfBound(){
        tested.removeTileContent(99,99);
    }

}
