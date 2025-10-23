package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;
import sttrswing.view.guicomponents.Slider;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Objects;

/**
 * PhaserAttack screen: choose energy for phaser attack.
 * Public API strictly per spec:
 * - public PhaserAttack(GameModel game, GameController controller)
 * - public Slider getSlider()
 */
public class PhaserAttack extends View {

    // private final GameModel game;
    // private final GameController controller;
    private final Slider slider; // kept for testability via getSlider()
    private final JTextField energyField;

    /**
     * Constructs a new PhaserAttack instance.
     * 
     * @param game       game state we use to construct this view
     * @param controller controller state for navigation / actions
     */
    public PhaserAttack(GameModel game, GameController controller) {
        super("Phaser Attack");
        Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(controller, "controller must not be null");

        // title (reuse View's addLabel for consistent style)
        addLabel(new JLabel("Phaser Attack"));

        final int currentEnergy = Math.max(0, game.playerEnergy()); // cannot spend all energy
        final int maxEnergy = Math.max(0, game.spareEnergy()); 
        final int minSpend = 2; // minimum energy to fire phasers cause the damage should be integer
        final boolean hasSpendableEnergy = maxEnergy >= minSpend; //check if we can spend energy

        this.slider = new Slider(maxEnergy, JSlider.VERTICAL);
        if (hasSpendableEnergy) {
            this.slider.setMinimum(minSpend);
            this.slider.setValue(minSpend);
        } else {
            this.slider.setEnabled(false);
        }
        this.energyField = new JTextField(6);
        if (hasSpendableEnergy) {
            this.energyField.setText(String.valueOf(this.slider.getValue()));
        } else {
            this.energyField.setEnabled(false);
        }

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

        JPanel inputPanel = new JPanel();
        inputPanel.setOpaque(false);
        inputPanel.add(new JLabel("Energy:"));
        inputPanel.add(this.energyField);
        sliderWrapper.add(inputPanel, BorderLayout.SOUTH);
        layout.add(sliderWrapper, BorderLayout.CENTER);

        String availabilityText;
        if (hasSpendableEnergy) {
            availabilityText = String.format(
                    "Enter %d-%d energy (current: %d)",
                    minSpend,
                    maxEnergy,
                    currentEnergy);
        } else {
            availabilityText = "Not enough energy to fire phasers.";
        }
        JLabel availability = new JLabel(availabilityText, SwingConstants.CENTER);
        availability.setOpaque(false);
        layout.add(availability, BorderLayout.SOUTH);

        add(layout, BorderLayout.CENTER);

        // Action buttons
        JPanel actions = new JPanel();
        actions.setOpaque(false);

        final Runnable[] validateInput = new Runnable[1];

        JButton confirm = buildButton("Confirm", e -> {
            String text = this.energyField.getText().trim();
            try {
                int spend = Integer.parseInt(text);
                if (spend >= minSpend && spend < currentEnergy && spend <= maxEnergy) {
                    game.firePhasers(spend); // GameModel public API
                    game.turn(); // advance a turn
                }
            } catch (NumberFormatException ignore) {
                // Should not happen because the button is disabled when invalid
            }
            controller.setDefaultView(game); // return to default view
        });
        confirm.setEnabled(hasSpendableEnergy);

        final boolean[] updating = {false};
        this.slider.addChangeListener(e -> {
            if (updating[0]) {
                return;
            }
            int value = this.slider.getValue();
            amount.setText(String.valueOf(value));
            if (hasSpendableEnergy) {
                updating[0] = true;
                this.energyField.setText(String.valueOf(value));
                updating[0] = false;
                if (validateInput[0] != null) {
                    validateInput[0].run();
                }
            }
        });

        validateInput[0] = () -> {
            if (updating[0]) {
                return;
            }
            String text = energyField.getText().trim();
            if (text.isEmpty()) {
                confirm.setEnabled(false);
                return;
            }
            try {
                int value = Integer.parseInt(text);
                boolean valid = value >= minSpend && value < currentEnergy && value <= maxEnergy;
                confirm.setEnabled(valid);
                if (valid) {
                    amount.setText(String.valueOf(value));
                    updating[0] = true;
                    slider.setValue(value);
                    updating[0] = false;
                }
            } catch (NumberFormatException ex) {
                confirm.setEnabled(false);
            }
        };

        DocumentListener listener = new DocumentListener() {
            private void update() {
                validateInput[0].run();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        };
        this.energyField.getDocument().addDocumentListener(listener);
        validateInput[0].run();

        if (!hasSpendableEnergy) {
            confirm.setEnabled(false);
        }

        JButton cancel = buildButton("Cancel", e -> controller.setDefaultView(game));

        actions.add(confirm);
        actions.add(cancel);
        add(actions, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Present for Testability reasons, returns the Slider used in the phaser attack
     * screen.
     * 
     * @return the Slider used in the phaser attack screen.
     */
    public Slider getSlider() {
        return this.slider;
    }
}