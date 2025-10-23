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
    assertEquals(Faction.NEUTRAL, k.faction()); 
    k.scan();                                   
    assertEquals(Faction.KLINGON, k.faction());
  }

  @Test
  public void symbol_masking_and_scan() {
    assertEquals(" ? ", k.symbol());
    k.scan();
    assertEquals("+K+", k.symbol());
  }

  @Test
  public void attack_returns_damage_and_affects_target() {
    Enterprise ent = new Enterprise(3, 5);
    int s0 = ent.shields();
    int e0 = ent.energy();

    int dmg = k.attack(ent);
    assertTrue(dmg >= 0);
    assertTrue(ent.shields() < s0 || ent.energy() < e0);
  }

  @Test
  public void hit_reduces_energy_and_marks_for_removal_at_zero() {
    k.hit(40);
    assertFalse(k.isMarkedForRemoval());

    k.hit(10000);
    assertTrue(k.isMarkedForRemoval());
  }
}
