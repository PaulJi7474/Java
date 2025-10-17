package sttrswing.controller;

import sttrswing.model.interfaces.GameModel;
import sttrswing.view.StartView;
import sttrswing.view.StandardLayoutView;
import sttrswing.view.View;
import sttrswing.view.WinGameView;
import sttrswing.view.LoseGameView;
import sttrswing.view.panels.EnterpriseStatus;
import sttrswing.view.panels.Options;
import sttrswing.view.panels.QuadrantScan;
import sttrswing.view.panels.NearbyQuadrantScan;
import sttrswing.view.panels.WarpNavigation;
import sttrswing.view.panels.QuadrantNavigation;
import sttrswing.view.panels.PhaserAttack;
import sttrswing.view.panels.Shield;
import sttrswing.view.panels.Torpedo;

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
/**
 * 首次显示界面：图1布局（TL=Start, TR=Stats, BL=Map, BR=Buttons）。
 * 点击 Start 后由 StartView 回调到 setCurrentQuadrantScanView(game) 切到图2布局。
 */
/**
 * 首次显示界面：图1布局（TL=Start, TR=Stats, BL=Map, BR=Buttons）。
 * 点击 Start 后由 StartView 回调到 setCurrentQuadrantScanView(game) 切到图2布局。
 */
  public void start(GameModel game) {
    // Initial window & menu
    if (!isDisplayable()) {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(windowSize);
      setLocationRelativeTo(null);
      setTitle("Star Trek");

      JMenuBar mb = new JMenuBar();
      fileMenu.removeAll();

      // --- Save ---
      JMenuItem save = new JMenuItem("Save");
      save.addActionListener(e -> {
        // Game.export() -> string; write to data/save.trek
        GameSaver saver = new GameSaver(game.export(), "data/save.trek");
        saver.save();
        JOptionPane.showMessageDialog(
            this,
            saver.success() ? "Saved to data/save.trek" : "Save failed. Check write permission.",
            "Save",
            saver.success() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
      });

      // --- Load ---
      JMenuItem load = new JMenuItem("Load");
      load.addActionListener(e -> {
        GameLoader loader = new GameLoader("data/save.trek");
        loader.load();

        if (!loader.success()) {
          JOptionPane.showMessageDialog(
              this,
              "Load failed. Make sure data/save.trek exists and is valid.",
              "Load",
              JOptionPane.ERROR_MESSAGE
          );
          return;
        }

        try {
          game.load(loader.enterprise(), loader.galaxy());
        } catch (IllegalStateException ex) {
          JOptionPane.showMessageDialog(
              this,
              "Load failed. The save file could not be parsed.",
              "Load",
              JOptionPane.ERROR_MESSAGE
          );
          return;
        }

        setCurrentQuadrantScanView(game);

        String ent = loader.enterpriseLine();
        java.util.List<String> galaxy = loader.galaxyLines();
        JOptionPane.showMessageDialog(
            this,
            "Loaded OK from data/save.trek.\n" + ent + "\nGalaxy lines loaded: " + galaxy.size(),
            "Load",
            JOptionPane.INFORMATION_MESSAGE
        );

      });

      fileMenu.add(save);
      fileMenu.add(load);
      mb.add(fileMenu);
      setJMenuBar(mb);
    }

    // —— Your original initial (pre-start) layout ——
    StartView startView = new StartView(game, this);
    StandardLayoutView layout = new StandardLayoutView("Star Trek");
    layout.addViewPanel(startView)
          .addViewPanel(new EnterpriseStatus(game))
          .addViewPanel(new QuadrantScan(game))
          .addViewPanel(new Options(game, this));

    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) setVisible(true);
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
    if (game.hasWon()) { setWinGameView(game); return; }
    if (game.hasLost()) { setLoseGameView(game); return; }

    StandardLayoutView layout = new StandardLayoutView("Quadrant Navigation");
    layout.addViewPanel(new QuadrantScan(game))              // TL
          .addViewPanel(new EnterpriseStatus(game))          // TR
          .addViewPanel(new Options(game, this))             // BL
          .addViewPanel(new QuadrantNavigation(game, this)); // BR

    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) setVisible(true);
  }


  public void setCurrentQuadrantScanView(GameModel game) {
    // —— 图2：进行中的布局 —— //
    StandardLayoutView layout = new StandardLayoutView("Star Trek |");
    layout.addViewPanel(new QuadrantScan(game));      // 左上：Map
    layout.addViewPanel(new EnterpriseStatus(game));  // 右上：Stat / Value
    layout.addViewPanel(new Options(game, this));     // 左下：按钮
    // 右下：留空，不再添加第 4 个面板

    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }


  public void setScanNearbyQuadrantView(GameModel game) {
      StandardLayoutView layout = new StandardLayoutView("Long Range Scan");
      // 左上：当前象限地图（短扫）
      layout.addViewPanel(new QuadrantScan(game));
      // 右上：Stat/Value
      layout.addViewPanel(new EnterpriseStatus(game));
      // 左下：操作按钮
      layout.addViewPanel(new Options(game, this));
      // 右下：长程扫描 3×3 结果
      layout.addViewPanel(new NearbyQuadrantScan(game));

      currentView = layout;
      setContentPane(layout);
      revalidate();
      repaint();
      if (!isVisible()) setVisible(true);
      pack(); // 让布局计算尺寸，避免空白
  }


  public void setWarpNavigationView(GameModel game) {
    // route to end screens if needed
    if (game.hasWon()) { setWinGameView(game); return; }
    if (game.hasLost()) { setLoseGameView(game); return; }

    // 2x2 layout: TL map, TR stats, BL options, BR warp controls
    StandardLayoutView layout = new StandardLayoutView("Warp Navigation");
    layout.addViewPanel(new QuadrantScan(game))          // TL: map (current quadrant)
          .addViewPanel(new EnterpriseStatus(game))      // TR: stats & values
          .addViewPanel(new Options(game, this))         // BL: options
          .addViewPanel(new WarpNavigation(game, this)); // BR: warp controls

    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) setVisible(true);
  }

  public void setPhaserAttackView(GameModel game) {
    if (game.hasWon()) { setWinGameView(game); return; }
    if (game.hasLost()) { setLoseGameView(game); return; }

    StandardLayoutView layout = new StandardLayoutView("Phaser Attack");
    layout.addViewPanel(new QuadrantScan(game))
        .addViewPanel(new EnterpriseStatus(game))
        .addViewPanel(new Options(game, this))
        .addViewPanel(new PhaserAttack(game, this));

    currentView = layout;
    setContentPane(layout);
    revalidate();
    repaint();
    if (!isVisible()) {
      setVisible(true);
    }
  }

  public void setTorpedoView(GameModel game) {
    if (game.hasWon()) { setWinGameView(game); return; }
    if (game.hasLost()) { setLoseGameView(game); return; }

    StandardLayoutView layout = new StandardLayoutView("Torpedoes");
    layout.addViewPanel(new QuadrantScan(game))
        .addViewPanel(new EnterpriseStatus(game))
        .addViewPanel(new Options(game, this))
        .addViewPanel(new Torpedo(game, this));

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
    if (game.hasWon()) { setWinGameView(game); return; }
    if (game.hasLost()) { setLoseGameView(game); return; }

    StandardLayoutView layout = new StandardLayoutView("Shields");
    layout.addViewPanel(new QuadrantScan(game))
        .addViewPanel(new EnterpriseStatus(game))
        .addViewPanel(new Options(game, this))
        .addViewPanel(new Shield(game, this));

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