package sttrswing.model;

import sttrswing.model.enums.Faction;
import sttrswing.model.interfaces.HasFaction;
import sttrswing.model.interfaces.Healable;
import java.util.ArrayList;

/**
 * Representation of the {@link Enterprise}, handles internal state for the
 * {@link Enterprise} in
 * our game.
 */
public class Enterprise extends Entity implements Healable, HasFaction {
    private final int startingTorpedoes = 10; // starting number of torpedoes
    private final int startingShields = 500; // starting shield energy
    private final int startingEnergy = 2500; // starting energy reserves
    private final Stat torpedoes = new Stat(startingTorpedoes, startingTorpedoes);
    // starting torpedoes is also the max
    private final Stat shields = new Stat(startingShields, 3000);
    // max shields is 3000
    private final Stat energy = new Stat(startingEnergy, 3000);
    // max energy is 3000
    private final Faction faction = Faction.FEDERATION; // faction this belongs to
    private boolean isAlive = true; // whether the enterprise is alive

    /**
     * Constructs a {@link Enterprise} instance with:
     * <ul>
     * <li>shields at 500,</li>
     * <li>energy at 2500,</li>
     * <li>10 torpedoes</li>
     * <li>X:4, Y:4 for starting position</li>
     * </ul>
     * .
     */
    public Enterprise() {
        super(4, 4);
        this.setSymbol("-E-");
    }

    public Enterprise(final int x, final int y, final int energy,
            final int shields, final int torpedoes) {
        super(x, y);
        this.energy.set(energy);
        this.shields.set(shields);
        this.torpedoes.set(torpedoes);
    }

    /**
     * Constructs a {@link Enterprise} instance with:
     * <ul>
     * <li>shields at 500,</li>
     * <li>energy at 2500,</li>
     * <li>10 torpedoes</li>
     * <li>X:x, Y:y for starting position</li>
     * </ul>
     * .
     *
     * @param x - horizontal starting coordinate for the {@link Enterprise}
     * @param y - vertical starting coordinate for the {@link Enterprise}
     */
    public Enterprise(final int x, final int y) {
        super(x, y);
        this.setSymbol("-E-");
    }

    /**
     * Returns which {@link Faction} this belongs to.
     *
     * @return which {@link Faction} this belongs to.
     */
    public Faction faction() {
        return faction;
    }

    /**
     * Returns an int representation of the number of torpedoes the
     * {@link Enterprise} has to use.
     *
     * @return an int representation of the number of torpedoes the
     *         {@link Enterprise} has to use.
     */
    public int torpedoAmmo() {
        return this.torpedoes.get();
    }

    /**
     * Creates a Torpedo using {@link Entity} and reduces the {@link Enterprise}
     * internal ammo
     * tracking by 1, returns null if no ammo left.
     *
     * @return a Torpedo using {@link Entity} returns null if no ammo left.
     */
    public Entity fireTorpedo() {
        if (!this.hasTorpedoAmmo()) {
            return null;
        }
        this.torpedoes.adjust(-1);
        return new Entity(this.getX(), this.getY());
    }

    /**
     * Returns if the {@link Enterprise} still has Torpedo Ammo to spend.
     *
     * @return if the {@link Enterprise} still has Torpedo Ammo to spend.
     */
    public boolean hasTorpedoAmmo() {
        return this.torpedoes.get() > 0;
    }

    /**
     * Returns an int representation of the total amount of energy in the
     * {@link Enterprise}'s shields
     * {@link Stat}.
     *
     * @return an int representation of the total amount of energy in the
     *         {@link Enterprise}'s shields
     *         {@link Stat}.
     */
    public int shields() {
        return this.shields.get();
    }

    /**
     * Returns an int representation of the total amount of energy in the
     * {@link Enterprise}'s energy
     * reserves {@link Stat}.
     *
     * @return an int representation of the total amount of energy in the
     *         {@link Enterprise}'s energy
     *         reserves {@link Stat}.
     */
    public int energy() {
        return energy.get();
    }

    /**
     * Drains the given amount of energy from the {@link Enterprise} down to a
     * minimum of 0.
     *
     * @param energy - amount of energy to subtract
     * @return - amount of energy that was available to drain.
     */
    public int drainEnergy(final int energy) {
        int availableEnergy = this.energy.get();
        int drained = Math.min(energy, availableEnergy);
        this.energy.adjust(-energy);
        return drained;
    }

