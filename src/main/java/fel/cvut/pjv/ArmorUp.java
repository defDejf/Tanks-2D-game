package fel.cvut.pjv;

import java.util.logging.Logger;

/**
 * Increments armor upon activation by a vehicle.
 */
public class ArmorUp extends GameEntity {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    int armorIncrement;

    /**
     * ArmorUp constructor.
     *
     * @param rowPos          Row position on board to set - make sure it is not out of bounds.
     * @param colPos          Column position on board to set - make sure it is not out of bounds.
     * @param facingDirection It is recommended to always use "up", deviating will not cause errors but might be confusing to maintain.
     * @param isTransparent   It is recommended to always use true - upon using false it will not be possible to activate as it can not be driven on.
     * @param armorIncrement  Value to add to vehicles armor. Recommended to use positive values.
     */
    public ArmorUp(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int armorIncrement) {
        super(rowPos, colPos, facingDirection, isTransparent, Constants.armorSpriteType);
        this.armorIncrement = armorIncrement;
    }

    @Override
    public boolean getHit(ExplosionTile e) {
        return true;
    }

    /**
     * Takes a vehicle and increments its armor.
     * Gets called by GameController upon movement on a tile that contains this powerup.
     *
     * @param v Vehicle to have its armor incremented, must not be null.
     * @see GameController
     * @see Vehicle
     */
    @Override
    public void getDrivenOn(Vehicle v) {
        LOGGER.finer("ArmorUp activated by " + v);
        v.setArmor(Math.min(v.getArmor() + armorIncrement, Constants.maxArmor));
    }
}

