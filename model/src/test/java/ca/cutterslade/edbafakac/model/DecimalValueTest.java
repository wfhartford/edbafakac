package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DecimalValueTest extends ValueServiceTest {

  @Test
  public void saveLoadTest() {
    DecimalValue onePointOne = getValueService().decimalWithValue(1.1).save();
    onePointOne = (DecimalValue) getValueService().getValue(onePointOne.getKey(), RetrieveMode.READ_ONLY);
    assertEquals(1.1, onePointOne.getValue().doubleValue(), 0);
  }
}
