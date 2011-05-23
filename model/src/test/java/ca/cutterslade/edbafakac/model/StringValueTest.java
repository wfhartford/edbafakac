package ca.cutterslade.edbafakac.model;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class StringValueTest {

  private static final String HELLO_WORLD = "Hello, World!";

  private static final String HELLO_WORLD_FR = "Bonjour tout le monde!";

  @Test
  public void simpleHelloTest() {
    final StringValue value = (StringValue) BaseType.STRING.getType().getNewValue();
    value.setValue(HELLO_WORLD, Locale.CANADA);
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA));
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.ENGLISH));
    Assert.assertEquals(HELLO_WORLD, value.getBaseValue());
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.US));
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.TRADITIONAL_CHINESE));
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA_FRENCH));
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.FRANCE));
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.FRENCH));
    value.setValue(HELLO_WORLD_FR, Locale.CANADA_FRENCH);
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.CANADA));
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.ENGLISH));
    Assert.assertEquals(HELLO_WORLD, value.getBaseValue());
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.US));
    Assert.assertEquals(HELLO_WORLD, value.getValue(Locale.TRADITIONAL_CHINESE));
    Assert.assertEquals(HELLO_WORLD_FR, value.getValue(Locale.CANADA_FRENCH));
    Assert.assertEquals(HELLO_WORLD_FR, value.getValue(Locale.FRANCE));
    Assert.assertEquals(HELLO_WORLD_FR, value.getValue(Locale.FRENCH));
  }
}