    /**
     * Return whether the enterprise is adjacent to a starbase.
     *
     * @param starbases the list of starbases to check against.
     * @return true if the enterprise is next to one of the starbases, false
     *         otherwise.
     */
    public boolean docked(ArrayList<Starbase> starbases) {
        for (Starbase starbase : starbases) {
            int xdelta = Math.abs(starbase.getX() - this.getX());
            int ydelta = Math.abs(starbase.getY() - this.getY());
            if (xdelta < 2 && ydelta < 2) { // adjacent
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the given amount of energy to the {@link Enterprise} up to it's maximum
     * amount (3000).
     *
     * @param energy - amount of energy to add
     */
    public void gainEnergy(final int energy) {
        this.energy.adjust(energy);
        if(energy >0 ) {
            this.torpedoes.adjust(1);
        }
    }

    /**
     * Provides a 3 char long {@link String} representation of the
     * {@link Enterprise} with implicit
     * indications for the state of:
     * <ul>
     * <li>Energy reserves,</li>
     * <li>If it has Torpedoes,</li>
     * <li>Shields energy levels</li>
     * </ul>
     * .
     *
     * <h4>Energy Reserves</h4>
     * <ul>
     * <li>E : 1000 or more Energy Reserves (note the E can be changed out for Ë
     * based on torpedo
     * count)</li>
     * <li>e : 999 or less energy reserves (note the e can be changed out for ë
     * based on torpedo
     * count)</li>
     * </ul>
     * 
     * <h4>Torpedoes:</h4>
     *
     * <p>
     * The outer characters of the symbol remain constant so that changes to shield
     * allocation do
     * not alter the displayed icon.
     * </p>
     *
     * @return a 3 char long {@link String} representation of the {@link Enterprise}
     *         with implicit
     *         indications for the state of torpedo ammo and energy.
     */
    @Override
    public String symbol() {
        String left = "{";
        String center = "E";
        String right = "}";

        // Central Symbol management
        if (this.energy.get() < 1000) {
            center = "e";
        }
        if (this.torpedoes.get() > 0 && center.equals("e")) {
            center = "ë";
        } else if (this.torpedoes.get() > 0 && center.equals("E")) {
            center = "Ë";
        }

        return left + center + right;
    }

    /**
     * Adjust shields and energy by the given target amount of energy, if there is
     * insufficient energy
     * to transfer that, transfer what is available.
     *
     * @param energy - the amount of energy we wish to transfer to shields
     * @return how much energy was actually transferred to shields. (this may differ
     *         from the amount
     *         ordered based on energy available etc.)
     */
    public int transferEnergyToShields(final int energy) {
        int energyToTransfer = this.drainEnergy(energy);
        this.shields.adjust(energyToTransfer);
        return energyToTransfer;
    }

    /**
     * Return if the {@link Enterprise} is currently alive.
     *
     * @return if the {@link Enterprise} is currently alive.
     */
    public boolean isAlive() {
        return this.isAlive;
    }

    /**
     * Hit the {@link Enterprise} for the given amount of damage, reducing its
     * shields by the given
     * amount. After damage is inflicted, checks if the enterprise still has shields
     * above 0, if a hit
     * took it to 0 shields, or it was already at 0 shields when hit, the Enterprise
     * is destroyed.
     *
     * @param damage - how much damage we wish to do to the {@link Enterprise}
     */
    @Override
    public void hit(final int damage) {
        this.shields.adjust(-damage);
        // if we take damage while our shields are down, or enough damage in a hit to
        // reduce we lose
        if (this.shields.get() <= 0) {
            this.isAlive = false;
        }
    }

    /**
     * Heal the {@link Enterprise} for the given amount of healing.
     *
     * @param energy - how much do we wish to heal the {@link Enterprise} by.
     */
    @Override
    public void heal(final int energy) {
        this.energy.adjust(energy);
    }

    /**
     * Export a stringified representation of the enterprises state. Meaning its: x,
     * y, energy count,
     * torpedo count and shield count.
     *
     * @return a stringified representation of the enterprises state. Meaning its:
     *         x, y, energy count,
     *         torpedo count and shield count.
     */
    public String export() {
        StringBuilder sb = new StringBuilder();
        sb.append("[e]");
        sb.append(" x:" + this.getX());
        sb.append(" y:" + this.getY());
        sb.append(" e:" + this.energy.get());
        sb.append(" s:" + this.shields.get());
        sb.append(" t:" + this.torpedoes.get());
        sb.append(" |");
        sb.append("\n");
        return sb.toString();
    }
}