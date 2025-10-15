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
 * QuadrantNavigation: 选择方向与距离，进行跨象限移动。
 * Public API strictly per spec:
 *  - public QuadrantNavigation(GameModel game, GameController controller)
 *  - public DirectionButton buildButton(int direction, ActionListener listener)
 *  - public Slider getSlider()
 */
public class QuadrantNavigation extends View {

    // private final GameModel game;
    // private final GameController controller;

    // For testability: the Slider used to represent "distance" in this view
    private final Slider slider;

    /**
     * Constructs a new instance of QuadrantNavigation.
     * @param game        game state we use to construct this view
     * @param controller  controller state we use for navigation/actions
     */
    public QuadrantNavigation(GameModel game, GameController controller) {
        super("Quadrant Navigation");
        Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(controller, "controller must not be null");

        // 标题
        addLabel(new JLabel("Navigate Between Quadrants"));


        this.slider = new Slider(1, 1);

        final double defaultDistance = 1.0;

        // 方向按钮：3×3 格布局（中心占位）
        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 6));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        // 构造监听器工厂：读取当前距离，移动、推进一回合并返回默认视图
        ActionListener move = e -> {
            if (e.getSource() instanceof DirectionButton) {
                DirectionButton btn = (DirectionButton) e.getSource();
                int direction = btn.getDirection();
                double dist = defaultDistance;
                game.moveBetweenQuadrants(direction, dist);
                game.turn();
                controller.setDefaultView(game);
            }
        };

        // 方向映射与填充：1→, 2↗, 3↑, 4↖, 5←, 6↙, 7↓, 8↘
        grid.add(buildButton(4, move)); // ↖
        grid.add(buildButton(3, move)); // ↑
        grid.add(buildButton(2, move)); // ↗

        grid.add(buildButton(5, move)); // ←
        grid.add(centerBadge());        // 中心占位“YOU”
        grid.add(buildButton(1, move)); // →

        grid.add(buildButton(6, move)); // ↙
        grid.add(buildButton(7, move)); // ↓
        grid.add(buildButton(8, move)); // ↘

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(grid);

        add(content, BorderLayout.CENTER);

        revalidate();
        repaint();

    }

    /**
     * Builds a DirectionButton for the given direction (标注其会消耗一回合) 并绑定监听器；
     * 同时跟踪按钮与监听器以便父类 View.cleanup() 清理。
     *
     * @param direction direction we want the new direction button to represent (1..8)
     * @param listener  listener that triggers a turn, movement, and screen transition
     * @return the newly built and tracked DirectionButton.
     */
    public DirectionButton buildButton(final int direction, final ActionListener listener) {
        DirectionButton btn = new DirectionButton(direction, /*consumesTurn=*/true);
        if (listener != null) {
            btn.addActionListener(listener);
            trackListener(listener);
        }
        // track the button so cleanup() can detach listeners later
        trackButton(btn);
        return btn;
    }

    /**
     * For testability reasons, should return the Slider used for the in quadrant
     * navigation view's distance.
     * @return the Slider used for the in quadrant navigation views distance.
     */
    public Slider getSlider() {
        return this.slider;
    }

    // --- private helpers ---

    private JComponent centerBadge() {
        JLabel you = new JLabel("YOU", SwingConstants.CENTER);
        you.setOpaque(false);
        you.setForeground(Color.WHITE);
        return you;
    }
}
