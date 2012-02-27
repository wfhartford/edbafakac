package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanValueTest extends ValueServiceTest {

  @Test
  public void trueTest() {
    final BooleanValue booleanTrue = getValueService().getBooleanTrue();
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
    final BooleanValue booleanFalse = getValueService().getBooleanFalse();
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
    final BooleanValue booleanNull = (BooleanValue) getValueService().getBooleanType().getNewValue(null);
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
    getValueService().getBooleanTrue().setValue(Boolean.FALSE);
  }

  @Test(expected = IllegalStateException.class)
  public void setFalseTest() {
    getValueService().getBooleanFalse().setValue(Boolean.TRUE);
  }

  @Test
  public void setSaveTest() {
    BooleanValue value = (BooleanValue) getValueService().getBooleanType().getNewValue(null);
    value = (BooleanValue) getValueService().getValue(value.save().getKey(), RetrieveMode.READ_WRITE);
    assertNull(value.getValue());
    value.setValue(Boolean.FALSE);
    value = (BooleanValue) getValueService().getValue(value.save().getKey(), RetrieveMode.READ_WRITE);
    assertFalse(value.getValue());
    value.setValue(Boolean.TRUE);
    value = (BooleanValue) getValueService().getValue(value.save().getKey(), RetrieveMode.READ_WRITE);
    assertTrue(value.getValue());
    value.setValue(null);
    value = (BooleanValue) getValueService().getValue(value.save().getKey(), RetrieveMode.READ_WRITE);
    assertNull(value.getValue());
  }
}
