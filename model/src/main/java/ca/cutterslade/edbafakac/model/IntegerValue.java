package ca.cutterslade.edbafakac.model;

import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.cutterslade.edbafakac.db.Entry;

public final class IntegerValue extends Value<IntegerValue> {

  private static final String VALUE_KEY = "61b1d125-2e3d-41e0-93c8-1df3a3a4ddd3";

  IntegerValue(@Nonnull final ValueService service, @Nonnull final Entry entry,
      @Nonnull final RetrieveMode retrieveMode) {
    super(service, entry, retrieveMode);
  }

  public IntegerValue setValue(@Nullable final BigInteger value) {
    return null == value ? removeProperty(VALUE_KEY) : setProperty(VALUE_KEY, value.toString());
  }

  public BigInteger getValue() {
    final String value = getProperty(VALUE_KEY);
    return null == value ? null : new BigInteger(value);
  }

}
