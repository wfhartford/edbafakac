package ca.cutterslade.edbafakac.model;

import java.util.Locale;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public class StringValue extends Value {

  private static final String BASE_VALUE_KEY = "7090a2fc-9ea1-4f2e-9ad0-4c27e789f3db";

  protected StringValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public void setValue(final String value, final Locale locale) {
    if (null == value) {
      removeProperty(locale.toString());
    }
    else {
      setProperty(locale.toString(), value);
      for (Locale parent = getParent(locale); null != parent; parent = getParent(parent)) {
        setPropertyIfMissing(parent.toString(), value);
      }
      setPropertyIfMissing(BASE_VALUE_KEY, value);
    }
  }

  public void setBaseValue(final String value) {
    Preconditions.checkArgument(null != value, "Base value cannot be unset");
    setProperty(BASE_VALUE_KEY, value);
  }

  public String getValue(final Locale locale) {
    String value = null;
    for (Locale loc = locale; null != loc && null == value; loc = getParent(loc)) {
      value = getProperty(loc.toString());
    }
    if (null == value) {
      value = getBaseValue();
    }
    return value;
  }

  public String getBaseValue() {
    return getProperty(BASE_VALUE_KEY);
  }

  private Locale getParent(final Locale locale) {
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
