package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;
import sttrswing.view.guicomponents.DirectionButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Torpedo targeting panel. Public API strictly per spec:
 *  - public Torpedo(GameModel game, GameController controller)
 *  - public DirectionButton buildButton(int direction, ActionListener listener)
 */
public class Torpedo extends View {

    private final GameModel game;
    private final GameController controller;

    /**
     * Construct a new Torpedo instance.
     * @param game        game state
     * @param controller  controller state
     */
    public Torpedo(final GameModel game, final GameController controller) {
        super("Torpedoes");
        this.game = Objects.requireNonNull(game, "game must not be null");
        this.controller = Objects.requireNonNull(controller, "controller must not be null");

        addLabel(new JLabel("Photon Torpedoes"));

        // 方向选择：3×3
        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 6));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ActionListener fire = e -> {
            if (e.getSource() instanceof DirectionButton) {
                int dir = ((DirectionButton) e.getSource()).getDirection();
                this.game.fireTorpedo(dir);
                this.game.turn();
                this.controller.setDefaultView(this.game);
            }
        };

        grid.add(buildButton(4, fire)); // ↖
        grid.add(buildButton(3, fire)); // ↑
        grid.add(buildButton(2, fire)); // ↗

        grid.add(buildButton(5, fire)); // ←
        grid.add(centerBadge());        // 中心占位
        grid.add(buildButton(1, fire)); // →

        grid.add(buildButton(6, fire)); // ↙
        grid.add(buildButton(7, fire)); // ↓
        grid.add(buildButton(8, fire)); // ↘

        add(grid);

        revalidate();
        repaint();
    }

    /**
     * Build a DirectionButton, tracking it and the given ActionListener listener.
     * @param direction direction this button is meant to represent.
     * @param listener  ActionListener we want to bind to the newly instantiated DirectionButton.
     * @return A tracked, configured DirectionButton.
     */
    public DirectionButton buildButton(final int direction, final ActionListener listener) {
        // 是否显示“消耗回合”图标规范未强制，这里设为 true 更贴近实际
        DirectionButton btn = new DirectionButton(direction, /*consumesTurn=*/true);
        if (listener != null) {
            btn.addActionListener(listener);
            trackListener(listener);
        }
        trackButton(btn);
        return btn;
    }

    // --- private ---
    private JComponent centerBadge() {
        JLabel you = new JLabel("YOU", SwingConstants.CENTER);
        you.setOpaque(false);
        you.setForeground(Color.WHITE);
        return you;
    }
}
