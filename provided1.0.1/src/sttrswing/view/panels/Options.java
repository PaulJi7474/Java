package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Options panel that presents a vertical stack of game actions (navigation, warp,
 * phasers, torpedoes, shields, and scans). The panel renders seven buttons in a
 * single column and delegates navigation to {@link GameController}.
 */
public class Options extends View {

    /**
     * Constructs the Options panel and populates it with action buttons.
     *
     * @param game       the game model referenced by the action handlers
     * @param controller the controller used to switch views and coordinate actions
     */
    public Options(GameModel game, GameController controller) {
        super("Options");
        this.setLayout(new GridLayout(7, 1, 0, 8));
        buildOptionButtons(game, controller);
    }

    /**
     * Builds and adds all option buttons to this panel in the expected order:
     * <ol>
     *   <li>Quadrant Navigation</li>
     *   <li>Warp Navigation</li>
     *   <li>Phasers</li>
     *   <li>Torpedoes</li>
     *   <li>Shields</li>
     *   <li>Short Range Scan</li>
     *   <li>Long Range Scan</li>
     * </ol>
     *
     * @param game       the game model used by the handlers
     * @param controller the controller used to transition between views
     */
    public void buildOptionButtons(GameModel game, GameController controller) {
        // 1) Quadrant Navigation
        this.add(buildButton("Quadrant Navigation", 
            e -> controller.setQuadrantNavigationView(game)
        ));

        // 2) Warp Navigation
        this.add(buildButton("Warp Navigation", e -> controller.setWarpNavigationView(game)));

        // 3) Phasers
        this.add(phasersButton(game, controller));

        // 4) Torpedoes
        this.add(torpedoButton(game, controller));

        // 5) Shields
        this.add(shieldsButton(game, controller));

        // 6) Short Range Scan — current quadrant (8×8 sector)
        this.add(scanInQuadrantButton(game, controller));

        // 7) Long Range Scan — 3×3 adjacent quadrants
        this.add(scanNearbyQuadrantsButton(game, controller));
    }

    /**
     * Creates the <em>Phasers</em> button. If the game model exposes {@code hasSpareEnergy()},
     * the button will be disabled when energy is insufficient; otherwise it remains enabled.
     * Clicking the button opens the Phaser Attack view.
     *
     * @param game       the game model providing energy availability (if supported)
     * @param controller the controller used to navigate to the phaser view
     * @return a configured and possibly disabled {@link JButton}
     */
    public JButton phasersButton(GameModel game, GameController controller) {
        ActionListener listener = e -> controller.setPhaserAttackView(game);
        JButton btn = buildButton("Phasers", listener);
        try {
            btn.setEnabled(game.hasSpareEnergy());
        } catch (Throwable ignore) {
            // If the implementation does not provide hasSpareEnergy(), keep enabled.
        }
        return btn;
    }

    /**
     * Creates the <em>Torpedoes</em> button. If the game model exposes {@code hasSpareTorpedoes()},
     * the button will be disabled when there is no ammunition; otherwise it remains enabled.
     * Clicking the button opens the Torpedoes view.
     *
     * @param game       the game model providing torpedo availability (if supported)
     * @param controller the controller used to navigate to the torpedo view
     * @return a configured and possibly disabled {@link JButton}
     */
    public JButton torpedoButton(GameModel game, GameController controller) {
        ActionListener listener = e -> controller.setTorpedoView(game);
        JButton btn = buildButton("10 x Torpedoes", listener);
        try {
            btn.setEnabled(game.hasSpareTorpedoes());
        } catch (Throwable ignore) {
            // If the implementation differs, keep enabled.
        }
        return btn;
    }

    /**
     * Creates the <em>Shields</em> button. Clicking the button opens the Shields view
     * where shield levels can be adjusted.
     *
     * @param game       the game model referenced by the view
     * @param controller the controller used to navigate to the shields view
     * @return a configured {@link JButton}
     */
    public JButton shieldsButton(GameModel game, GameController controller) {
        return buildButton("Shields", e -> controller.setShieldsView(game));
    }

    /**
     * Creates the <em>Short Range Scan</em> button. When clicked:
     * <ol>
     *   <li>Invokes {@code game.scanQuadrant()} to record/report the scan.</li>
     *   <li>Advances the game turn via {@code game.turn()}.</li>
     *   <li>Transitions to the current quadrant scan view.</li>
     * </ol>
     *
     * @param game       the game model on which the scan and turn are performed
     * @param controller the controller used to navigate back to the current-scan view
     * @return a configured {@link JButton}
     */
    public JButton scanInQuadrantButton(GameModel game, GameController controller) {
        return buildButton("Short Range Scan", e -> {
            game.scanQuadrant();
            game.turn();
            controller.setCurrentQuadrantScanView(game);
        });
    }

    /**
     * Creates the <em>Long Range Scan</em> button. When clicked it advances the game turn
     * and transitions to the nearby quadrants scan view (3×3 surrounding quadrants).
     * No call to {@code game.scanQuadrant()} is required for the long-range overview.
     *
     * @param game       the game model on which the turn is performed
     * @param controller the controller used to navigate to the long-range scan view
     * @return a configured {@link JButton}
     */
    public JButton scanNearbyQuadrantsButton(GameModel game, GameController controller) {
        return buildButton("Long Range Scan", e -> {
            game.turn();
            controller.setScanNearbyQuadrantView(game);
        });
    }
}
