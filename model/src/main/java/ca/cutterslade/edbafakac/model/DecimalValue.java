package ca.cutterslade.edbafakac.model;

import java.math.BigDecimal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.cutterslade.edbafakac.db.Entry;

public final class DecimalValue extends Value<DecimalValue> {

  private static final String VALUE_KEY = "a3340c4a-2e1a-4f3e-b9c0-32ca213f14d0";

  public static DecimalValue withValue(final long value) {
    return withValue(BigDecimal.valueOf(value));
  }

  public static DecimalValue withValue(final double value) {
    return withValue(BigDecimal.valueOf(value));
  }

  public static DecimalValue withValue(@Nullable final BigDecimal value) {
    return ((DecimalValue) Types.getDecimalType().getNewValue(null)).setValue(value);
  }

  DecimalValue(@Nonnull final Entry entry, @Nonnull final RetrieveMode retrieveMode) {
    super(entry, retrieveMode);
  }

  public DecimalValue setValue(@Nullable final BigDecimal value) {
    return null == value ? removeProperty(VALUE_KEY) : setProperty(VALUE_KEY, value.toString());
  }

  public BigDecimal getValue() {
    final String value = getProperty(VALUE_KEY);
    return null == value ? null : new BigDecimal(value);
  }
}
