package ca.cutterslade.edbafakac.model;

import java.math.BigInteger;

import ca.cutterslade.edbafakac.db.Entry;

public class IntegerValue extends Value {

  private static final String VALUE_KEY = "61b1d125-2e3d-41e0-93c8-1df3a3a4ddd3";

  public IntegerValue() {
    super();
  }

  IntegerValue(final Entry entry) {
    super(entry);
  }

  public void setValue(final BigInteger value) {
    if (null == value) {
      removeProperty(VALUE_KEY);
    }
    else {
      setProperty(VALUE_KEY, value.toString());
    }
  }

  public BigInteger getValue() {
    final String value = (String) getProperty(VALUE_KEY);
    return null == value ? null : new BigInteger(value);
  }

}
