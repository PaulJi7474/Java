package sttrswing.view;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;

import javax.swing.*;
import java.awt.*;

/**
 * StartView per spec: show a JTextArea + a Start JButton,
 * and expose getButton()/getText() for testability.
 */
public class StartView extends View {
  private final JTextArea text;
  private final JButton button;

  public StartView(GameModel game, GameController controller) {
    super("Start");
    setLayout(new BorderLayout(24, 24));
    setBackground(Pallete.BLACK.color());

    String welcome = "WELCOME CAPTAIN   Click the Start button to start the game!";
    text = new JTextArea(welcome);
    text.setEditable(false);
    text.setOpaque(false);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    text.setForeground(Pallete.WHITE.color());
    text.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
    text.setMargin(new Insets(16, 24, 0, 24));

    JPanel intro = new JPanel(new BorderLayout());
    intro.setOpaque(false);
    intro.add(text, BorderLayout.WEST);
    add(intro, BorderLayout.NORTH);

    button = buildButton("START", e -> controller.setDefaultView(game));
    button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
    button.setForeground(Pallete.WHITE.color());
    button.setBackground(Pallete.BLUE.color());
    button.setFocusPainted(false);
    button.setPreferredSize(new Dimension(260, 180));

    JPanel center = new JPanel(new GridBagLayout());
    center.setOpaque(false);
    center.add(button);
    add(center, BorderLayout.CENTER);
  }

  // Javadoc: only for testability
  public JButton getButton() { return button; }
  public JTextArea getText() { return text; }
}
