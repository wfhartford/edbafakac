package ca.cutterslade.edbafakac.gae.model;

import java.math.BigDecimal;

import com.google.appengine.api.datastore.Entity;

public class DecimalValue extends Value {

  private static final String VALUE_KEY = "a3340c4a-2e1a-4f3e-b9c0-32ca213f14d0";

  DecimalValue(final Entity entity) {
    super(entity);
  }

  public DecimalValue(final Value parent) {
    super(parent);
  }

  public void setValue(final BigDecimal value) {
    if (null == value) {
      removeProperty(VALUE_KEY);
    }
    else {
      setProperty(VALUE_KEY, value.toString());
    }
  }

  public BigDecimal getValue() {
    final String value = (String) getProperty(VALUE_KEY);
    return null == value ? null : new BigDecimal(value);
  }
}
