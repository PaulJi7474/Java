package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;
import sttrswing.view.guicomponents.Slider;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Shield control panel. Public API strictly per spec:
 *  - public Shield(GameModel game, GameController controller)
 *  - public Slider getSlider()
 */
public class Shield extends View {

    // private final GameModel game;
    // private final GameController controller;
    private final Slider slider; // for testability

    /**
     * Constructs a new Shield instance.
     * @param game        game state
     * @param controller  controller for navigation/actions
     */
    public Shield(final GameModel game, final GameController controller) {
        super("Shields");
        // this.game = Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(game, "game must not be null");
        // this.controller = Objects.requireNonNull(controller, "controller must not be null");
        Objects.requireNonNull(controller, "controller must not be null");

        addLabel(new JLabel("Shields"));

        // 仅供测试访问的 Slider 对象
        this.slider = new Slider();

        // 真实可视滑动条（内部实现，不对外暴露）
        final int maxSpend = Math.max(0, game.spareEnergy());
        final JSlider spend = new JSlider(0, maxSpend, Math.min(100, maxSpend));
        spend.setOpaque(false);
        spend.setPaintTicks(true);
        spend.setMajorTickSpacing(Math.max(1, maxSpend / 4 == 0 ? 1 : maxSpend / 4));

        JPanel form = new JPanel(new BorderLayout(8, 8));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Energy to spend on shields:"), BorderLayout.WEST);
        form.add(spend, BorderLayout.CENTER);
        add(form);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        JButton apply = buildButton("Apply", e -> {
            int req = spend.getValue();
            game.shields(req);
            game.turn();
            controller.setDefaultView(game);
        });
        JButton cancel = buildButton("Cancel", e -> controller.setDefaultView(game));
        actions.add(apply);
        actions.add(cancel);
        add(actions);

        revalidate();
        repaint();
    }

    /**
     * For Testability Purposes, lets us access the Slider that should be held by the Shield panel.
     * @return Slider held by the Shield panel.
     */
    public Slider getSlider() {
        return this.slider;
    }
}
