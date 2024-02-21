package fel.cvut.pjv;

import java.util.logging.Logger;

/**
 * Explodes upon being driven on by a Vehicle.
 */
public class Mine extends Explosive {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    GameController gameController;

    public Mine(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int damage, int explosionRadius, GameController gameController) {
        super(rowPos, colPos, facingDirection, isTransparent, damage, explosionRadius, Constants.mineTypeSprite, null);
        this.gameController = gameController;
    }

    /**
     * Requests GameController to explode tiles in radius around this after this gets driven on.
     *
     * @param v vehicle that activated the mine.
     */
    @Override
    public void getDrivenOn(Vehicle v) {
        LOGGER.finer("Mine activated by " + v);
        gameController.explodeTiles(this.getRowPos(), this.getColPos(), this.getExplosionRadius(), this.getDamage(), null);
    }
}
