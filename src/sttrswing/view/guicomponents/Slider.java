package sttrswing.view.guicomponents;

import javax.swing.JSlider;

/**
 * Simple wrapper around {@link JSlider} configured for the assignment's slider UX.
 */
public class Slider extends JSlider {

  public Slider(int max, int orientation) {
    super(0, Math.max(0, max));
    int actualMax = Math.max(0, max);
    setValue(actualMax);
    setOrientation(orientation);
    int spacing = actualMax == 0 ? 1 : Math.max(1, Math.min(150, actualMax / 5));
    setMajorTickSpacing(spacing);
    setPaintTicks(true);
    setPaintLabels(true);
  }

  public Slider(int max) {
    this(max, JSlider.VERTICAL);
  }
}