package sttrswing.controller;

import sttrswing.model.interfaces.GameModel;
import sttrswing.view.StartView;
import sttrswing.view.StandardLayoutView;
import sttrswing.view.View;
import sttrswing.view.WinGameView;
import sttrswing.view.LoseGameView;
import sttrswing.view.panels.EnterpriseStatus;
import sttrswing.view.panels.Options;
import sttrswing.view.panels.QuadrantScan;
import sttrswing.view.panels.NearbyQuadrantScan;
import sttrswing.view.panels.WarpNavigation;
import sttrswing.view.panels.QuadrantNavigation;
import sttrswing.view.panels.PhaserAttack;
import sttrswing.view.panels.Shield;
import sttrswing.view.panels.Torpedo;

import javax.swing.*;
import java.awt.*;

/**
 * Top-level controller responsible for configuring the application window
 * and orchestrating transitions between views in the Star Trek Swing game.
 */
public class GameController extends JFrame {

    private final Dimension windowSize;
    private final GameModel game;
    private final JMenu fileMenu = new JMenu("File");
    private View currentView;

    /**
     * Constructs a new {@code GameController}.
     *
     * @param windowSize the desired dimensions of the top-level window
     * @param game       the game model to be coordinated by this controller
     */
    public GameController(Dimension windowSize, GameModel game) {
        this.windowSize = windowSize;
        this.game = game;
    }

    /**
     * Exposed for testability: returns the {@link JMenu} used for file actions.
     *
     * @return the File menu instance
     */
    public JMenu getFileMenu() {
        return fileMenu;
    }

    /**
     * Ends the controller and disposes the underlying {@link JFrame}.
     * After calling this method, the window is destroyed and resources are released.
     */
    public void end() {
        dispose();
    }

