import org.junit.Test;
    
import sttrswing.model.Entity;

import static org.junit.Assert.*;

public class EntityTest {

  @Test
  public void symbol_isQuestionMark_beforeScan() {
    Entity e = new Entity(2, 3);
    e.setSymbol("-X-");
    assertEquals(" ? ", e.symbol());
  }

  @Test
  public void symbol_returnsAssigned_afterScan() {
    Entity e = new Entity(2, 3);
    e.setSymbol("-X-");
    e.scan();
    assertEquals("-X-", e.symbol());
  }

  @Test
  public void position_settersAndAdjust_work() {
    Entity e = new Entity(0, 0);
    e.setX(4);
    e.setY(5);
    assertEquals(4, e.getX());
    assertEquals(5, e.getY());
    e.adjustPosition(-1, +2);
    assertEquals(3, e.getX());
    assertEquals(7, e.getY());
  }

  @Test
  public void remove_setsMarkedForRemoval() {
    Entity e = new Entity(0, 0);
    assertFalse(e.isMarkedForRemoval());
    e.remove();
    assertTrue(e.isMarkedForRemoval());
  }

  @Test
  public void toString_containsKeyFields() {
    Entity e = new Entity(1, 2);
    String s = e.toString();
    assertTrue(s.contains("x:"));
    assertTrue(s.contains("y:"));
    assertTrue(s.contains("scanned:"));
    assertTrue(s.contains("markedForRemoval"));
  }
}
