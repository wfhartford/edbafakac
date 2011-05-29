package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

// CSOFF: MagicNumber
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ListValueTest {

  @Test
  public void basicEmptyListTest() {
    final ListValue list = ListValue.ofValues();
    assertNotNull(list);
    assertEquals(BaseType.LIST.getType(), list.getType(true));
    assertNull(list.getValueType(true));
    assertEquals(0, list.getSize());
  }

  @Test
  public void oneEntryTest() {
    final ListValue list = ListValue.ofValues(value("only"));
    assertEquals(1, list.getSize());
    assertEquals("only", value(list.get(0)));
    assertNull(list.getValueType(true));
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
    ListValue.ofType(Types.getIntegerType()).add(value("only"));
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

  @Test(expected = IllegalArgumentException.class)
  public void addNullTest() {
    ListValue.ofValues().add(null);
  }

  @Test
  public void indexOfTest() {
    final StringValue one = value("one");
    final StringValue two = value("two");
    final StringValue three = value("three");
    final ListValue list = ListValue.ofValues(Types.getStringType(), Arrays.asList(one, two, three));
    assertEquals(3, list.getSize());
    assertTrue(list.contains(one));
    assertEquals(0, list.indexOf(one));
    assertTrue(list.contains(two));
    assertEquals(1, list.indexOf(two));
    assertTrue(list.contains(three));
    assertEquals(2, list.indexOf(three));
  }

  private StringValue value(final String string) {
    return StringValue.withBase(string, true);
  }

  private String value(final Value<?> value) {
    return ((StringValue) value).getBaseValue();
  }
}
