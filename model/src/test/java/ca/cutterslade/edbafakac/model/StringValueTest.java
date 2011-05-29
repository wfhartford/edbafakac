package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class StringValueTest {

  private static final String HELLO_WORLD = "Hello, World!";

  private static final String HELLO_WORLD_FR = "Bonjour tout le monde!";

  @Test
  public void twoLanguangeTest() {
    final StringValue value = StringValue.withValue(HELLO_WORLD, Locale.CANADA);
    assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ENGLISH));
    assertEquals(HELLO_WORLD, value.getBaseValue());
    assertEquals(HELLO_WORLD, value.getValue(Locale.US));
    assertEquals(HELLO_WORLD, value.getValue(Locale.TRADITIONAL_CHINESE));
    assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA_FRENCH));
    assertEquals(HELLO_WORLD, value.getValue(Locale.FRANCE));
    assertEquals(HELLO_WORLD, value.getValue(Locale.FRENCH));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ROOT));
    value.setValue(HELLO_WORLD_FR, Locale.CANADA_FRENCH);
    assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ENGLISH));
    assertEquals(HELLO_WORLD, value.getBaseValue());
    assertEquals(HELLO_WORLD, value.getValue(Locale.US));
    assertEquals(HELLO_WORLD, value.getValue(Locale.TRADITIONAL_CHINESE));
    assertEquals(HELLO_WORLD_FR, value.getValue(Locale.CANADA_FRENCH));
    assertEquals(HELLO_WORLD_FR, value.getValue(Locale.FRANCE));
    assertEquals(HELLO_WORLD_FR, value.getValue(Locale.FRENCH));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ROOT));
  }
}
