package sttrswing.view;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;

import javax.swing.*;
import java.awt.*;
import sttrswing.view.Pallete;

/**
 * StartView: The top-left panel with welcome text and "START" button.
 * <ul>
 *   <li>Text: WELCOME CAPTAIN...</li>
 *   <li>On button click: calls {@code controller.setCurrentQuadrantScanView(game)} to switch to layout 2.</li>
 *   <li>{@link #getButton()} / {@link #getText()}: provided for testing purposes per specification.</li>
 * </ul>
 */
public class StartView extends View {
    private final JTextArea text; // Welcome text area
    private final JButton button; // START button

    /**
     * Creates a {@code StartView} with a welcome message and a prominent START button.
     * Pressing the START button triggers a transition to the "in-progress" layout
     * via {@link GameController#setCurrentQuadrantScanView(GameModel)}.
     *
     * @param game        the game model referenced by the controller when starting
     * @param controller  the controller that handles navigation to the next layout
     */
    public StartView(GameModel game, GameController controller) {

        super("Start");
        setLayout(new BorderLayout(24, 10));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        setBackground(Pallete.BLACK.color());

        String welcome = "WELCOME CAPTAIN Click the Start button to start the game!";
        text = new JTextArea(welcome);
        text.setEditable(false);
        text.setOpaque(false);
        // text.setLineWrap(true);
        // text.setWrapStyleWord(true);
        text.setForeground(Pallete.WHITE.color());
        text.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        text.setMargin(new Insets(16, 24, 0, 24));

        JPanel intro = new JPanel(new BorderLayout());
        intro.setOpaque(false);
        intro.add(text, BorderLayout.WEST);
        add(intro, BorderLayout.NORTH);

        button = new JButton("START");
        button.setForeground(Pallete.WHITE.color());
        button.setBackground(Pallete.BLUE.color());
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(260, 180));

        // click START: switch to layout 2 (TL=Map, TR=Stats, BL=Buttons, BR=empty)
        button.addActionListener(e -> controller.setCurrentQuadrantScanView(game));

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(button);
        add(center, BorderLayout.CENTER);
    }

    /**
     * Exposed for testability: returns the START button component.
     *
     * @return the {@link JButton} used to start the game
     */
    public JButton getButton() {
        return button;
    }

    /**
     * Exposed for testability: returns the welcome text component.
     *
     * @return the {@link JTextArea} displaying the welcome message
     */
    public JTextArea getText() {
        return text;
    }
}
