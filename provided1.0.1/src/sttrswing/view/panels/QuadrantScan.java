package sttrswing.view.panels;

import sttrswing.model.interfaces.GameModel;
import sttrswing.model.interfaces.HasFaction;
import sttrswing.model.interfaces.HasPosition;
import sttrswing.model.interfaces.HasSymbol;
import sttrswing.model.enums.Faction;
import sttrswing.view.Pallete;
import sttrswing.view.View;
import sttrswing.view.guicomponents.MapSquare;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Scans and renders the current quadrant. Only exposes the constructor and the two
 * public build* methods required by the spec.
 */
public class QuadrantScan extends View {

    private final GameModel game;

    /**
     * Construct a new QuadrantScan.
     * @param game game state we need access to for the symbol for the current Quadrant.
     */
    public QuadrantScan(final GameModel game) {
        super("Quadrant Scan");
        this.game = Objects.requireNonNull(game, "game must not be null");

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel grid = new JPanel(new GridLayout(8, 8, 2, 2));
        grid.setOpaque(false);

        MapSquare[][] squares = new MapSquare[8][8];
        ArrayList<?> entries = game.getSymbolsForQuadrant();
        for (Object entry : entries) {
            HasPosition pos = (HasPosition) entry;
            int x = Math.max(0, Math.min(7, pos.getX()));
            int y = Math.max(0, Math.min(7, pos.getY()));
            squares[y][x] = buildMapSquare((HasSymbol & HasFaction) entry);
        }

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                MapSquare sq = squares[y][x];
                if (sq == null) {
                    sq = buildEmptyMapSquare();
                    sq.setText(" ? ");
                }
                grid.add(sq);
            }
        }

        add(grid, BorderLayout.CENTER);

        JTextArea report = new JTextArea(game.lastActionReport());
        report.setEditable(false);
        report.setLineWrap(true);
        report.setWrapStyleWord(true);
        report.setForeground(Pallete.WHITE.color());
        report.setOpaque(false);
        report.setMargin(new Insets(8, 0, 0, 0));
        add(report, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Public for Testability reasons, constructs a MapSquare with no symbol and default coloring
     * and adds it to this view.
     * @return the newly constructed empty MapSquare.
     */
    public MapSquare buildEmptyMapSquare() {
        MapSquare sq = new MapSquare("");
        // MapSquare 默认已设置黑底、绿字与边框，无需额外处理
        return sq;
    }

    /**
     * Public for Testability reasons, constructs a MapSquare displaying data.symbol() and
     * colour-coding it based on data.faction().
     *
     * @param data Information we need for the visuals of this map square
     * @param <T>  Requires access to .symbol() and .faction()
     * @return coloured MapSquare reflecting the data entry
     */
    public <T extends HasSymbol & HasFaction> MapSquare buildMapSquare(final T data) {
        Objects.requireNonNull(data, "data must not be null");
        MapSquare sq = new MapSquare(data.symbol() == null ? "" : data.symbol());

        // 根据阵营上色：联邦=青色，克林贡=红色，中立=默认绿
        Faction f = data.faction();
        if (f == Faction.FEDERATION) {
            sq.setForeground(Color.CYAN);
        } else if (f == Faction.KLINGON) {
            sq.setForeground(Color.RED);
        } // NEUTRAL 使用 MapSquare 默认的绿色

        return sq;
    }
}
