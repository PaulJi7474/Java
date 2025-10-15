package sttrswing.controller;

import sttrswing.model.interfaces.GameModel;
import sttrswing.view.StartView;
import sttrswing.view.StandardLayoutView;
import sttrswing.view.View;
import sttrswing.view.WinGameView;
import sttrswing.view.LoseGameView;
import sttrswing.view.panels.EnterpriseStatus;
import sttrswing.view.panels.Options;
import sttrswing.view.panels.PhaserAttack;
import sttrswing.view.panels.QuadrantNavigation;
import sttrswing.view.panels.QuadrantScan;
import sttrswing.view.panels.Shield;
import sttrswing.view.panels.Torpedo;
import sttrswing.view.panels.WarpNavigation;

import javax.swing.*;
import java.awt.*;

public class GameController extends JFrame {
  private static final String DEFAULT_HEADER =
      "WELCOME CAPTAIN   Click the Start button to start the game!";
  private final Dimension windowSize;
  private final GameModel game;
  private final JMenu fileMenu = new JMenu("File");
  private View currentView;

  public GameController(Dimension windowSize, GameModel game) {
    this.windowSize = windowSize;
    this.game = game;
  }

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
      fileMenu.removeAll();
      JMenuItem save = new JMenuItem("Save");
      save.addActionListener(e -> JOptionPane.showMessageDialog(this, "Save not implemented yet."));
      JMenuItem load = new JMenuItem("Load");
      load.addActionListener(
          e -> {
            JOptionPane.showMessageDialog(this, "Load not implemented yet.");
            setDefaultView(game);
          });
      fileMenu.add(save);
      fileMenu.add(load);
      mb.add(fileMenu);
      setJMenuBar(mb);
    }

    // 规范要求：Creates a StartView and sets for the GameControllers current view.
    StartView startView = new StartView(game, this);
    currentView = startView;
    setContentPane(startView);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setWinGameView(GameModel game) {
    WinGameView view = new WinGameView(game, this);
    currentView = view;
    setContentPane(view);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setLoseGameView(GameModel game) {
    LoseGameView view = new LoseGameView(game, this);
    currentView = view;
    setContentPane(view);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setQuadrantNavigationView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Quadrant Navigation");
    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setCurrentQuadrantScanView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Current Quadrant Scan");
    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setScanNearbyQuadrantView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Nearby Quadrants Scan");
    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setWarpNavigationView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Warp Navigation");
    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setPhaserAttackView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Phaser Attack");
    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setTorpedoView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Torpedoes");
    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  /**
   * 默认落地视图：检查是否游戏结束；未结束则呈现「当前象限地图 + 企业号状态 + 选项菜单」的标准布局。
   * 这里先放空容器；等你把各面板类写好（EnterpriseStatus、QuadrantScan、Options），
    * 再把它们按顺序 add 进去。
   */
  public void setDefaultView(GameModel game) {
    if (game.hasWon()) {
      setWinGameView(game);
      return;
    }
    if (game.hasLost()) {
      setLoseGameView(game);
      return;
    }

    StandardLayoutView layout =
        new StandardLayoutView("WELCOME CAPTAIN   Click the Start button to start the game!");
    layout.addViewPanel(new QuadrantScan(game))
        .addViewPanel(new EnterpriseStatus(game))
        .addViewPanel(new Options(game, this));
    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setShieldsView(GameModel game) {
    StandardLayoutView layout = new StandardLayoutView("Shields");
    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  // 仅为测试用：返回当前设置到 JFrame 的 View
  public View getView() {
    return currentView;
  }

  @Override
  public void setTitle(String title) {
    super.setTitle(title);
  }

  @Override
  public String getTitle() {
    return super.getTitle();
  }
}