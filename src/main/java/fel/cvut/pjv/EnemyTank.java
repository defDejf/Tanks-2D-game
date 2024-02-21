package fel.cvut.pjv;

/**
 * Is identical to vehicle.
 * Kept as separate class due to possible future implementation of class-specific behavior.
 * Unlike vehicle, this can be submitted to EnemyTankDriver.
 */
public class EnemyTank extends Vehicle {
    public EnemyTank(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int health, int armor, int range, int damage, int explosionRadius) {
        super(rowPos, colPos, facingDirection, isTransparent, health, armor, range, damage, explosionRadius, Constants.enemyTankSpriteType);
    }
}
