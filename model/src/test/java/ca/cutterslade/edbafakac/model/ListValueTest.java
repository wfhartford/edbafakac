package ca.cutterslade.edbafakac.model;

import junit.framework.Assert;

import org.junit.Test;

public class ListValueTest {

  @Test
  public void basicEmptyListTest() {
    final ListValue list = ListValue.ofValues();
    Assert.assertNotNull(list);
    Assert.assertEquals(BaseType.LIST.getType(), list.getType(true));
    Assert.assertNull(list.getValueType(true));
    Assert.assertEquals(0, list.getSize());
  }

  @Test
  public void oneEntryTest() {
    final ListValue list = ListValue.ofValues(StringValue.withBase("only", true));
    Assert.assertEquals(1, list.getSize());
    Assert.assertEquals("only", ((StringValue) list.get(0)).getBaseValue());
    Assert.assertNull(list.getValueType(true));
  }
}
