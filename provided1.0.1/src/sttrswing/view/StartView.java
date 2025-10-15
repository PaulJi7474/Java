package sttrswing.view;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;

import javax.swing.*;
import java.awt.*;
import java.awt.Insets;

/**
 * StartView per spec: show a JTextArea + a Start JButton,
 * and expose getButton()/getText() for testability.
 */
public class StartView extends View {
  private final JTextArea text;
  private final JButton button;

  public StartView(GameModel game, GameController controller) {
    super("Start"); // ✅ 关键：父类没有无参构造器，必须显式调用
    setLayout(new BorderLayout());
    setBackground(Pallete.BLACK.color());

    text = new JTextArea("WELCOME CAPTAIN Click Start to begin the game.");
    text.setEditable(false);
    text.setOpaque(false);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    text.setForeground(Pallete.WHITE.color());
    text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    text.setMargin(new Insets(10, 15, 10, 15));

    // 用父类的 buildButton 完成追踪
    button = buildButton("Start", e -> controller.setDefaultView(game));

    add(text, BorderLayout.CENTER);
    JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
    south.setOpaque(false);
    south.add(button);
    add(south, BorderLayout.SOUTH);
  }

  // Javadoc: only for testability
  public JButton getButton() { return button; }
  public JTextArea getText() { return text; }
}
