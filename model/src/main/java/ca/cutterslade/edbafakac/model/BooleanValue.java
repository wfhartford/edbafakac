package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public final class BooleanValue extends Value<BooleanValue> {

  private static final String VALUE_KEY = "4ce36559-c679-42cd-8531-b7d89aa5213a";

  BooleanValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public static BooleanValue get(final boolean value) {
    return value ? getTrue() : getFalse();
  }

  public static BooleanValue getFalse() {
    return (BooleanValue) BaseValue.BOOLEAN_FALSE.getValue();
  }

  public static BooleanValue getTrue() {
    return (BooleanValue) BaseValue.BOOLEAN_TRUE.getValue();
  }

  public BooleanValue setValue(final Boolean value) {
    return null == value ? removeProperty(VALUE_KEY) : setProperty(VALUE_KEY, value.toString());
  }

  public Boolean getValue() {
    final String value = getProperty(VALUE_KEY);
    return null == value ? null : Boolean.valueOf(value);
  }

  public boolean getValue(final boolean dflt) {
    final Boolean value = getValue();
    return null == value ? dflt : value;
  }

  public boolean isTrue() {
    return getValue(false);
  }

  public boolean isNotTrue() {
    return !getValue(false);
  }

  public boolean isFalse() {
    return !getValue(true);
  }

  public boolean isNotFalse() {
    return getValue(false);
  }

  public boolean isNull() {
    return null == getValue();
  }
}
