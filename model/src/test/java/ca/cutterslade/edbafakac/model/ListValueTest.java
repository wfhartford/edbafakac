package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

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
    final ListValue list = ListValue.ofValues(value("only"));
    Assert.assertEquals(1, list.getSize());
    Assert.assertEquals("only", value(list.get(0)));
    Assert.assertNull(list.getValueType(true));
  }

  @Test
  public void twoEntriesTest() {
    final ListValue list = ListValue.ofValues(value("one"), value("two"));
    assertEquals(2, list.getSize());
    assertEquals("one", value(list.get(0)));
    assertEquals("two", value(list.get(1)));
    assertNull(list.getValueType(true));
  }

  @Test
  public void typeRestrictedTest() {
    final ListValue list = ListValue.ofType(Types.getStringType());
    list.add(value("only"));
    assertEquals(1, list.getSize());
    assertEquals("only", value(list.get(0)));
    assertEquals(Types.getStringType(), list.getValueType(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void typeRestrictedWrongTypeTest() {
    final ListValue list = ListValue.ofType(Types.getIntegerType());
    list.add(value("only"));
  }

  @Test
  public void removeTest() {
    final ListValue list =
        ListValue.ofValues(Types.getStringType(), Arrays.asList(value("one"), value("oops"), value("two")));
    assertEquals(3, list.getSize());
    assertEquals("oops", value(list.get(1)));
    list.remove(1);
    assertEquals(2, list.getSize());
    assertEquals("one", value(list.get(0)));
    assertEquals("two", value(list.get(1)));
  }

  @Test
  public void insertTest() {
    final ListValue list = ListValue.ofType(Types.getStringType()).addAll(value("one"), value("three"));
    assertEquals(2, list.getSize());
    list.insert(1, value("two"));
    assertEquals(3, list.getSize());
    assertEquals("one", value(list.get(0)));
    assertEquals("two", value(list.get(1)));
    assertEquals("three", value(list.get(2)));
  }

  @Test
  public void setTest() {
    final ListValue list = ListValue.ofType(Types.getStringType()).addAll(value("one"), value("tow"));
    assertEquals(2, list.getSize());
    assertEquals("tow", value(list.get(1)));
    list.set(1, value("two"));
    assertEquals(2, list.getSize());
    assertEquals("one", value(list.get(0)));
    assertEquals("two", value(list.get(1)));
  }

  private StringValue value(final String string) {
    return StringValue.withBase(string, true);
  }

  private String value(final Value<?> value) {
    return ((StringValue) value).getBaseValue();
  }
}
