package sttrswing.view;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;

import javax.swing.*;
import java.awt.*;
import sttrswing.view.Pallete;

/**
 * StartView：左上角的欢迎与“START”按钮面板。
 * - 文本：WELCOME CAPTAIN...
 * - 点击按钮：调用 controller.setCurrentQuadrantScanView(game) 切换到图2布局。
 * - getButton()/getText()：按规范仅用于测试。
 */
public class StartView extends View {
  private final JTextArea text;
  private final JButton button;

  public StartView(GameModel game, GameController controller) {
    
    super("Start");
    setLayout(new BorderLayout(24, 24));
    setBackground(Pallete.BLACK.color());

    String welcome = "WELCOME CAPTAIN Click the Start button to start the game!";
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

    button = new JButton("START");
    button.setForeground(Pallete.WHITE.color());
    button.setBackground(Pallete.BLUE.color());
    button.setFocusPainted(false);
    button.setPreferredSize(new Dimension(260, 180));

    // 点击 START：切换为图2布局（TL=Map, TR=Stats, BL=Buttons, BR=空）
    button.addActionListener(e -> controller.setCurrentQuadrantScanView(game));

    JPanel center = new JPanel(new GridBagLayout());
    center.setOpaque(false);
    center.add(button);
    add(center, BorderLayout.CENTER);
  }

  // 仅用于测试
  public JButton getButton() { return button; }
  public JTextArea getText() { return text; }
}
