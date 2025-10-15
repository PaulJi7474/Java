package sttrswing.view.panels;

import sttrswing.model.interfaces.GameModel;
import sttrswing.model.interfaces.HasFaction;
import sttrswing.model.interfaces.HasSymbol;
import sttrswing.model.enums.Faction;
import sttrswing.view.View;
import sttrswing.view.guicomponents.MapSquare;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

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

        // 标题
        addLabel(new JLabel("Short Range Scan"));

        // 留一个容器摆放 MapSquare（此处只是占位，真实格子由控制器/其它面板组装）
        JPanel container = new JPanel(new GridLayout(1, 1, 4, 4));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        container.add(buildEmptyMapSquare());
        add(container);

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
