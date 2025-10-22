package sttrswing.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Base View per spec: has a title, black background (from Pallete),
 * label/button/listener tracking, and cleanup.
 */
public class View extends JPanel {
    private String title;                      // title of this View
    private JLabel lastLabel;                  // the last label added
    private final ArrayList<ActionListener> listeners = new ArrayList<>(); // tracked listeners
    private final ArrayList<JButton> buttons = new ArrayList<>();          // tracked buttons

    /**
     * Constructs a {@code View} with the given title, a black background, and a {@link BorderLayout}.
     *
     * @param title the title associated with this view; if {@code null}, it is treated as an empty string
     */
    public View(String title) {
        this.title = (title == null ? "" : title);
        setBackground(Pallete.BLACK.color());
        setLayout(new BorderLayout());
    }

    /**
     * Returns the title of this view.
     *
     * @return the current title (never {@code null})
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this view.
     *
     * @param title the new title; if {@code null}, an empty string is stored
     */
    public void setTitle(String title) {
        this.title = (title == null ? "" : title);
    }

    /**
     * Returns the most recently added {@link JLabel} via {@link #addLabel(JLabel)}.
     *
     * @return the last label added, or {@code null} if none has been added
     */
    public JLabel getLabel() {
        return lastLabel;
    }

    /**
     * Styles the given label (white, bold, 16pt, non-opaque), adds it to the top of this view,
     * records it as the "last label", and triggers layout/paint updates.
     *
     * @param label the label to add; if {@code null}, this method is a no-op
     */
    public void addLabel(JLabel label) {
        if (label == null) return;
        label.setForeground(Pallete.WHITE.color());
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        label.setOpaque(false);
        this.lastLabel = label;
        add(label, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    /**
     * Tracks an {@link ActionListener} for later cleanup.
     *
     * @param action the listener to track; ignored if {@code null}
     */
    public void trackListener(ActionListener action) {
        if (action != null) {
            listeners.add(action);
        }
    }

    /**
     * Tracks a {@link JButton} for later cleanup.
     *
     * @param button the button to track; ignored if {@code null}
     */
    public void trackButton(JButton button) {
        if (button != null) {
            buttons.add(button);
        }
    }

    /**
     * Returns a snapshot of all tracked {@link ActionListener}s.
     *
     * @return a new list containing the currently tracked listeners
     */
    public ArrayList<ActionListener> getListeners() {
        return new ArrayList<>(listeners);
    }

    /**
     * Returns a snapshot of all tracked {@link JButton}s.
     *
     * @return a new list containing the currently tracked buttons
     */
    public ArrayList<JButton> getButtons() {
        return new ArrayList<>(buttons);
    }

    /**
     * Builds a {@link JButton} with the given label, optionally attaches the provided listener,
     * and tracks both the button and the listener for later cleanup.
     *
     * @param label    the text to display on the button
     * @param listener the listener to attach; may be {@code null}
     * @return the newly created and tracked button
     */
    public JButton buildButton(String label, ActionListener listener) {
        JButton btn = new JButton(label);
        if (listener != null) {
            btn.addActionListener(listener);
            trackListener(listener);
        }
        trackButton(btn);
        return btn;
    }

    /**
     * Removes all tracked listeners from all tracked buttons, then clears the internal
     * tracking collections. Call this when the view is being disposed or replaced to
     * prevent memory leaks and duplicate event delivery.
     */
    public void cleanup() {
        for (JButton b : buttons) {
            for (ActionListener l : listeners) {
                b.removeActionListener(l);
            }
        }
        listeners.clear();
        buttons.clear();
    }
}
