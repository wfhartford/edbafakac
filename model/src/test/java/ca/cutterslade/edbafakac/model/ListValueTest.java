package ca.cutterslade.edbafakac.model;

import junit.framework.Assert;

import org.junit.Test;

public class ListValueTest {

  @Test
  public void basicEmptyListTest() {
    final ListValue list = (ListValue) BaseType.LIST.getType().getNewValue(null);
    Assert.assertNotNull(list);
    Assert.assertEquals(BaseType.LIST.getType(), list.getType());
    Assert.assertNull(list.getValueType(true));
    Assert.assertEquals(0, list.getSize());
  }

}