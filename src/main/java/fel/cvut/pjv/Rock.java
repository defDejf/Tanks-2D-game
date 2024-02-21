package fel.cvut.pjv;

/**
 * Class is used to create indestructible barriers in levels.
 */
public class Rock extends GameEntity {
    public Rock(int rowPos, int colPos, int[] facingDirection, boolean isTransparent) {
        super(rowPos, colPos, facingDirection, isTransparent, Constants.rockSpriteType);
    }

    @Override
    public boolean getHit(ExplosionTile e) {
        return false;
    }

    @Override
    public void getDrivenOn(Vehicle v) {
    }
}
