package fel.cvut.pjv;

import java.util.logging.Logger;

/**
 * Increments health upon activation by a vehicle.
 */
public class HealthUp extends GameEntity {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    int healthIncrement;

    /**
     * HealthUp constructor.
     *
     * @param rowPos          Row position on board to set - make sure it is not out of bounds.
     * @param colPos          Column position on board to set - make sure it is not out of bounds.
     * @param facingDirection It is recommended to always use "up", deviating will not cause errors but might be confusing to maintain.
     * @param isTransparent   It is recommended to always use true - upon using false it will not be possible to activate as it can not be driven on.
     * @param healthIncrement Value to add to vehicles health. Recommended to use positive values.
     */
    public HealthUp(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int healthIncrement) {
        super(rowPos, colPos, facingDirection, isTransparent, Constants.healthSpriteType);
        this.healthIncrement = healthIncrement;
    }

    @Override
    public boolean getHit(ExplosionTile e) {
        return true;
    }

    /**
     * Takes a vehicle and increments its health.
     * Gets called by GameController upon movement on a tile that contains this powerup.
     *
     * @param v Vehicle to have its armor incremented, must not be null.
     * @see GameController
     * @see Vehicle
     */
    @Override
    public void getDrivenOn(Vehicle v) {
        LOGGER.finer("HealthUp activated by " + v);
        v.setHealth(Math.min(v.getHealth() + healthIncrement, Constants.maxHealth));
    }
}
