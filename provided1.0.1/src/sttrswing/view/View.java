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
  private String title;
  private JLabel lastLabel; // the last label added
  private final ArrayList<ActionListener> listeners = new ArrayList<>();
  private final ArrayList<JButton> buttons = new ArrayList<>();

  public View(String title) {
    this.title = (title == null ? "" : title);
    setBackground(Pallete.BLACK.color());   // 按要求：黑色背景（来自 Pallete）
    setLayout(new BorderLayout());          // 方便子类直接 add 到不同区域
  }

  // ---- Title APIs ----
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = (title == null ? "" : title); }

  // ---- Label APIs ----
  public JLabel getLabel() { return lastLabel; }

  /** 调整样式并加入到当前 View，同时记录为“lastLabel” */
  public void addLabel(JLabel label) {
    if (label == null) return;
    // 统一样式：白色字、稍大字号、不遮底
    label.setForeground(Pallete.WHITE.color());
    label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
    label.setOpaque(false);
    this.lastLabel = label;
    add(label, BorderLayout.NORTH); // 默认放顶部；子类可自行重排
    revalidate();
    repaint();
  }

  // ---- Tracking APIs ----
  public void trackListener(ActionListener action) {
    if (action != null) listeners.add(action);
  }

  public void trackButton(JButton button) {
    if (button != null) buttons.add(button);
  }

  public ArrayList<ActionListener> getListeners() {
    return new ArrayList<>(listeners);
  }

  public ArrayList<JButton> getButtons() {
    return new ArrayList<>(buttons);
  }

  /** 统一建按钮并完成追踪 */
  public JButton buildButton(String label, ActionListener listener) {
    JButton btn = new JButton(label);
    if (listener != null) {
      btn.addActionListener(listener);
      trackListener(listener);
    }
    trackButton(btn);
    return btn;
  }

  /** 清理：移除已追踪的监听器，防止内存泄漏/重复触发 */
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
