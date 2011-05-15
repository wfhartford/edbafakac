package ca.cutterslade.edbafakac.gae.model;

import com.google.appengine.api.datastore.Entity;

public class BooleanValue extends Value {

  private static final String VALUE_KEY = "4ce36559-c679-42cd-8531-b7d89aa5213a";

  BooleanValue(final Entity entity) {
    super(entity);
  }

  public BooleanValue(final Value parent) {
    super(parent);
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
    final String value = (String) getProperty(VALUE_KEY);
    return null == value ? null : Boolean.valueOf(value);
  }
}
