package sttrswing.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * 2×2 等分布局的容器视图：最多接收 4 个子 View。
 * 加入顺序：行优先、从左到右。
 * 只加入 3 个 View 时，右下角自然留空。
 */
public class StandardLayoutView extends View {

    private final ArrayList<View> panels = new ArrayList<>(4);

    public StandardLayoutView(String title) {
        super(title);
        // 关键：2 行 2 列，等分四块
        setLayout(new GridLayout(2, 2, 8, 8));
        setOpaque(true);
    }

    /** 按顺序加入子 View，最多 4 个 */
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

    /** 测试用：返回已加入的 View 列表 */
    public ArrayList<View> getViewPanels() {
        return new ArrayList<>(panels);
    }
}
