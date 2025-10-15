package sttrswing.controller;

import sttrswing.model.interfaces.GameModel;
import sttrswing.view.StartView;
import sttrswing.view.StandardLayoutView;
import sttrswing.view.View;
import sttrswing.view.WinGameView;
import sttrswing.view.LoseGameView;

import javax.swing.*;
import java.awt.*;

public class GameController extends JFrame {
  private final Dimension windowSize;
  private final GameModel game;
  private final JMenu fileMenu = new JMenu("File");
  private View currentView;

  public GameController(Dimension windowSize, GameModel game) {
    this.windowSize = windowSize;
    this.game = game;
  }

  // Exposed for testability reasons.
  public JMenu getFileMenu() {
    return fileMenu;
  }

  public void end() {
    dispose();
  }

  /**
   * 首次调用：展示 StartView。
   * 之后（例如从 Start 按钮回调再调用）：可根据需要切到默认视图。
   */
  public void start(GameModel game) {
    // 基础窗口初始化（只做一次）
    if (!isDisplayable()) {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(windowSize);
      setLocationRelativeTo(null);
      setTitle("Star Trek");
      JMenuBar mb = new JMenuBar();
      buildFileMenuItems(); // Save/Load 菜单项的事件稍后接 GameSaver/GameLoader
      mb.add(fileMenu);
      setJMenuBar(mb);
    }

    // 规范要求：Creates a StartView and sets for the GameControllers current view.
    StartView startView = new StartView(game, this);
    setContentTo(startView);
  }

    public void setWinGameView(GameModel game) {
        setContentPane(new WinGameView(game, this));
        revalidate();
        repaint();
        if (!isVisible()) setVisible(true);
    }

    public void setLoseGameView(GameModel game) {
        setContentPane(new LoseGameView(game, this));
        revalidate();
        repaint();
        if (!isVisible()) setVisible(true);
    }



  public void setQuadrantNavigationView(GameModel game) {
    // 先用空容器占位，等具体面板类完成后往里 add
    StandardLayoutView layout = new StandardLayoutView("Quadrant Navigation");
    setContentTo(layout);
  }

  public void setCurrentQuadrantScanView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Current Quadrant Scan");
    setContentTo(layout);
  }

  public void setScanNearbyQuadrantView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Nearby Quadrants Scan");
    setContentTo(layout);
  }

  public void setWarpNavigationView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Warp Navigation");
    setContentTo(layout);
  }

  public void setPhaserAttackView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Phaser Attack");
    setContentTo(layout);
  }

  public void setTorpedoView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Torpedoes");
    setContentTo(layout);
  }

  /**
   * 默认落地视图：检查是否游戏结束；未结束则呈现「当前象限地图 + 企业号状态 + 选项菜单」的标准布局。
   * 这里先放空容器；等你把各面板类写好（EnterpriseStatus、QuadrantScan、Options），
   * 再把它们按顺序 add 进去。
   */
  public void setDefaultView(GameModel game) {
    // TODO：若 game 已结束，改为 setWinGameView(...) 或 setLoseGameView(...)
    StandardLayoutView layout = new StandardLayoutView("WELCOME CAPTAIN   Click the Start button to start the game!");
    // 未来：layout.addViewPanel(new QuadrantScan(game, this))
    //      .addViewPanel(new EnterpriseStatus(game))
    //      .addViewPanel(new Options(game, this));
    setContentTo(layout);
  }

  // 仅为测试用：返回当前设置到 JFrame 的 View
  public View getView() {
    return currentView;
  }

  // ---------------- private helpers ----------------

  private void buildFileMenuItems() {
    fileMenu.removeAll();

    JMenuItem save = new JMenuItem("Save");
    save.addActionListener(e -> {
      // TODO: new GameSaver("data/save.trek").save(game);
      JOptionPane.showMessageDialog(this, "Save not implemented yet.");
    });

    JMenuItem load = new JMenuItem("Load");
    load.addActionListener(e -> {
      // TODO: new GameLoader("data/save.trek").load(); 并把结果写回 game
      JOptionPane.showMessageDialog(this, "Load not implemented yet.");
      // 读档后通常回到默认布局：
      setDefaultView(game);
    });

    fileMenu.add(save);
    fileMenu.add(load);
  }

  private void setContentTo(View view) {
    this.currentView = view;
    setContentPane(view);
    revalidate();
    repaint();
    if (!isVisible()) setVisible(true);
  }
}
