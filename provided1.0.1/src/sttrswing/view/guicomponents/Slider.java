package sttrswing.view.guicomponents;

import javax.swing.JSlider;

/**
 * Simple wrapper around {@link JSlider} configured for the assignment's
 * "energy" selection UX. It always starts at 0, renders ticks and labels and
 * keeps the major tick spacing within a sensible range (max/5 or 150).
 */
public class Slider extends JSlider {

  private static final int MIN_VALUE = 0;

  /**
   * Construct a slider with no specific maximum; equivalent to
   * {@code new Slider(0, 0)} so existing call sites that relied on the
   * parameter-less constructor continue to compile.
   */
  public Slider() {
    this(0, 0);
  }

  /**
   * Construct a slider that represents the range {@code [0, max]}. The current
   * value defaults to {@code max} (or 0 if {@code max} is negative).
   *
   * @param max maximum selectable energy value.
   */
  public Slider(int max) {
    this(max, max);
  }

  /**
   * Construct a slider that represents the range {@code [0, max]} with an
   * initial value.
   *
   * @param max   maximum selectable energy value.
   * @param value initial value for the slider.
   */
  public Slider(int max, int value) {
    super(MIN_VALUE, Math.max(MIN_VALUE, max), clamp(value, max));
    configure(Math.max(MIN_VALUE, max));
  }

  private static int clamp(int value, int max) {
    int upper = Math.max(MIN_VALUE, max);
    if (value < MIN_VALUE) {
      return MIN_VALUE;
    }
    if (value > upper) {
      return upper;
    }
    return value;
  }

  private void configure(int max) {
    int spacing = max == 0 ? 1 : Math.max(1, Math.min(150, max / 5));
    setMajorTickSpacing(spacing);
    setPaintTicks(true);
    setPaintLabels(true);
    setOrientation(JSlider.VERTICAL);
  }
}