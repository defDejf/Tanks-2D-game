package fel.cvut.pjv;

/**
 * Moving explosive with set range of N tiles and unchangeable direction.
 * Explodes upon contact with any entity.
 */
public class Bullet extends Explosive {
    double lastMove;
    private boolean hasExploded;
    private volatile int remainingRange;
    /**
     * Bullet constructor.
     * @param explosionRadius dictates how many tiles around the center will be exploded.
     *                        For example value 1 will create 3x3 explosion.
     * @param isTransparent should be always set to false, constructor argument is kept for future modifications.
     * @param remainingRange dictates how many tiles can bullet move on before exploding.
     */
    public Bullet(int rowPos, int colPos,
                  int[] facingDirection,
                  boolean isTransparent,
                  int damage, int explosionRadius,
                  int remainingRange,
                  double lastMove,
                  Vehicle author) {
        super(rowPos, colPos, facingDirection, isTransparent, damage, explosionRadius, Constants.bulletSpriteType, author);
        this.remainingRange = remainingRange;
        this.hasExploded = false;
        this.lastMove = lastMove;
    }
    /**
     * Gets how many tiles bullet can still move on before exploding.
     */
    public int getRemainingRange() {
        return remainingRange;
    }
    /**
     * Sets how many tiles bullet can still move on before exploding.
     */
    public void setRemainingRange(int remainingRange) {
        this.remainingRange = remainingRange;
    }
    /**
     * Checks whether bullet has already exploded. Used in BulletDriver to remove exploded bullets.
     */
    public boolean isHasExploded() {
        return hasExploded;
    }
    /**
     * Sets hasExploded property. Exploded bullets get excluded from BulletDriver and removed from board.
     */
    public void setHasExploded(boolean hasExploded) {
        this.hasExploded = hasExploded;
    }
    /**
     * Gets time of last move in nanoseconds. Used in BulletDriver to check whether bullet should be moved again.
     */
    public double getLastMove() {
        return lastMove;
    }
    /**
     * Sets time of last move in nanoseconds. Should be called with every movement of bullet.
     */
    public void setLastMove(double lastMove) {
        this.lastMove = lastMove;
    }
}
