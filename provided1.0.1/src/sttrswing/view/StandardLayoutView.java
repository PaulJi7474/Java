package sttrswing.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * 2 × 2 grid layout container view: accepts up to 4 child Views.
 * Order of addition: row-first, left to right. If only 3 Views are added,
 * the bottom-right corner remains empty.
 */
public class StandardLayoutView extends View {

    private final ArrayList<View> panels = new ArrayList<>(4);

    /**
     * Creates an empty {@code StandardLayoutView} with the layout preconfigured,
     * ready to receive up to 4 Views.
     *
     * @param title title to display on the top of the panel
     */
    public StandardLayoutView(String title) {
        super(title);
        setLayout(new GridLayout(2, 2, 8, 8));
        setOpaque(true);
    }

    /**
     * Adds a {@link View} to the layout of this {@code StandardLayoutView} up to a maximum of 4.
     * Views are added in row-major order (top-left to bottom-right). This method triggers
     * {@link #revalidate()} and {@link #repaint()} and returns {@code this} for chaining.
     *
     * @param view View to add as a panel; a {@code StandardLayoutView} can have up to 4 panels added
     * @return a reference to this instance so you can chain these method calls together,
     *         e.g. {@code example.addViewPanel(a).addViewPanel(b);}
     * @throws RuntimeException if a View beyond the max panel count is added
     */
    public StandardLayoutView addViewPanel(View view) {
        if (panels.size() >= 4) {
            throw new RuntimeException("A StandardLayoutView can only contain up to 4 panels.");
        }
        panels.add(view);
        add(view);
        revalidate();
        repaint();
        return this;
    }

    /**
     * Here purely for testability reasons.
     *
     * @return a new list containing all {@link View}s that were added to this {@code StandardLayoutView}
     */
    public ArrayList<View> getViewPanels() {
        return new ArrayList<>(panels);
    }
}
