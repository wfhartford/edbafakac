package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanValueTest {

  @Test
  public void trueTest() {
    final BooleanValue booleanTrue = BooleanValue.getTrue();
    assertTrue(booleanTrue.getValue());
    assertTrue(booleanTrue.getValue(true));
    assertTrue(booleanTrue.getValue(false));
    assertTrue(booleanTrue.isTrue());
    assertFalse(booleanTrue.isNotTrue());
    assertFalse(booleanTrue.isFalse());
    assertTrue(booleanTrue.isNotFalse());
    assertFalse(booleanTrue.isNull());
  }

  @Test
  public void falseTest() {
    final BooleanValue booleanFalse = BooleanValue.getFalse();
    assertFalse(booleanFalse.getValue());
    assertFalse(booleanFalse.getValue(true));
    assertFalse(booleanFalse.getValue(false));
    assertFalse(booleanFalse.isTrue());
    assertTrue(booleanFalse.isNotTrue());
    assertTrue(booleanFalse.isFalse());
    assertFalse(booleanFalse.isNotFalse());
    assertFalse(booleanFalse.isNull());
  }

  @Test
  public void nullTest() {
    final BooleanValue booleanNull = (BooleanValue) Types.getBooleanType().getNewValue(null);
    assertNull(booleanNull.getValue());
    assertTrue(booleanNull.getValue(true));
    assertFalse(booleanNull.getValue(false));
    assertFalse(booleanNull.isTrue());
    assertTrue(booleanNull.isNotTrue());
    assertFalse(booleanNull.isFalse());
    assertTrue(booleanNull.isNotFalse());
    assertTrue(booleanNull.isNull());
  }

  @Test(expected = IllegalStateException.class)
  public void setTrueTest() {
    BooleanValue.getTrue().setValue(Boolean.FALSE);
  }

  @Test(expected = IllegalStateException.class)
  public void setFalseTest() {
    BooleanValue.getFalse().setValue(Boolean.TRUE);
  }
}
