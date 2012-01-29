package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntegerValueTest {

  private static final long MAGIC_NUMBER = 42;

  @Test
  public void saveLoadTest() {
    IntegerValue fourtyTwo = IntegerValue.withValue(MAGIC_NUMBER).save();
    fourtyTwo = (IntegerValue) Values.getValue(fourtyTwo.getKey(), RetrieveMode.READ_ONLY);
    assertEquals(MAGIC_NUMBER, fourtyTwo.getValue().longValue());
  }
}
