package fel.cvut.pjv;

/**
 * Parent class of Bullet, Mine and Explosive tile.
 * Keeps author to increment score if a vehicle gets hit.
 */
public class Explosive extends GameEntity {
    private final Vehicle author;
    private final int damage;
    private final int explosionRadius;

    /**
     * Explosive Constructor.
     *
     * @param spriteType      what sprite should be displayed for this. Gets set by its inheriting classes.
     * @param author          vehicle which created this and
     *                        will get its score incremented if explosion of this hit another vehicle.
     * @param explosionRadius dictates how many tiles around the center will be exploded.
     *                        For example value 1 will create 3x3 explosion
     * @param isTransparent   dictates whether explosive can be driven on. Used in Mine implementation.
     */
    public Explosive(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int damage, int explosionRadius, String spriteType, Vehicle author) {
        super(rowPos, colPos, facingDirection, isTransparent, spriteType);
        this.damage = damage;
        this.explosionRadius = explosionRadius;
        this.author = author;
    }

    /**
     * Gets damage this will cause upon exploding.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets how many tiles around the center will be exploded.
     * For example value 1 will create 3x3 explosion
     */
    public int getExplosionRadius() {
        return explosionRadius;
    }

    @Override
    public boolean getHit(ExplosionTile e) {
        return true;
    }

    @Override
    public void getDrivenOn(Vehicle v) {
    }

    /**
     * Gets creator of explosive.
     *
     * @return can be null.
     */
    public Vehicle getAuthor() {
        return author;
    }
}
