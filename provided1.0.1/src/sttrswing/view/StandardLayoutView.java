// sttrswing/view/StandardLayoutView.java
package sttrswing.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StandardLayoutView extends View {
  private final JPanel grid;
  private final ArrayList<View> viewPanels = new ArrayList<>(4);

  public StandardLayoutView(String title) {
    super(title);
    setLayout(new BorderLayout(8, 8));
    setBackground(Pallete.BLACK.color());

    JLabel header = new JLabel("<html><body style='width:100%'>" + getTitle() + "</body></html>",
        SwingConstants.LEFT);
    header.setForeground(Pallete.WHITE.color());
    header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
    header.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
    add(header, BorderLayout.NORTH);

    grid = new JPanel(new GridLayout(2, 2, 12, 12));
    grid.setOpaque(false);
    grid.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    add(grid, BorderLayout.CENTER);
  }

  /** Adds a View up to a maximum of 4; returns this for chaining. */
  public StandardLayoutView addViewPanel(View view) {
    if (viewPanels.size() >= 4) {
      throw new RuntimeException("StandardLayoutView can hold at most 4 panels.");
    }
    view.setOpaque(true);
    view.setBackground(Pallete.BLACK.color());
    view.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Pallete.GREYDARK.color()),
        BorderFactory.createEmptyBorder(12, 12, 12, 12)));
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
