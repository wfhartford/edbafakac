package ca.cutterslade.edbafakac.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.cutterslade.edbafakac.db.Entry;

public final class BooleanValue extends Value<BooleanValue> {

  private static final String VALUE_KEY = "4ce36559-c679-42cd-8531-b7d89aa5213a";

  BooleanValue(@Nonnull final ValueService service, @Nonnull final Entry entry,
      @Nonnull final RetrieveMode retrieveMode) {
    super(service, entry, retrieveMode);
  }

  public BooleanValue setValue(@Nullable final Boolean value) {
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
    return getValue(true);
  }

  public boolean isNull() {
    return null == getValue();
  }
}
