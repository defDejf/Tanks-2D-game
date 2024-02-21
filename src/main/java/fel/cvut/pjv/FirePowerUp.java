package fel.cvut.pjv;

import java.util.logging.Logger;

/**
 * Class increments damage upon activation by a vehicle.
 */
public class FirePowerUp extends GameEntity {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final int firePowerIncrement;

    /**
     * FirePowerUp constructor.
     *
     * @param rowPos             Row position on board to set - make sure it is not out of bounds.
     * @param colPos             Column position on board to set - make sure it is not out of bounds.
     * @param facingDirection    It is recommended to always use "up", deviating will not cause errors but might be confusing to maintain.
     * @param isTransparent      It is recommended to always use true - upon using false it will not be possible to activate as it can not be driven on.
     * @param firePowerIncrement Value to add to vehicles armor. Recommended to use positive values.
     */
    public FirePowerUp(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int firePowerIncrement) {
        super(rowPos, colPos, facingDirection, isTransparent, Constants.firepowerUpSpriteType);
        this.firePowerIncrement = firePowerIncrement;
    }

    /**
     * Gets how many damage points will be added to vehicle damage.
     */
    public int getFirePowerIncrement() {
        return firePowerIncrement;
    }

    @Override
    public boolean getHit(ExplosionTile e) {
        return true;
    }

    @Override
    public void getDrivenOn(Vehicle v) {
        LOGGER.finer("FirePowerUp activated by " + v);
        v.setDamage(Math.min(v.getDamage() + firePowerIncrement, Constants.maxDamage));
    }
}