    /**
     * Initialises the primary window (if not yet created), wires up the menu bar
     * with Save/Load actions, and displays the initial four-panel layout:
     * <ul>
     *   <li>Start view</li>
     *   <li>Enterprise status</li>
     *   <li>Current quadrant scan</li>
     *   <li>Options</li>
     * </ul>
     * This method is idempotent with respect to window creation; repeated calls
     * will refresh the content pane.
     *
     * @param game the game state to be visualised and manipulated by the views
     */
    public void start(GameModel game) {
        // Initial window & menu
        if (!isDisplayable()) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(windowSize);
            setLocationRelativeTo(null);
            setTitle("Star Trek");

            
            fileMenu.removeAll();

            // Save
            JMenuItem save = new JMenuItem("Save");
            save.addActionListener(e -> {
                GameSaver saver = new GameSaver(game.export(), "data/save.trek");
                saver.save();
                JOptionPane.showMessageDialog(
                        this,
                        saver.success() ? "Saved to data/save.trek" : "Save failed. Check write permission.",
                        "Save",
                        saver.success() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            });

            // Load
            JMenuItem load = new JMenuItem("Load");
            load.addActionListener(e -> {
                GameLoader loader = new GameLoader("data/save.trek");
                loader.load();

                if (!loader.success()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Load failed. Make sure data/save.trek exists and is valid.",
                            "Load",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    game.load(loader.buildEnterprise(), loader.buildGalaxy());
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Load failed. The save file could not be parsed.",
                            "Load",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                setCurrentQuadrantScanView(game);

                String ent = loader.enterpriseLine();
                java.util.List<String> galaxy = loader.galaxyLines();
                JOptionPane.showMessageDialog(
                        this,
                        "Loaded OK from data/save.trek.\n" + ent + "\nGalaxy lines loaded: " + galaxy.size(),
                        "Load",
                        JOptionPane.INFORMATION_MESSAGE);

            });

            JMenuBar mb = new JMenuBar();

            fileMenu.add(save);
            fileMenu.add(load);
            mb.add(fileMenu);
            setJMenuBar(mb);
        }

        StartView startView = new StartView(game, this);
        StandardLayoutView layout = new StandardLayoutView("Star Trek");
        layout.addViewPanel(startView)
                .addViewPanel(new EnterpriseStatus(game))
                .addViewPanel(new QuadrantScan(game))
                .addViewPanel(new Options(game, this));

        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Transitions the UI to the winning game-over screen.
     *
     * @param game the game state to present on the win view
     */
    public void setWinGameView(GameModel game) {
        WinGameView view = new WinGameView(game, this);
        currentView = view;
        setContentPane(view);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Transitions the UI to the losing game-over screen.
     *
     * @param game the game state to present on the lose view
     */
    public void setLoseGameView(GameModel game) {
        LoseGameView view = new LoseGameView(game, this);
        currentView = view;
        setContentPane(view);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Displays the Quadrant Navigation layout unless the game is already
     * in a terminal state. The layout is:
     * <ul>
     *   <li>TL: Current quadrant map</li>
     *   <li>TR: Enterprise status</li>
     *   <li>BL: Options</li>
     *   <li>BR: Quadrant navigation controls</li>
     * </ul>
     *
     * @param game the current game state used by the panels
     */
    public void setQuadrantNavigationView(GameModel game) {
        if (game.hasWon()) {
            setWinGameView(game);
            return;
        }
        if (game.hasLost()) {
            setLoseGameView(game);
            return;
        }

        StandardLayoutView layout = new StandardLayoutView("Quadrant Navigation");
        layout.addViewPanel(new QuadrantScan(game)) // TL
                .addViewPanel(new EnterpriseStatus(game)) // TR
                .addViewPanel(new Options(game, this)) // BL
                .addViewPanel(new QuadrantNavigation(game, this)); // BR

        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Displays the "in-progress" standard layout (map, status, options)
     * with an empty fourth quadrant:
     * <ul>
     *   <li>TL: Current quadrant map</li>
     *   <li>TR: Enterprise status</li>
     *   <li>BL: Options</li>
     *   <li>BR: Empty</li>
     * </ul>
     *
     * @param game the current game state used by the panels
     */
    public void setCurrentQuadrantScanView(GameModel game) {
        StandardLayoutView layout = new StandardLayoutView("Star Trek |");
        layout.addViewPanel(new QuadrantScan(game));
        layout.addViewPanel(new EnterpriseStatus(game));
        layout.addViewPanel(new Options(game, this));

        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Displays the Long Range Scan layout, combining the current quadrant
     * view, status, options, and a 3x3 nearby quadrant scan panel.
     *
     * @param game the current game state used by the panels
     */
    public void setScanNearbyQuadrantView(GameModel game) {
        StandardLayoutView layout = new StandardLayoutView("Long Range Scan");
        layout.addViewPanel(new QuadrantScan(game));
        layout.addViewPanel(new EnterpriseStatus(game));
        layout.addViewPanel(new Options(game, this));
        layout.addViewPanel(new NearbyQuadrantScan(game));

        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
        pack();
    }

    /**
     * Displays the Warp Navigation layout unless the game is in a terminal state.
     * The layout is:
     * <ul>
     *   <li>TL: Current quadrant map</li>
     *   <li>TR: Enterprise status</li>
     *   <li>BL: Options</li>
     *   <li>BR: Warp controls</li>
     * </ul>
     *
     * @param game the current game state used by the panels
     */
    public void setWarpNavigationView(GameModel game) {
        if (game.hasWon()) {
            setWinGameView(game);
            return;
        }
        if (game.hasLost()) {
            setLoseGameView(game);
            return;
        }

        StandardLayoutView layout = new StandardLayoutView("Warp Navigation");
        layout.addViewPanel(new QuadrantScan(game)) // TL
                .addViewPanel(new EnterpriseStatus(game)) // TR
                .addViewPanel(new Options(game, this)) // BL
                .addViewPanel(new WarpNavigation(game, this)); // BR

        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Displays the Phaser Attack layout unless the game is in a terminal state.
     *
     * @param game the current game state used by the panels
     */
    public void setPhaserAttackView(GameModel game) {
        if (game.hasWon()) {
            setWinGameView(game);
            return;
        }
        if (game.hasLost()) {
            setLoseGameView(game);
            return;
        }

        StandardLayoutView layout = new StandardLayoutView("Phaser Attack");
        layout.addViewPanel(new QuadrantScan(game))
                .addViewPanel(new EnterpriseStatus(game))
                .addViewPanel(new Options(game, this))
                .addViewPanel(new PhaserAttack(game, this));

        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Displays the Torpedoes layout unless the game is in a terminal state.
     *
     * @param game the current game state used by the panels
     */
    public void setTorpedoView(GameModel game) {
        if (game.hasWon()) {
            setWinGameView(game);
            return;
        }
        if (game.hasLost()) {
            setLoseGameView(game);
            return;
        }

        StandardLayoutView layout = new StandardLayoutView("Torpedoes");
        layout.addViewPanel(new QuadrantScan(game))
                .addViewPanel(new EnterpriseStatus(game))
                .addViewPanel(new Options(game, this))
                .addViewPanel(new Torpedo(game, this));

        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Displays the default layout used during regular play unless the game
     * has already ended. The layout is:
     * <ul>
     *   <li>Current quadrant map</li>
     *   <li>Enterprise status</li>
     *   <li>Options</li>
     * </ul>
     *
     * @param game the current game state used by the panels
     */
    public void setDefaultView(GameModel game) {
        if (game.hasWon()) {
            setWinGameView(game);
            return;
        }
        if (game.hasLost()) {
            setLoseGameView(game);
            return;
        }

        StandardLayoutView layout = new StandardLayoutView(
                "WELCOME CAPTAIN   Click the Start button to start the game!");
        layout.addViewPanel(new QuadrantScan(game))
                .addViewPanel(new EnterpriseStatus(game))
                .addViewPanel(new Options(game, this));
        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Displays the Shields layout unless the game is in a terminal state.
     *
     * @param game the current game state used by the panels
     */
    public void setShieldsView(GameModel game) {
        if (game.hasWon()) {
            setWinGameView(game);
            return;
        }
        if (game.hasLost()) {
            setLoseGameView(game);
            return;
        }

        StandardLayoutView layout = new StandardLayoutView("Shields");
        layout.addViewPanel(new QuadrantScan(game))
                .addViewPanel(new EnterpriseStatus(game))
                .addViewPanel(new Options(game, this))
                .addViewPanel(new Shield(game, this));

        currentView = layout;
        setContentPane(layout);
        revalidate();
        repaint();
        if (!isVisible()) {
            setVisible(true);
        }
    }

    /**
     * Exposed for testability: returns the view currently set on the frame.
     *
     * @return the current {@link View} instance displayed by the controller
     */
    public View getView() {
        return currentView;
    }

    /**
     * {@inheritDoc}
     *
     * @param title the new title for the window
     */
    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    /**
     * {@inheritDoc}
     *
     * @return the current window title
     */
    @Override
    public String getTitle() {
        return super.getTitle();
    }
}
