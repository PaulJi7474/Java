// sttrswing/view/StandardLayoutView.java
package sttrswing.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StandardLayoutView extends View {
  private final JPanel sideColumn;
  private final ArrayList<View> viewPanels = new ArrayList<>(4);

  public StandardLayoutView(String title) {
    super(title);
    setLayout(new BorderLayout(16, 16));
    setBackground(Pallete.BLACK.color());
    setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    sideColumn = new JPanel();
    sideColumn.setLayout(new BoxLayout(sideColumn, BoxLayout.Y_AXIS));
    sideColumn.setOpaque(false);
  }

  /** Adds a View up to a maximum of 4; returns this for chaining. */
  public StandardLayoutView addViewPanel(View view) {
    if (viewPanels.size() >= 4) {
      throw new RuntimeException("StandardLayoutView can hold at most 4 panels.");
    }

    if (viewPanels.isEmpty()) {
      view.setOpaque(true);
      view.setBackground(Pallete.BLACK.color());
    } else {
      view.setOpaque(false);
    }
    view.setBorder(BorderFactory.createEmptyBorder());
    viewPanels.add(view);

    if (viewPanels.size() == 1) {
      add(view, BorderLayout.CENTER);
    } else {
      if (sideColumn.getParent() == null) {
        add(sideColumn, BorderLayout.EAST);
      }
      if (sideColumn.getComponentCount() > 0) {
        sideColumn.add(Box.createVerticalStrut(16));
      }
      view.setAlignmentX(Component.LEFT_ALIGNMENT);
      sideColumn.add(view);
    }

    revalidate();
    repaint();
    return this;
  }

  /** For testability: returns a copy of the added views list. */
  public ArrayList<View> getViewPanels() {
    return new ArrayList<>(viewPanels);
  }
}