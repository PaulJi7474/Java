package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;
import sttrswing.view.Pallete;

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

    private final JPanel buttonColumn;

    /**
     * Constructs an Options view.
     * @param game        game state we use to construct this view
     * @param controller  controller state for navigation / actions
     */
    public Options(final GameModel game, final GameController controller) {
        super("Options");
        Objects.requireNonNull(game, "game must not be null");
        Objects.requireNonNull(controller, "controller must not be null");

        setLayout(new BorderLayout(8, 8));
        addLabel(new JLabel("Command Options"));

        buttonColumn = new JPanel(new GridLayout(0, 1, 8, 8));
        buttonColumn.setOpaque(false);
        buttonColumn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(buttonColumn, BorderLayout.CENTER);

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
        buttonColumn.removeAll();

        JButton quadrantNav = buildButton("Quadrant Navigation", e -> controller.setQuadrantNavigationView(game));
        JButton warpNav = buildButton("Warp Navigation", e -> controller.setWarpNavigationView(game));
        JButton phasers = phasersButton(game, controller);
        JButton torpedo = torpedoButton(game, controller);
        JButton shields = shieldsButton(game, controller);
        JButton shortRange = scanInQuadrantButton(game, controller);
        JButton longRange = scanNearbyQuadrantsButton(game, controller);

        JButton[] buttons = new JButton[]{
            quadrantNav, warpNav, phasers, torpedo, shields, shortRange, longRange
        };
        for (JButton button : buttons) {
            button.setFocusPainted(false);
            button.setBackground(Pallete.BLUEDARK.color());
            button.setForeground(Pallete.WHITE.color());
            button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Pallete.GREY.color()),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            button.setPreferredSize(new Dimension(220, 44));
        }

        buttonColumn.add(quadrantNav);
        buttonColumn.add(warpNav);
        buttonColumn.add(phasers);
        buttonColumn.add(torpedo);
        buttonColumn.add(shields);
        buttonColumn.add(shortRange);
        buttonColumn.add(longRange);
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
        JButton btn = buildButton("Short Range Scan", action);
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
        ActionListener action = e -> {
            game.turn();
            controller.setScanNearbyQuadrantView(game);
        };
        JButton btn = buildButton("Long Range Scan", action);
        return btn;
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
        JButton btn = buildButton("10 x Torpedoes", action);
        try {
            if (!game.hasSpareTorpedoes()) {
                btn.setEnabled(false);
                btn.setToolTipText("No torpedoes remaining.");
            }
        } catch (Throwable ignored) {
        }
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
        try {
            if (!game.hasSpareEnergy()) {
                btn.setEnabled(false);
                btn.setToolTipText("Not enough energy to fire phasers.");
            }
        } catch (Throwable ignored) {
        }
        return btn;
    }
}
