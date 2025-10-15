package sttrswing.view.panels;

import sttrswing.view.View;
import sttrswing.model.interfaces.GameModel;
import sttrswing.model.interfaces.HasPosition;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Objects;

/**
 * Shows current Enterprise status in a compact JTable.
 * Public API strictly follows the spec:
 *  - public EnterpriseStatus(GameModel game)
 *  - public JTable getTable()
 */
public class EnterpriseStatus extends View {

    private final GameModel game;
    private final JTable table;

    /**
     * Constructs a new EnterpriseStatus view.
     * @param game game state we use to construct this view for both information and method calls
     */
    public EnterpriseStatus(final GameModel game) {
        super("Enterprise Status");
        this.game = Objects.requireNonNull(game, "game must not be null");

        // Title
        addLabel(new JLabel("Enterprise Status"));

        // Build non-editable table model (Property / Value)
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Property", "Value"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        // Populate from GameModel (only using its public interface)
        HasPosition pos = game.playerPosition();
        String position = (pos == null) ? "(?, ?)" : "(" + pos.getX() + ", " + pos.getY() + ")";
        model.addRow(new Object[]{"Position", position});
        model.addRow(new Object[]{"Energy", game.playerEnergy()});
        model.addRow(new Object[]{"Shields", game.playerShields()});
        model.addRow(new Object[]{"Spare Energy", game.spareEnergy()});
        model.addRow(new Object[]{"Spare Torpedoes", game.spareTorpedoes()});
        model.addRow(new Object[]{"Klingons (Total)", game.totalKlingonCount()});
        model.addRow(new Object[]{"Starbases (Total)", game.totalStarbaseCount()});

        // Table styling for dark background
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(90, 90, 90));
        table.setSelectionBackground(new Color(30, 30, 30));
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JScrollPane scroller = new JScrollPane(table);
        scroller.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);

        // Since View uses BoxLayout.Y_AXIS, this adds the table under the title
        add(scroller);

        revalidate();
        repaint();
    }

    /**
     * Public for Testability reasons, lets us access the JTable, so we ensure it has instantiated as expected.
     * @return the JTable
     */
    public JTable getTable() {
        return table;
    }
}
