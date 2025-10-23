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

        int spareEnergy = Math.max(0, game.spareEnergy());
        this.slider = new Slider(spareEnergy, JSlider.VERTICAL);

        int sliderMax = Math.max(1, spareEnergy);
        final JSlider distance = new JSlider(1, sliderMax, Math.min(3, sliderMax));
        distance.setOpaque(false);
        distance.setPaintTicks(true);
        distance.setMajorTickSpacing(Math.max(1, sliderMax / 4));

        JPanel distPanel = new JPanel(new BorderLayout(8, 8));
        distPanel.setOpaque(false);
        distPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        distPanel.add(new JLabel("Warp distance:"), BorderLayout.WEST);
        distPanel.add(distance, BorderLayout.CENTER);
        add(distPanel);

        JPanel grid = new JPanel(new GridLayout(3, 3, 6, 6));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));

        ActionListener move = e -> {
            if (!(e.getSource() instanceof DirectionButton)) {
                return;
            }

            int maxSteps = Math.max(0, game.spareEnergy() / 100);
            if (maxSteps <= 0) {
                JOptionPane.showMessageDialog(WarpNavigation.this,
                    "Not enough energy to move.",
                    "Insufficient Energy",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(Math.min(3, maxSteps), 1, maxSteps, 1)
            );
            
            JComponent editor = spinner.getEditor();
            if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
                defaultEditor.getTextField().setColumns(4);
            }

            JPanel prompt = new JPanel(new BorderLayout(6, 6));
            prompt.add(new JLabel("Enter warp distance (1-" + maxSteps + "):"), BorderLayout.NORTH);
            prompt.add(spinner, BorderLayout.CENTER);

            int option = JOptionPane.showConfirmDialog(WarpNavigation.this,
                prompt,
                "Warp Distance",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            Object value = spinner.getValue();
            int selected = value instanceof Number ? ((Number) value).intValue() : 0;
            if (selected < 1 || selected > maxSteps) {
                return;
            }

            int direction = ((DirectionButton) e.getSource()).getDirection();
            game.moveWithinQuadrant(direction, selected);
            game.turn();
            controller.setDefaultView(game);
        };

        grid.add(buildButton(4, move));
        grid.add(buildButton(3, move));
        grid.add(buildButton(2, move));

        grid.add(buildButton(5, move));
        JLabel you = new JLabel("YOU", SwingConstants.CENTER);
        you.setOpaque(false);
        you.setForeground(Color.WHITE);
        grid.add(you);
        grid.add(buildButton(1, move));

        grid.add(buildButton(6, move));
        grid.add(buildButton(7, move));
        grid.add(buildButton(8, move));

        add(grid);

    }

    /**
     * Exists for testability reasons to allow us to 
     * confirm the Slider matches some expected constraints.
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

}