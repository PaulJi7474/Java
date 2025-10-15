package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;
import sttrswing.view.guicomponents.DirectionButton;
import sttrswing.view.guicomponents.Slider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Warp navigation within the current quadrant. Public API strictly per spec:
 *  - public WarpNavigation(GameModel game, GameController controller)
 *  - public Slider getSlider()
 *  - public DirectionButton buildButton(int direction, ActionListener listener)
 */
public class WarpNavigation extends View {

    // private final GameModel game;
    // private final GameController controller;
    private final Slider slider; // for testability: warp factor / distance slider

    /**
     * Construct a new WarpNavigation instance.
     * @param game        game state
     * @param controller  controller state
     */
    public WarpNavigation(GameModel game, GameController controller) {
        super("Warp Navigation");
        Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(controller, "controller must not be null");

        addLabel(new JLabel("Warp Navigation"));

        this.slider = new Slider();

        // 真实可视滑块（内部使用）：以 spareEnergy 作为最大可移动距离的简单上限
        final int max = Math.max(1, game.spareEnergy());
        final JSlider distance = new JSlider(1, max, Math.min(3, max));
        distance.setOpaque(false);
        distance.setPaintTicks(true);
        distance.setMajorTickSpacing(Math.max(1, max / 4 == 0 ? 1 : max / 4));

        JPanel distPanel = new JPanel(new BorderLayout(8, 8));
        distPanel.setOpaque(false);
        distPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        distPanel.add(new JLabel("Warp distance:"), BorderLayout.WEST);
        distPanel.add(distance, BorderLayout.CENTER);
        add(distPanel);

        // 方向按钮：3×3
        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 6));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        ActionListener move = e -> {
            if (e.getSource() instanceof DirectionButton) {
                int dir = ((DirectionButton) e.getSource()).getDirection();
                double dist = Math.max(1, distance.getValue());
                game.moveWithinQuadrant(dir, dist);
                game.turn();
                controller.setDefaultView(game);
            }
        };

        grid.add(buildButton(4, move)); // ↖
        grid.add(buildButton(3, move)); // ↑
        grid.add(buildButton(2, move)); // ↗

        grid.add(buildButton(5, move)); // ←
        grid.add(centerBadge());
        grid.add(buildButton(1, move)); // →

        grid.add(buildButton(6, move)); // ↙
        grid.add(buildButton(7, move)); // ↓
        grid.add(buildButton(8, move)); // ↘

        add(grid);

        revalidate();
        repaint();
        
        if (getComponentCount() == 0) {
            add(new javax.swing.JButton("Warp…"), java.awt.BorderLayout.CENTER);
        }

    }

    /**
     * Exists for testability reasons to allow us to confirm the Slider matches some expected constraints.
     * @return the Slider used for warp factor/distance control.
     */
    public Slider getSlider() {
        return this.slider;
    }

    /**
     * Builds a DirectionButton for the given direction, indicating it consumes a turn and
     * attaching the given ActionListener; also tracks both for later cleanup by View.cleanup().
     *
     * @param direction direction value to represent (1..8)
     * @param listener  listener that triggers a turn, movement, etc.
     * @return the newly built and tracked DirectionButton.
     */
    public DirectionButton buildButton(final int direction, final ActionListener listener) {
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
