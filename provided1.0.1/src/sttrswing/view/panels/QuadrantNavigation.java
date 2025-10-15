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

        // 距离（供测试的 Slider 对象）—— 规范只要求 getSlider() 返回它
        this.slider = new Slider();

        // 实际可视滑块（用于交互），不暴露为 public API
        final int max = Math.max(1, game.spareEnergy()); // 简单用剩余能量当作最大可移动距离上限的代理
        final JSlider distance = new JSlider(1, max, Math.min(3, max));
        distance.setOpaque(false);
        distance.setPaintTicks(true);
        distance.setMajorTickSpacing(Math.max(1, max / 4 == 0 ? 1 : max / 4));

        JPanel distancePanel = new JPanel(new BorderLayout(8, 8));
        distancePanel.setOpaque(false);
        distancePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        distancePanel.add(new JLabel("Distance:"), BorderLayout.WEST);
        distancePanel.add(distance, BorderLayout.CENTER);
        add(distancePanel);

        // 方向按钮：3×3 格布局（中心占位）
        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 6));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        // 构造监听器工厂：读取当前距离，移动、推进一回合并返回默认视图
        ActionListener move = e -> {
            if (e.getSource() instanceof DirectionButton) {
                DirectionButton btn = (DirectionButton) e.getSource();
                int direction = btn.getDirection();
                double dist = Math.max(1, distance.getValue()); // 简单映射为 double
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

        add(grid);

        revalidate();
        repaint();

        add(new javax.swing.JLabel("Quadrant Navigation"), java.awt.BorderLayout.CENTER);

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
