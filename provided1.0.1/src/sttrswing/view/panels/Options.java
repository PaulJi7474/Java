package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Options view: presents game action buttons.
 * Public API (per spec):
 *  - public Options(GameModel game, GameController controller)
 *  - public void buildOptionButtons(GameModel game, GameController controller)
 *  - public JButton scanInQuadrantButton(GameModel game, GameController controller)
 *  - public JButton scanNearbyQuadrantsButton(GameModel game, GameController controller)
 *  - public JButton shieldsButton(GameModel game, GameController controller)
 *  - public JButton torpedoButton(GameModel game, GameController controller)
 *  - public JButton phasersButton(GameModel game, GameController controller)
 */
public class Options extends View {

    /**
     * Constructs an Options view.
     * @param game        game state we use to construct this view
     * @param controller  controller state for navigation / actions
     */
    public Options(final GameModel game, final GameController controller) {
        super("Options");
        Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(controller, "controller must not be null");

        // title
        addLabel(new JLabel("Game Options"));

        // content area (button area)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buildOptionButtons(game, controller);

        revalidate();
        repaint();
    }

    /**
     * Constructs the various game options as JButton to be presented on this Options.
     * @param game        game state for button actions
     * @param controller  controller for screen transitions
     */
    public void buildOptionButtons(final GameModel game, final GameController controller) {
        // setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JButton inQuadrant = scanInQuadrantButton(game, controller);
        JButton nearby     = scanNearbyQuadrantsButton(game, controller);
        JButton shields    = shieldsButton(game, controller);
        JButton torpedo    = torpedoButton(game, controller);
        JButton phasers    = phasersButton(game, controller);

        // vertical layout with spacing
        inQuadrant.setAlignmentX(Component.LEFT_ALIGNMENT);
        nearby.setAlignmentX(Component.LEFT_ALIGNMENT);
        shields.setAlignmentX(Component.LEFT_ALIGNMENT);
        torpedo.setAlignmentX(Component.LEFT_ALIGNMENT);
        phasers.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(Box.createVerticalStrut(4)); add(inQuadrant);
        add(Box.createVerticalStrut(4)); add(nearby);
        add(Box.createVerticalStrut(4)); add(shields);
        add(Box.createVerticalStrut(4)); add(torpedo);
        add(Box.createVerticalStrut(4)); add(phasers);
        add(Box.createVerticalGlue());
    }

    /**
     * Creates a JButton configured to enable scanning the current quadrant,
     * triggering a game turn and then transitioning to the current quadrant scan view.
     * @param game        reference to the game state for use in the action listener.
     * @param controller  reference to the controller for screen transition.
     * @return the configured JButton
     */
    public JButton scanInQuadrantButton(final GameModel game, final GameController controller) {
        ActionListener action = e -> {
            game.scanQuadrant();
            game.turn();
            controller.setCurrentQuadrantScanView(game);
        };
        JButton btn = buildButton("Scan: Current Quadrant", action);
        return btn;
    }

    /**
     * Creates a JButton configured to enable scanning nearby quadrants,
     * triggering a game turn and then transitioning to the sector scan view.
     * @param game        reference to the game state for use in the action listener.
     * @param controller  reference to the controller for screen transition.
     * @return the configured JButton
     */
    public JButton scanNearbyQuadrantsButton(final GameModel game, final GameController controller) {
        ActionListener action = e -> {
            // “扫描附近象限”的数据来自 getSurroundingQuadrants()；这里只需推动时间并切换界面
            /** 
             * The data for "scan nearby quadrants" comes from getSurroundingQuadrants();
             * here we just need to advance time and switch the view.
            */
            game.turn();
            controller.setScanNearbyQuadrantView(game);
        };
        JButton btn = buildButton("Scan: Nearby Quadrants", action);
        return btn;
    }

    /**
     * Creates a JButton configured to enable setting the GameController to the shields view.
     * @param game        game state
     * @param controller  game controller used for manipulating the user view
     * @return the configured JButton
     */
    public JButton shieldsButton(final GameModel game, final GameController controller) {
        return null;
    }

    /**
     * Constructs a JButton that has an ActionListener bound to request the controller
     * to go to the torpedo view.
     * @param game        game state
     * @param controller  controller state
     * @return the configured JButton
     */
    public JButton torpedoButton(final GameModel game, final GameController controller) {
        ActionListener action = e -> controller.setTorpedoView(game);
        JButton btn = buildButton("Torpedoes", action);
        return btn;
    }

    /**
     * Creates a JButton with an action bound that will tell the controller to go to the phaser
     * attack screen. Tracks the JButton and ActionListener for cleanup. Will set the button to
     * disabled if there is not sufficient energy for phasers to be an option.
     * @param game        Game state for use in any relevant actions
     * @param controller  Reference to the Controller, so we can alter screens
     * @return the configured JButton
     */
    public JButton phasersButton(final GameModel game, final GameController controller) {
        ActionListener action = e -> controller.setPhaserAttackView(game);
        JButton btn = buildButton("Phasers", action);
        // Disable if there is not enough energy (consistent with the spec "not enough energy to be an option")
        try {
            if (!game.hasSpareEnergy()) {
                btn.setEnabled(false);
                btn.setToolTipText("Not enough energy to fire phasers.");
            }
        } catch (Throwable ignored) {
            // If the interface implementation does not support it yet, do not block the interface
        }
        return btn;
    }
}
