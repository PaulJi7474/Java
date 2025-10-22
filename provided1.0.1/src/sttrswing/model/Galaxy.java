package sttrswing.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the {@link Quadrant}s held within the {@link Galaxy}.
 */
public class Galaxy {

    /**
     * All quadrants in this galaxy, logically arranged as an 8×8 grid (row-major: y
     * then x).
     * Never {@code null}. The reference is initialised in the constructor and not
     * reassigned.
     */
    private final List<Quadrant> quadrants;

    /**
     * Constructs a new Galaxy with 64 {@link Quadrant}s arranged as an 8×8 grid.
     */
    public Galaxy() {
        this.quadrants = this.generateQuadrants();
    }

    /**
     * Constructs a new Galaxy using the provided quadrants rather than generating
     * its own.
     * The list reference is stored as-is (no defensive copy).
     *
     * @param quadrants quadrants this {@link Galaxy} should use; expected to
     *                  represent an 8×8 grid
     */
    public Galaxy(ArrayList<Quadrant> quadrants) {
        this.quadrants = quadrants;
    }

    /**
     * Returns a {@link List} of {@link Quadrant}s that are adjacent to (and
     * including)
     * the quadrant at the given coordinates. Up to nine quadrants may be returned
     * (a 3×3 cluster centred at {@code (x, y)}), clipped to the galaxy bounds.
     *
     * @param x x-coordinate of the centre quadrant
     * @param y y-coordinate of the centre quadrant
     * @return list of neighbouring quadrants including the centre; empty if the
     *         centre does not exist
     */
    public List<Quadrant> getQuadrantClusterAt(final int x, final int y) {
        ArrayList<Quadrant> cluster = new ArrayList<>();
        Quadrant center = this.quadrantAt(x, y);
        if (center == null) {
            return cluster;
        }
        cluster.add(center);

        final int left = x - 1;
        final int top = y - 1;
        final int bottom = y + 1;
        final int right = x + 1;

        Quadrant q = this.quadrantAt(left, top);
        if (q != null)
            cluster.add(q);

        q = this.quadrantAt(left, y);
        if (q != null)
            cluster.add(q);

        q = this.quadrantAt(left, bottom);
        if (q != null)
            cluster.add(q);

        q = this.quadrantAt(x, top);
        if (q != null)
            cluster.add(q);

        q = this.quadrantAt(x, bottom);
        if (q != null)
            cluster.add(q);

        q = this.quadrantAt(right, top);
        if (q != null)
            cluster.add(q);

        q = this.quadrantAt(right, y);
        if (q != null)
            cluster.add(q);

        q = this.quadrantAt(right, bottom);
        if (q != null)
            cluster.add(q);

        return cluster;
    }

    /**
     * Generates 64 {@link Quadrant} instances with unique coordinates laid out as
     * an 8×8 grid.
     *
     * @return a list of generated quadrants in row-major order (y then x)
     */
    public List<Quadrant> generateQuadrants() {
        final int maxRows = 8;
        final int maxCols = 8;
        ArrayList<Quadrant> list = new ArrayList<>();
        for (int row = 0; row < maxRows; row += 1) {
            for (int col = 0; col < maxCols; col += 1) {
                list.add(new Quadrant(row, col));
            }
        }
        return list;
    }

    /**
     * Returns the total number of {@link Klingon}s across all quadrants in this
     * {@link Galaxy}.
     *
     * @return total number of Klingons
     */
    public int klingonCount() {
        int klingons = 0;
        for (Quadrant q : this.quadrants) {
            klingons += q.klingonCount();
        }
        return klingons;
    }

    /**
     * Returns the total number of starbases across all quadrants in this
     * {@link Galaxy}.
     *
     * @return total number of starbases
     */
    public int starbaseCount() {
        int starbases = 0;
        for (Quadrant q : this.quadrants) {
            starbases += q.starbaseCount();
        }
        return starbases;
    }

    /**
     * Returns the {@link Quadrant} located at the specified coordinates, if
     * present.
     *
     * @param x horizontal coordinate of the quadrant to find
     * @param y vertical coordinate of the quadrant to find
     * @return the quadrant at {@code (x, y)}; {@code null} if none exists
     */
    public Quadrant quadrantAt(final int x, final int y) {
        for (Quadrant quadrant : this.quadrants) {
            if (quadrant.getX() == x && quadrant.getY() == y) {
                return quadrant;
            }
        }
        return null;
    }

    /**
     * Invokes {@code outOfFocusTick(game)} on all {@link Quadrant}s except those
     * provided in
     * {@code quadrantsToSkip}.
     *
     * @param quadrantsToSkip quadrants that should not be ticked
     * @param game            the game instance passed to each quadrant's tick
     */
    public void outOfFocusTick(ArrayList<Quadrant> quadrantsToSkip, Game game) {
        for (Quadrant quadrant : this.quadrants) {
            if (!quadrantsToSkip.contains(quadrant)) {
                quadrant.outOfFocusTick(game);
            }
        }
    }

    /**
     * Exports this galaxy's quadrants as a saveable string, one line per quadrant,
     * e.g.:
     * 
     * <pre>
     * [q] x:2 y:5 s:K |
     * </pre>
     *
     * @return a string representation of this galaxy suitable for saving
     */
    public String export() {
        StringBuilder exportString = new StringBuilder();
        for (Quadrant quadrant : this.quadrants) {
            StringBuilder sb = new StringBuilder();
            sb.append("[q]");
            sb.append(" x:").append(quadrant.getX());
            sb.append(" y:").append(quadrant.getY());
            sb.append(" s:").append(quadrant.symbol());
            sb.append(" |");
            sb.append("\n");
            exportString.append(sb);
        }
        return exportString.toString();
    }
}
