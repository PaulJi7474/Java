// src/sttrswing/view/panels/Options.java
package sttrswing.view.panels;

import sttrswing.controller.GameController;
import sttrswing.model.interfaces.GameModel;
import sttrswing.view.View;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Options panel: right-下象限的一列操作按钮。
 * 本实现新增 Short/Long Range Scan，并去掉旧的 "Scan: Quadrant"。
 */
public class Options extends View {

  public Options(GameModel game, GameController controller) {
    super("Options");
    // 垂直 7 行按钮，留一点行间距更接近示例图
    this.setLayout(new GridLayout(7, 1, 0, 8));
    buildOptionButtons(game, controller);
  }

  /** 构建所有操作按钮并加入面板。 */
  public void buildOptionButtons(GameModel game, GameController controller) {
    // 1) Quadrant Navigation
    this.add(buildButton("Quadrant Navigation", e -> {
      controller.setQuadrantNavigationView(game);
    }));

    // 2) Warp Navigation
    this.add(buildButton("Warp Navigation", e -> {
      controller.setWarpNavigationView(game);
    }));

    // 3) Phasers
    this.add(phasersButton(game, controller));

    // 4) Torpedoes
    this.add(torpedoButton(game, controller));

    // 5) Shields
    this.add(shieldsButton(game, controller));

    // 6) Short Range Scan —— 当前象限（8×8 sector）
    this.add(scanInQuadrantButton(game, controller));

    // 7) Long Range Scan —— 附近 3×3 象限统计
    this.add(scanNearbyQuadrantsButton(game, controller));
  }

  /** Phasers 按钮：若能量不足则禁用（Javadoc 要求）。 */
  public JButton phasersButton(GameModel game, GameController controller) {
    ActionListener listener = e -> controller.setPhaserAttackView(game);
    JButton btn = buildButton("Phasers", listener);
    // 无参 hasSpareEnergy()：是否有足够能量做需要能量的动作
    try {
      btn.setEnabled(game.hasSpareEnergy());
    } catch (Throwable ignore) {
      // 某些实现只提供 hasSpareEnergy(int)；那就保持可点，由界面内再校验
    }
    return btn;
  }

  /** Torpedoes 按钮：若弹药为 0 则禁用（Javadoc 语义）。 */
  public JButton torpedoButton(GameModel game, GameController controller) {
    ActionListener listener = e -> controller.setTorpedoView(game);
    JButton btn = buildButton("10 x Torpedoes", listener);
    try {
      // GameModel / Game 都有 hasSpareTorpedoes()
      btn.setEnabled(game.hasSpareTorpedoes());
    } catch (Throwable ignore) {
      // 若接口实现不同，保持可用
    }
    return btn;
  }

  /** Shields 按钮：进入护盾设置界面。 */
  public JButton shieldsButton(GameModel game, GameController controller) {
    return buildButton("Shields", e -> controller.setShieldsView(game));
  }

  /**
   * Short Range Scan（短程扫描）：
   * 1) 扫描当前象限 game.scanQuadrant();
   * 2) 结算回合 game.turn();
   * 3) 切到当前象限扫描视图 controller.setCurrentQuadrantScanView(game);
   */
  public JButton scanInQuadrantButton(GameModel game, GameController controller) {
    return buildButton("Short Range Scan", e -> {
      game.scanQuadrant();     // 记录报告、标记已扫描
      game.turn();             // 扫描算一回合
      controller.setCurrentQuadrantScanView(game);
    });
  }

  /**
   * Long Range Scan（长程扫描）：
   * 1) 直接结算回合（长扫也消耗回合）
   * 2) 切到周边象限扫描视图 controller.setScanNearbyQuadrantView(game);
   *
   * 说明：长扫显示 3×3 邻近象限的 Klingons/Starbases/Stars 统计，
   * 数据来源于 Galaxy/Quadrant；不需要调用 game.scanQuadrant()。
   */
  public JButton scanNearbyQuadrantsButton(GameModel game, GameController controller) {
    return buildButton("Long Range Scan", e -> {
      game.turn();
      controller.setScanNearbyQuadrantView(game);
    });
  }
}
