package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;
import sttrswing.view.guicomponents.Slider;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * PhaserAttack screen: choose energy for phaser attack.
 * Public API strictly per spec:
 *  - public PhaserAttack(GameModel game, GameController controller)
 *  - public Slider getSlider()
 */
public class PhaserAttack extends View {

    // private final GameModel game;
    // private final GameController controller;
    private final Slider slider;   // kept for testability via getSlider()

    /**
     * Constructs a new PhaserAttack instance.
     * @param game        game state we use to construct this view
     * @param controller  controller state for navigation / actions
     */
    public PhaserAttack(final GameModel game, final GameController controller) {
        super("Phaser Attack");
        // this.game = Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(game, "game must not be null");
        // this.controller = Objects.requireNonNull(controller, "controller must not be null");
        Objects.requireNonNull(controller, "controller must not be null");

        // title (reuse View's addLabel for consistent style)
        addLabel(new JLabel("Phaser Attack"));

                final int maxEnergy = Math.max(0, game.spareEnergy());
        this.slider = new Slider(maxEnergy, Math.min(200, maxEnergy));

        JPanel layout = new JPanel(new BorderLayout(12, 12));
        layout.setOpaque(false);
        layout.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel header = new JLabel("Energy to spend:", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        JLabel amount = new JLabel(String.valueOf(this.slider.getValue()), SwingConstants.CENTER);
        amount.setFont(amount.getFont().deriveFont(Font.BOLD, amount.getFont().getSize() + 2f));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(header);
        info.add(Box.createVerticalStrut(4));
        info.add(amount);

        layout.add(info, BorderLayout.NORTH);

        JPanel sliderWrapper = new JPanel(new BorderLayout());
        sliderWrapper.setOpaque(false);
        this.slider.setOpaque(false);
        sliderWrapper.add(this.slider, BorderLayout.CENTER);
        layout.add(sliderWrapper, BorderLayout.CENTER);

        JLabel availability = new JLabel("Available: " + maxEnergy + " energy", SwingConstants.CENTER);
        availability.setOpaque(false);
        layout.add(availability, BorderLayout.SOUTH);

        add(layout);

        // Action buttons
        JPanel actions = new JPanel();
        actions.setOpaque(false);

        JButton confirm = buildButton("Confirm", e -> {
            int spend = this.slider.getValue();
            if (spend > 0) {
                game.firePhasers(spend);   // GameModel public API
                game.turn();               // advance a turn
            }
            controller.setDefaultView(game); // return to default view
        });
        confirm.setEnabled(this.slider.getValue() > 0);

        this.slider.addChangeListener(e -> {
            int value = this.slider.getValue();
            amount.setText(String.valueOf(value));
            confirm.setEnabled(value > 0);
        });

        JButton cancel = buildButton("Cancel", e -> controller.setDefaultView(game));

        actions.add(confirm);
        actions.add(cancel);
        add(actions);

        revalidate();
        repaint();
    }

    /**
     * Present for Testability reasons, returns the Slider used in the phaser attack screen.
     * @return the Slider used in the phaser attack screen.
     */
    public Slider getSlider() {
        return this.slider;
    }
}
