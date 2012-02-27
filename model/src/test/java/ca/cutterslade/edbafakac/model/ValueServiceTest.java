package ca.cutterslade.edbafakac.model;

import org.junit.After;
import org.junit.Before;

public abstract class ValueServiceTest {

  private ValueService service;

  @Before
  public void initValueService() {
    service = ValueService.getInstance();
  }

  @After
  public void destroyValueService() {
    service = null;
  }

  protected ValueService getValueService() {
    return service;
  }
}
