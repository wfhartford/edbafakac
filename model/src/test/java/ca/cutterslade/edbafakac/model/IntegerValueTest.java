package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntegerValueTest extends ValueServiceTest {

  private static final long MAGIC_NUMBER = 42;

  @Test
  public void saveLoadTest() {
    IntegerValue fourtyTwo = getValueService().integerWithValue(MAGIC_NUMBER).save();
    fourtyTwo = (IntegerValue) getValueService().getValue(fourtyTwo.getKey(), RetrieveMode.READ_ONLY);
    assertEquals(MAGIC_NUMBER, fourtyTwo.getValue().longValue());
  }
}
