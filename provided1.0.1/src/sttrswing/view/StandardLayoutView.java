// sttrswing/view/StandardLayoutView.java
package sttrswing.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StandardLayoutView extends View {
  private final JPanel grid;
  private final ArrayList<View> viewPanels = new ArrayList<>(4);

  public StandardLayoutView(String title) {
    super(title); // 标题来自父类
    setLayout(new BorderLayout(8, 8));
    setBackground(Pallete.BLACK.color());

    // 顶部标题
    JLabel header = new JLabel(getTitle(), SwingConstants.LEFT);
    header.setForeground(Pallete.WHITE.color());
    header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
    header.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
    add(header, BorderLayout.NORTH);

    // 2x2 容器，最多 4 个子 View
    grid = new JPanel(new GridLayout(2, 2, 8, 8));
    grid.setOpaque(false);
    grid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    add(grid, BorderLayout.CENTER);
  }

  /** Adds a View up to a maximum of 4; returns this for chaining. */
  public StandardLayoutView addViewPanel(View view) {
    if (viewPanels.size() >= 4) {
      throw new RuntimeException("StandardLayoutView can hold at most 4 panels.");
    }
    viewPanels.add(view);
    grid.add(view);
    revalidate();
    repaint();
    return this;
  }

  /** For testability: returns a copy of the added views list. */
  public ArrayList<View> getViewPanels() {
    return new ArrayList<>(viewPanels);
    }
}
