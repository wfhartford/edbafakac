package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public class BooleanValue extends Value {

  private static final String VALUE_KEY = "4ce36559-c679-42cd-8531-b7d89aa5213a";

  public BooleanValue() {
    super();
  }

  BooleanValue(final Entry entry) {
    super(entry);
  }

  public void setValue(final Boolean value) {
    if (null == value) {
      removeProperty(VALUE_KEY);
    }
    else {
      setProperty(VALUE_KEY, value.toString());
    }
  }

  public Boolean getValue() {
    final String value = getProperty(VALUE_KEY);
    return null == value ? null : Boolean.valueOf(value);
  }
}
