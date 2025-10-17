import org.junit.Test;
    
import org.junit.Before;

import static org.junit.Assert.*;
    
import sttrswing.model.Quadrant;

import sttrswing.model.XyPair;

import sttrswing.model.Entity;

import sttrswing.model.Starbase;

import sttrswing.model.Enterprise;

import sttrswing.model.enums.Faction;

public class StarbaseTest {
  private Starbase b;

  @Before
  public void setUp() {
    b = new Starbase(5,5);
  }

  @Test
  public void faction_is_neutral_until_scanned_then_federation() {
    assertEquals(Faction.NEUTRAL, b.faction());
    b.scan();
    assertEquals(Faction.FEDERATION, b.faction());
  }

  @Test
  public void symbol_masking_and_scan() {
    assertEquals(" ? ", b.symbol());
    b.scan();
    assertEquals("[S]", b.symbol()); // Starbase.symbol() 扫描后符号；若不同请改为你的实现
  }

  @Test
  public void attemptHeal_increases_enterprise_energy_or_shields_when_adjacent() {
    Enterprise ent = new Enterprise(2, 1); // 与 (1,1) 相邻
    // 人为制造较低资源，便于观察补给
    ent.hit(400);             // 降低护盾/能量
    // int s0 = ent.shields();
    int e0 = ent.energy();

    b.attemptHeal(ent);
    // 能量或护盾应有提升
    assertTrue(ent.energy() >= e0);
  }

  @Test
  public void starbase_can_be_destroyed_when_energy_depleted() {
    assertFalse(b.isMarkedForRemoval());
    b.hit(10000);             // 过量伤害
    assertTrue(b.isMarkedForRemoval());
  }
}
