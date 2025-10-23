
import org.junit.Test;
    
import sttrswing.model.Entity;

import sttrswing.model.Enterprise;

import static org.junit.Assert.*;

public class EnterpriseTest {

  @Test
  public void defaultConstructorInitialisesStats() {
    Enterprise enterprise = new Enterprise();

    assertEquals(4, enterprise.getX());
    assertEquals(4, enterprise.getY());
    assertEquals(2500, enterprise.energy());
    assertEquals(500, enterprise.shields());
    assertEquals(10, enterprise.torpedoAmmo());
    assertTrue(enterprise.hasTorpedoAmmo());
    assertTrue(enterprise.isAlive());
  }

  @Test
  public void fireTorpedoConsumesAmmoAndReturnsProjectile() {
    Enterprise enterprise = new Enterprise(2, 3);

    Entity torpedo = enterprise.fireTorpedo();
    assertNotNull(torpedo);
    assertEquals(2, torpedo.getX());
    assertEquals(3, torpedo.getY());
    assertEquals(9, enterprise.torpedoAmmo());

    for (int i = 0; i < 9; i++) {
      enterprise.fireTorpedo();
    }
    assertEquals(0, enterprise.torpedoAmmo());
    assertFalse(enterprise.hasTorpedoAmmo());
    assertNull(enterprise.fireTorpedo());
  }

  @Test
  public void transferEnergyToShieldsMovesAvailableEnergy() {
    Enterprise enterprise = new Enterprise(0, 0, 50, 100, 0);

    int transferred = enterprise.transferEnergyToShields(200);

    assertEquals(50, transferred);
    assertEquals(0, enterprise.energy());
    assertEquals(150, enterprise.shields());
  }

  @Test
  public void hitSetsAliveFalseWhenShieldsDepleted() {
    Enterprise enterprise = new Enterprise(0, 0, 1000, 50, 0);

    enterprise.hit(40);
    assertTrue(enterprise.isAlive());
    assertEquals(10, enterprise.shields());

    enterprise.hit(10);
    assertFalse(enterprise.isAlive());
    assertEquals(0, enterprise.shields());

    Enterprise shieldless = new Enterprise(1, 1, 1000, 0, 0);
    shieldless.hit(1);
    assertFalse(shieldless.isAlive());
  }

  @Test
  public void symbolReflectsEnergyAndTorpedoLevels() {
    Enterprise fullyArmed = new Enterprise(0, 0, 1500, 100, 5);
    assertEquals("{Ë}", fullyArmed.symbol());

    Enterprise highEnergyNoAmmo = new Enterprise(0, 0, 1500, 100, 0);
    assertEquals("{E}", highEnergyNoAmmo.symbol());

    Enterprise lowEnergyArmed = new Enterprise(0, 0, 500, 100, 3);
    assertEquals("{ë}", lowEnergyArmed.symbol());

    Enterprise lowEnergyUnarmed = new Enterprise(0, 0, 500, 100, 0);
    assertEquals("{e}", lowEnergyUnarmed.symbol());
  }
}