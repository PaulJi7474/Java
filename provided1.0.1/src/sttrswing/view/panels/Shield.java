package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;
import sttrswing.view.guicomponents.Slider;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;
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

        final int spareEnergy = Math.max(0, game.spareEnergy());
        final int currentEnergy = Math.max(0, game.playerEnergy());
        final int maxTransfer = Math.min(spareEnergy, (int) Math.floor(currentEnergy * 0.9));
        final boolean canTransferEnergy = maxTransfer >= 1;

        this.slider = new Slider(maxTransfer);
        this.slider.setOpaque(false);
        this.slider.setSnapToTicks(true);

        if (canTransferEnergy) {
            this.slider.setMinimum(1);
            this.slider.setMaximum(maxTransfer);
            this.slider.setMinorTickSpacing(Math.max(1, this.slider.getMajorTickSpacing() / 5));
            this.slider.setValue(maxTransfer);
            final Hashtable<Integer, JComponent> labels = new Hashtable<>();
            final int spacing = Math.max(1, this.slider.getMajorTickSpacing());
            for (int value = maxTransfer; value >= 1; value -= spacing) {
                labels.put(value, new JLabel(String.valueOf(value)));
            }
            if (!labels.containsKey(1)) {
                labels.put(1, new JLabel("1"));
            }
            if (!labels.containsKey(maxTransfer)) {
                labels.put(maxTransfer, new JLabel(String.valueOf(maxTransfer)));
            }
            this.slider.setLabelTable(labels);
        } else {
            this.slider.setEnabled(false);
            final Hashtable<Integer, JComponent> labels = new Hashtable<>();
            labels.put(0, new JLabel("0"));
            this.slider.setLabelTable(labels);
        }

        JPanel content = new JPanel(new GridLayout(1, 2, 12, 0));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel sliderWrapper = new JPanel(new BorderLayout());
        sliderWrapper.setOpaque(false);
        sliderWrapper.add(this.slider, BorderLayout.CENTER);
        content.add(sliderWrapper);

        JPanel messagePanel = new JPanel();
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        JLabel prompt = new JLabel(canTransferEnergy ? "Adjust Shields!" : "Insufficient Energy", SwingConstants.CENTER);
        prompt.setFont(prompt.getFont().deriveFont(Font.BOLD, prompt.getFont().getSize() + 2f));
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel selectedAmount = new JLabel(canTransferEnergy ? String.valueOf(this.slider.getValue()) : "0", SwingConstants.CENTER);
        selectedAmount.setFont(selectedAmount.getFont().deriveFont(selectedAmount.getFont().getSize() + 6f));
        selectedAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel(canTransferEnergy
                ? String.format("Transfer between %d and %d (≤ 90%% of reserves)", this.slider.getMinimum(), maxTransfer)
                : "No spare energy available",
                SwingConstants.CENTER);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        messagePanel.add(Box.createVerticalGlue());
        messagePanel.add(prompt);
        messagePanel.add(Box.createVerticalStrut(12));
        messagePanel.add(selectedAmount);
        messagePanel.add(Box.createVerticalStrut(12));
        messagePanel.add(hint);
        messagePanel.add(Box.createVerticalGlue());

        content.add(messagePanel);

        add(content, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        JButton apply = buildButton("Apply", e -> {
            int req = this.slider.getValue();
            if (req >= 1) {
                game.shields(req);
                game.turn();
            }
            controller.setDefaultView(game);
        });
        apply.setEnabled(canTransferEnergy);

        this.slider.addChangeListener(e -> {
            int value = Math.max(this.slider.getMinimum(), this.slider.getValue());
            if (value != this.slider.getValue()) {
                this.slider.setValue(value);
                return;
            }
            selectedAmount.setText(String.valueOf(value));
            apply.setEnabled(value >= 1);
        });

        JButton cancel = buildButton("Cancel", e -> controller.setDefaultView(game));
        actions.add(apply);
        actions.add(cancel);
        add(actions, BorderLayout.SOUTH);

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