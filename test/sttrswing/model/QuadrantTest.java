import org.junit.Test;
    
import org.junit.Before;

import static org.junit.Assert.*;
    
import sttrswing.model.Quadrant;

import sttrswing.model.XyPair;

import sttrswing.model.Entity;

import sttrswing.model.Starbase;

import sttrswing.model.Enterprise;

public class QuadrantTest {
  private Quadrant q;

  @Before
  public void setUp() {
    // 使用你类中提供的“确定数量”的构造：Quadrant(galaxyX, galaxyY, starbases, klingons, stars)
    q = new Quadrant(2, 3, /*starbases*/2, /*klingons*/3, /*stars*/4);
  }

  @Test
  public void galaxy_position_exposed_via_getX_getY() {
    assertEquals(2, q.getX());
    assertEquals(3, q.getY());
  }

  @Test
  public void counts_match_construction_and_lists_exposed() {
    assertEquals(2, q.starbaseCount());
    assertEquals(3, q.klingonCount());
    assertEquals(4, q.starCount());

    assertEquals(2, q.starbases().size());
    assertEquals(3, q.klingons().size());
    assertEquals(4, q.stars().size());
  }

  @Test
  public void getRandomEmptySector_is_empty_and_inside_bounds() {
    XyPair p = q.getRandomEmptySector();
    assertNotNull(p);

    Entity at = q.getEntityAt(p.getX(), p.getY());
    assertNull(at);
  }

  @Test
  public void getSymbolAt_matches_entity_symbol_after_scan() {
    // 取一个已知存在的实体（例如第一座星基地），扫描后检查符号
    Starbase base = q.starbases().get(0);
    base.scan();
    String s = q.getSymbolAt(base.getX(), base.getY());
    assertEquals(base.symbol(), s);
  }

  @Test
  public void cleanup_removes_marked_entities() {
    int k0 = q.klingonCount();
    int b0 = q.starbaseCount();

    // 标记一个克林贡与一个星基地删除
    q.klingons().get(0).remove();
    q.starbases().get(0).remove();

    q.cleanup();

    assertEquals(k0 - 1, q.klingonCount());
    assertEquals(b0 - 1, q.starbaseCount());
  }
}
