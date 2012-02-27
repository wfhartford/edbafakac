package ca.cutterslade.edbafakac.model;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.collect.ImmutableList;

public final class StringValue extends Value<StringValue> {

  private static final String BASE_VALUE_KEY = Locale.ROOT.toString();

  private static final String SIMPLE_KEY = "9492132b-233c-4e62-8155-61f9c7e23c3a";

  StringValue(@Nonnull final ValueService service, @Nonnull final Entry entry,
      @Nonnull final RetrieveMode retrieveMode) {
    super(service, entry, retrieveMode);
  }

  public StringValue setSimple(final boolean simple) {
    checkWritable();
    if (simple) {
      final boolean wasSimple = Boolean.parseBoolean(getProperty(SIMPLE_KEY));
      if (!wasSimple) {
        setProperty(SIMPLE_KEY, String.valueOf(true));
        for (final String key : getUnknownPropertyKeys(BASE_VALUE_KEY, SIMPLE_KEY)) {
          removeProperty(key);
        }
      }
    }
    else {
      removeProperty(SIMPLE_KEY);
    }
    return this;
  }

  public boolean isSimple() {
    return Boolean.parseBoolean(getProperty(SIMPLE_KEY));
  }

  public StringValue setValue(@Nullable final String value, @Nonnull final Locale locale) {
    if (isSimple()) {
      if (null != value) {
        setBaseValue(value);
      }
    }
    else if (null == value) {
      removeProperty(locale.toString());
    }
    else {
      boolean force = true;
      for (final String loc : getLocaleChain(locale)) {
        if (force) {
          setProperty(loc, value);
          force = false;
        }
        else {
          setPropertyIfMissing(loc, value);
        }
      }
    }
    return this;
  }

  public StringValue setBaseValue(@Nonnull final String value) {
    return setProperty(BASE_VALUE_KEY, value);
  }

  public String getValue(@Nonnull final Locale locale) {
    String value = null;
    if (isSimple()) {
      value = getBaseValue();
    }
    else {
      for (final String loc : getLocaleChain(locale)) {
        value = getProperty(loc);
        if (null != value) {
          break;
        }
      }
    }
    return value;
  }

  public String getBaseValue() {
    return getProperty(BASE_VALUE_KEY);
  }

  private static ImmutableList<String> getLocaleChain(@Nonnull final Locale locale) {
    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (Locale loc = locale; null != loc; loc = getParent(loc)) {
      builder.add(loc.toString());
    }
    builder.add(BASE_VALUE_KEY);
    return builder.build();
  }

  private static Locale getParent(@Nonnull final Locale locale) {
    final Locale parent;
    if (locale.getCountry().isEmpty()) {
      parent = null;
    }
    else if (locale.getVariant().isEmpty()) {
      parent = new Locale(locale.getLanguage());
    }
    else {
      parent = new Locale(locale.getLanguage(), locale.getCountry());
    }
    return parent;
  }

}
