package ca.cutterslade.edbafakac.model;

import java.math.BigDecimal;

import ca.cutterslade.edbafakac.db.Entry;

public final class DecimalValue extends Value {

  private static final String VALUE_KEY = "a3340c4a-2e1a-4f3e-b9c0-32ca213f14d0";

  DecimalValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
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
    final String value = getProperty(VALUE_KEY);
    return null == value ? null : new BigDecimal(value);
  }
}
