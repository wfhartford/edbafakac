package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class StringValueTest extends ValueServiceTest {

  private static final String HELLO_WORLD = "Hello, World!";

  private static final String HELLO_WORLD_FR_CA = "Bonjour tout le monde!";

  private static final String HELLO_WORLD_FR_FR = "Bonjour tout le monde";

  @Test
  public void twoLanguangeTest() {
    StringValue value = getValueService().stringWithValue(HELLO_WORLD, Locale.CANADA);
    assertEquals(value, value.getName(RetrieveMode.READ_ONLY));
    testSingleLocale(value);
    value.setValue(HELLO_WORLD_FR_CA, Locale.CANADA_FRENCH);
    testDoubleLocale(value);
    value = (StringValue) getValueService().getValue(value.save().getKey(), RetrieveMode.READ_WRITE);
    assertEquals(value, value.getName(RetrieveMode.READ_ONLY));
    testDoubleLocale(value);
    value.setValue(HELLO_WORLD_FR_FR, Locale.FRANCE);
    testTripleLocale(value);
    value = (StringValue) getValueService().getValue(value.save().getKey(), RetrieveMode.READ_WRITE);
    assertEquals(value, value.getName(RetrieveMode.READ_ONLY));
    testTripleLocale(value);
  }

  private void testSingleLocale(final StringValue value) {
    assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ENGLISH));
    assertEquals(HELLO_WORLD, value.getBaseValue());
    assertEquals(HELLO_WORLD, value.getValue(Locale.US));
    assertEquals(HELLO_WORLD, value.getValue(Locale.TRADITIONAL_CHINESE));
    assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA_FRENCH));
    assertEquals(HELLO_WORLD, value.getValue(Locale.FRANCE));
    assertEquals(HELLO_WORLD, value.getValue(Locale.FRENCH));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ROOT));
  }

  private void testDoubleLocale(final StringValue value) {
    assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ENGLISH));
    assertEquals(HELLO_WORLD, value.getBaseValue());
    assertEquals(HELLO_WORLD, value.getValue(Locale.US));
    assertEquals(HELLO_WORLD, value.getValue(Locale.TRADITIONAL_CHINESE));
    assertEquals(HELLO_WORLD_FR_CA, value.getValue(Locale.CANADA_FRENCH));
    assertEquals(HELLO_WORLD_FR_CA, value.getValue(Locale.FRANCE));
    assertEquals(HELLO_WORLD_FR_CA, value.getValue(Locale.FRENCH));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ROOT));
  }

  private void testTripleLocale(final StringValue value) {
    assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ENGLISH));
    assertEquals(HELLO_WORLD, value.getBaseValue());
    assertEquals(HELLO_WORLD, value.getValue(Locale.US));
    assertEquals(HELLO_WORLD, value.getValue(Locale.TRADITIONAL_CHINESE));
    assertEquals(HELLO_WORLD_FR_CA, value.getValue(Locale.CANADA_FRENCH));
    assertEquals(HELLO_WORLD_FR_FR, value.getValue(Locale.FRANCE));
    assertEquals(HELLO_WORLD_FR_CA, value.getValue(Locale.FRENCH));
    assertEquals(HELLO_WORLD, value.getValue(Locale.ROOT));
  }
}
