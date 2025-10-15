package sttrswing.view.panels;

import sttrswing.model.interfaces.GameModel;
import sttrswing.model.interfaces.HasPosition;
import sttrswing.view.Pallete;
import sttrswing.view.View;

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

        setLayout(new BorderLayout(8, 8));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Stat", "Value"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        java.util.function.Function<HasPosition, String> formatPosition = position -> {
            if (position == null) {
                return "?, ?";
            }
            int x = position.getX() + 1;
            int y = position.getY() + 1;
            return x + ", " + y;
        };

        model.addRow(new Object[]{"Quadrant (X,Y)", formatPosition.apply(game.galaxyPosition())});
        model.addRow(new Object[]{"Sector (X,Y)", formatPosition.apply(game.playerPosition())});
        model.addRow(new Object[]{"Torpedoes", game.spareTorpedoes()});
        model.addRow(new Object[]{"Energy", game.playerEnergy()});
        model.addRow(new Object[]{"Shields", game.playerShields()});
        model.addRow(new Object[]{"Klingons left in Galaxy", game.totalKlingonCount()});
        model.addRow(new Object[]{"Starbases left in Galaxy", game.totalStarbaseCount()});

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.setBackground(Pallete.BLACK.color());
        table.setForeground(Pallete.WHITE.color());
        table.setGridColor(Pallete.GREYDARK.color());
        table.setSelectionBackground(Pallete.GREYDARK.color());
        table.setSelectionForeground(Pallete.WHITE.color());
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFont(table.getFont().deriveFont(Font.PLAIN, 14f));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(Pallete.BLACK.color());
        table.getTableHeader().setForeground(Pallete.WHITE.color());
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 14f));

        JScrollPane scroller = new JScrollPane(table);
        scroller.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        scroller.getViewport().setBackground(Pallete.BLACK.color());
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(true);
        add(scroller, BorderLayout.CENTER);

        revalidate();
        repaint();

        if (getComponentCount() == 0) {
            add(new javax.swing.JLabel("Enterprise Status"), java.awt.BorderLayout.CENTER);
        }

    }

    /**
     * Public for Testability reasons, lets us access the JTable, so we ensure it has instantiated as expected.
     * @return the JTable
     */
    public JTable getTable() {
        return table;
    }
}
