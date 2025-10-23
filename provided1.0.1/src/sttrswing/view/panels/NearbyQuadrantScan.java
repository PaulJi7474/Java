package sttrswing.view.panels;

import sttrswing.model.enums.Faction;
import sttrswing.model.interfaces.GameModel;
import sttrswing.model.interfaces.HasFaction;
import sttrswing.model.interfaces.HasSymbol;
import sttrswing.view.View;
import sttrswing.view.guicomponents.MapSquare;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * NearbyQuadrantScan: shows scan results (e.g. "201") of the 
 * 8 quadrants surrounding the player's current quadrant.
 * API strictly follows the spec:
 *  - public NearbyQuadrantScan(GameModel game)
 *  - public MapSquare buildMapSquare(String label)
 */
public class NearbyQuadrantScan extends View {

    private final GameModel game; 
    // game state we use to construct this view for both information and method calls

    /**
     * Construct a new NearbyQuadrantScan instance.
     * @param game game state we use to construct this view for both information and method calls
     */
    public NearbyQuadrantScan(final GameModel game) {
        super("Nearby Quadrants");
        this.game = Objects.requireNonNull(game, "game must not be null");

        setLayout(new BorderLayout(8, 8));
        addLabel(new JLabel("Nearby Quadrants"));

        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 6));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        HashMap<String, String> m = game.getSurroundingQuadrants();
        String playerSymbol = "YOU";
        for (Object entry : game.getSymbolsForQuadrant()) {
            HasFaction faction = (HasFaction) entry;
            if (faction.faction() == Faction.FEDERATION) {
                playerSymbol = ((HasSymbol) entry).symbol();
                break;
            }
        }

        grid.add(buildMapSquare(m.get("topLeft")));
        grid.add(buildMapSquare(m.get("top")));
        grid.add(buildMapSquare(m.get("topRight")));

        grid.add(buildMapSquare(m.get("left")));
        grid.add(buildMapSquare(playerSymbol));
        grid.add(buildMapSquare(m.get("right")));

        grid.add(buildMapSquare(m.get("bottomLeft")));
        grid.add(buildMapSquare(m.get("bottom")));
        grid.add(buildMapSquare(m.get("bottomRight")));

        add(grid, BorderLayout.CENTER);

        revalidate();
        repaint();
        if (getComponentCount() == 0) {
            add(new javax.swing.JLabel("Scanning…"), java.awt.BorderLayout.CENTER);
        }

    }

    /**
     * Constructs a new MapSquare with the given label or a blank string if the label is null.
     * @param label label we wish to display on this MapSquare.
     * @return a new MapSquare with the given label or a blank string if the label is null.
     */
    public MapSquare buildMapSquare(final String label) {
        return new MapSquare(label == null ? "" : label);
    }
}
