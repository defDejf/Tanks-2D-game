package fel.cvut.pjv;

/**
 * Explosion which is able to hit entities. Has damage property.
 * Has ticks to show, which determine how many frames it stays displayed.
 */
public class ExplosionTile extends Explosive {
    private int ticksToShow = Constants.ticksToShow;

    /**
     * ExplosionTile constructor.
     *
     * @param isTransparent value of this argument does not matter as explosions do not count as tile content.
     */
    public ExplosionTile(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int damage, Vehicle author) {
        super(rowPos, colPos, facingDirection, isTransparent, damage, 0, Constants.explosionSpriteType, author);
    }

    /**
     * Gets how many frames are left for this to be displayed.
     */
    public int getTicksToShow() {
        return ticksToShow;
    }

    /**
     * Sets how many frames are left for this should be displayed.
     */
    public void setTicksToShow(int ticksToShow) {
        this.ticksToShow = ticksToShow;
    }
}
