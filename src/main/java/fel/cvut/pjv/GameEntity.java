package fel.cvut.pjv;

/**
 * Is parent class of all objects in game that can be set in BoardTiles.
 */
abstract class GameEntity {
    private final String spriteType;
    boolean isTransparent;
    // right:[0,1] / left: [0,-1] / up:[-1,0] / down:[1,0]} -> [row_increment, column_increment]
    private int[] facingDirection;
    private int rowPos;
    private int colPos;

    /**
     * GameEntity constructor.
     *
     * @param rowPos          Row position on board to set - make sure it is not out of bounds.
     * @param colPos          Column position on board to set - make sure it is not out of bounds.
     * @param facingDirection Is used to determine sprite direction and movement direction.
     * @param isTransparent   It is recommended to always use true - upon using false it will not be possible to activate as it can not be driven on.
     * @param spriteType      identifier which sprite to load in gameView.
     * @see GameView
     */
    public GameEntity(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, String spriteType) {
        this.rowPos = rowPos;
        this.colPos = colPos;
        this.facingDirection = facingDirection;
        this.isTransparent = isTransparent;
        this.spriteType = spriteType;
    }

    /**
     * Gets sprite type do be displayed by view. Direction is sprite is handled inside view.
     */
    public String getSpriteType() {
        return spriteType;
    }

    /**
     * Gets facing direction of entity.
     * For non-movable entities default value is "up" or {-1,0}.
     *
     * @return {-1,0} means up,
     * {0,1} means right,
     * {1,0} means down,
     * {0,-1} means left.
     */
    public int[] getFacingDirection() {
        return facingDirection;
    }

    /**
     * Sets facing direction of this.
     * Should be only used on entities which can be moved.
     * {-1,0} means up,
     * {0,1} means right,
     * {1,0} means down,
     * {0,-1} means left.
     */
    public void setFacingDirection(int[] direction) {
        this.facingDirection = direction;
    }

    /**
     * Gets row position of this.
     */
    public int getRowPos() {
        return rowPos;
    }

    protected void setRowPos(int rowPos) {
        this.rowPos = rowPos;
    }

    /**
     * Gets column position of this.
     */
    public int getColPos() {
        return colPos;
    }

    protected void setColPos(int colPos) {
        this.colPos = colPos;
    }

    /**
     * Handles getting hit by explosions. Return value dictates whether it was destroyed by the hit or no.
     * Gets called by GameController upon exploding tile that contains this entity.
     *
     * @param e Explosion tile that hit the entity - must not be null.
     * @return True means destroyed, false means survived.
     * @see GameController
     */
    public abstract boolean getHit(ExplosionTile e);

    /**
     * Handles getting driven on by a vehicle. Is usually empty for non-transparent entities.
     */
    public abstract void getDrivenOn(Vehicle v);
}
