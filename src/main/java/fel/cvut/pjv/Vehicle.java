package fel.cvut.pjv;

import java.util.logging.Logger;

/**
 * Parent class of EnemyTank and PlayerTank. Keeps state of vehicles and their position on board.
 */
public class Vehicle extends GameEntity {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final int explosionRadius;
    private int score = 0;
    private int health;
    private int armor;
    private int range;
    private int damage;

    public Vehicle(int rowPos, int colPos, int[] facingDirection, boolean isTransparent, int health, int armor, int range, int damage, int explosionRadius, String spriteType) {
        super(rowPos, colPos, facingDirection, isTransparent, spriteType);
        this.health = health;
        this.armor = armor;
        this.range = range;
        this.damage = damage;
        this.explosionRadius = explosionRadius;
    }

    // either direct hit or hit by explosion radius (explosion tile)

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Method handles hits. Damage is calculated by (explosion_damage - armor).
     * If resulting damage is less than 3 it is set to 3.
     * A hit increments explosion authors score by 100.
     * A kill increments explosion authors score by 500.
     *
     * @param e ExplosionTile that hit the vehicle - not null.
     * @return false if vehicle survived hit, true if it got destroyed.
     */
    @Override
    public boolean getHit(ExplosionTile e) {
        int damage = (e.getDamage() - armor);
        if (damage < 3) {
            damage = 3;
        }
        Vehicle hitAuthor = e.getAuthor();
        health = health - damage;
        LOGGER.finer("Vehicle " + this + " got hit by " + hitAuthor + " for " + damage);
        if (health <= 0) {
            if (hitAuthor != null && !hitAuthor.equals(this)) { // dont increment score if vehicle hit itself
                hitAuthor.addScore(Constants.killScore);
            }
            return true;
        } else {
            if (hitAuthor != null && !hitAuthor.equals(this)) { // dont increment score if vehicle hit itself
                hitAuthor.addScore(Constants.hitScore);
            }
            return false;
        }
    }

    @Override
    public void getDrivenOn(Vehicle v) {
    }

    public void addScore(int increment) {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getExplosionRadius() {
        return explosionRadius;
    }
}
