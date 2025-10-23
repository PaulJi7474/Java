import org.junit.Test;
    
import org.junit.Before;

import static org.junit.Assert.*;

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
    assertEquals("[S]", b.symbol());
  }

  @Test
  public void attemptHeal_increases_enterprise_energy_or_shields_when_adjacent() {
    Enterprise ent = new Enterprise(2, 1);
    ent.hit(400);           
    int e0 = ent.energy();

    b.attemptHeal(ent);
    assertTrue(ent.energy() >= e0);
  }

  @Test
  public void starbase_can_be_destroyed_when_energy_depleted() {
    assertFalse(b.isMarkedForRemoval());
    b.hit(10000);             
    assertTrue(b.isMarkedForRemoval());
  }
}
