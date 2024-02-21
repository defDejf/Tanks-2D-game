package fel.cvut.pjv;

/**
 * Is identical to vehicle.
 * Kept as separate class due to possible future implementation of class-specific behavior.
 */
public class PlayerTank extends Vehicle {
    public PlayerTank(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int health, int armor, int range, int damage, int explosionRadius) {
        super(rowPos, colPos, facingDirection, isTransparent, health, armor, range, damage, explosionRadius, Constants.playerSpriteType);
    }

    /**
     * Adds score for a hit.
     * Gets called by the entity that was hit to ensure score is not added for hitting Rocks and such.
     */
    @Override
    public void addScore(int increment) {
        setScore(getScore() + increment);
    }
}
