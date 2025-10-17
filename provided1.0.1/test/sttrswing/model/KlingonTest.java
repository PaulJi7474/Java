import org.junit.Test;
    
import org.junit.Before;

import static org.junit.Assert.*;
    
import sttrswing.model.Klingon;

import sttrswing.model.Enterprise;

import sttrswing.model.enums.Faction;

public class KlingonTest {
  private Klingon k;

  @Before
  public void setUp() {
    k = new Klingon(4, 4);
  }

  @Test
  public void faction_is_neutral_until_scanned_then_klingon() {
    assertEquals(Faction.NEUTRAL, k.faction()); // 未识别前
    k.scan();                                   // 识别后
    assertEquals(Faction.KLINGON, k.faction());
  }

  @Test
  public void symbol_masking_and_scan() {
    assertEquals(" ? ", k.symbol());
    k.scan();
    assertEquals("+K+", k.symbol()); // Klingon.symbol() 的可视符号；若不同请改为你的实现
  }

  @Test
  public void attack_returns_damage_and_affects_target() {
    Enterprise ent = new Enterprise(3, 5);
    int s0 = ent.shields();
    int e0 = ent.energy();

    int dmg = k.attack(ent);
    assertTrue(dmg >= 0);
    // 攻击应降低护盾或能量
    assertTrue(ent.shields() < s0 || ent.energy() < e0);
  }

  @Test
  public void hit_reduces_energy_and_marks_for_removal_at_zero() {
    k.hit(100);
    // 没有直接 getter 暴露能量值，但被打不应标记删除
    assertFalse(k.isMarkedForRemoval());

    k.hit(10000); // 过量伤害
    assertTrue(k.isMarkedForRemoval());
  }
}
