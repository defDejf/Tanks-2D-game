package fel.cvut.pjv;

/**
 * Smallest unit of gameboard. Is synchronized to prevent concurrent modification.
 * Can contain an entity and an explosion at the same time.
 */
public class BoardTile {
    private GameEntity contains;
    private ExplosionTile explosion;

    // without a setter - will be handled by contains setter
    private boolean canBeMovedOn;
    private boolean isEmpty;
    private boolean isExploding;

    /**
     * BoardTile constructor.
     * <p>
     * Is initiated empty as its content is set during gameBoard setting.
     *
     * @see GameBoard
     */
    public BoardTile() {
        contains = null;
        canBeMovedOn = true;
        isEmpty = true;
        isExploding = false;
    }

    /**
     * Gets whether tile can be moved on. canBeMovedOn property gets set by setContains method.
     */
    public synchronized boolean getCanBeMovedOn() {
        return canBeMovedOn;
    }

    /**
     * Gets content of tile. Content and explosion can exist simultaneously.
     */
    public synchronized GameEntity getContains() {
        return contains;
    }

    /**
     * Sets tile content. Sets canBeMoved on property based on content.
     *
     * @param contains content to be set, its transparency dictates whether tile can be moved on.
     */
    public synchronized void setContains(GameEntity contains) {
        if (contains != null) {
            this.canBeMovedOn = contains.isTransparent;
            this.contains = contains;
            this.isEmpty = false;
        }
    }

    /**
     * Gets whether the tile is empty. A tile that can be moved on is not necessarily empty.
     */
    public synchronized boolean isEmpty() {
        return isEmpty;
    }

    /**
     * Removes tile content and resets all properties tied to it.
     */
    public synchronized void removeContent() {
        this.canBeMovedOn = true;
        this.contains = null;
        this.isEmpty = true;
    }

    /**
     * Gets ExplosionTile. Explosion tile does not count as content. getContains will return different value.
     */
    public synchronized ExplosionTile getExplosion() {
        return explosion;
    }

    /**
     * Sets explosionTile. Changes isExploding to true.
     */
    public synchronized void setExplosion(ExplosionTile explosion) {
        this.explosion = explosion;
        this.isExploding = true;
    }

    /**
     * Removes explosion. Sets isExploding to false.
     */
    public synchronized void removeExplosion() {
        this.explosion = null;
        this.isExploding = false;
    }

    /**
     * Gets whether tile is exploding. Exploding tile still may have other GameEntity as content.
     */
    public synchronized boolean isExploding() {
        return isExploding;
    }
}
