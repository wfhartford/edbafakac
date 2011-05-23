package ca.cutterslade.edbafakac.model;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntryServiceFactory;
import ca.cutterslade.utilities.PropertiesUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;

public final class Values {

  private static final ConcurrentMap<String, Value> BASE_VALUES = new MapMaker().makeMap();

  private Values() {
    throw new UnsupportedOperationException();
  }

  private abstract static class ServiceHolder {

    private static final EntryService ENTRY_SERVICE = EntryServiceFactory.INSTANCE.getEntryService();

    public static EntryService getEntryService() {
      return ENTRY_SERVICE;
    }
  }

  private static final Object MUTEX = new Object();

  private static boolean initialized;

  private static void init() {
    synchronized (MUTEX) {
      if (!initialized) {
        initialized = true;
        for (final BaseType type : BaseType.values()) {
          type.getType();
        }
        for (final BaseField field : BaseField.values()) {
          field.getField();
        }
      }
    }
  }

  private static EntryService getEntryService() {
    return ServiceHolder.getEntryService();
  }

  private static Entry getNewEntry(final String typeKey, final String typeClass) {
    Preconditions.checkArgument(null != typeKey);
    Preconditions.checkArgument(null != typeClass, "No type class provided for new entry with type key %s", typeKey);
    final Entry entry = getEntryService().getNewEntry();
    entry.setProperty(BaseField.VALUE_TYPE.getKey(), typeKey);
    entry.setProperty(BaseField.VALUE_CLASS.getKey(), typeClass);
    return entry;
  }

  public static Value getNewValue(final TypeValue type) {
    final Entry entry = getNewEntry(type.getKey(), BaseField.TYPE_CLASS.getField().getRawValue(type));
    return Value.getInstance(entry, false);
  }

  public static Value getValue(final String key, final boolean readOnly) {
    init();
    Value value = BASE_VALUES.get(key);
    if (null == value) {
      value = Value.getInstance(getEntryService().getEntry(key), readOnly);
      if (value.isBaseValue()) {
        Preconditions.checkArgument(readOnly, "Cannot provide writable value of %s", value.getName().getBaseValue());
        final Value oldValue = BASE_VALUES.putIfAbsent(key, value);
        if (null != oldValue) {
          value = oldValue;
        }
      }
    }
    else {
      if (!readOnly) {
        throw new IllegalArgumentException("Cannot provide writable value of " + value.getName().getBaseValue());
      }
    }
    return value;
  }

  public static Value getValue(final String key, final String defaultResource) {
    Value value;
    try {
      value = getValue(key, true);
    }
    catch (final EntryNotFoundException e) {
      final Entry entry = readBaseEntry(key, defaultResource);
      value = Value.getInstance(entry, true);
      final Value oldValue = BASE_VALUES.putIfAbsent(key, value);
      if (null == oldValue) {
        getEntryService().saveEntry(entry);
      }
      else {
        value = oldValue;
      }
    }
    return value;
  }

  private static Entry readBaseEntry(final String key, final String defaultResource) {
    try {
      final ImmutableMap<String, String> values = PropertiesUtils.loadProperties(Values.class, defaultResource);
      final Entry entry = getEntryService().getNewEntry(key);
      for (final Map.Entry<String, String> ent : values.entrySet()) {
        final String fieldKey = ent.getKey();
        final String value = null == BaseField.getBaseField(fieldKey).getResolver() ? ent.getValue() :
            BaseFieldResolver.UNRESOLVED_PREFIX + ent.getValue();
        entry.setProperty(fieldKey, value);
      }
      if (BaseType.TYPE.getKey().equals(entry.getProperty(BaseField.VALUE_TYPE.getKey())) &&
          !entry.hasProperty(BaseField.TYPE_FIELDS.getKey())) {
        entry.setProperty(BaseField.TYPE_FIELDS.getKey(), BaseFieldResolver.UNRESOLVED_PREFIX +
            BaseField.VALUE_NAME.getKey() + ',' + BaseField.VALUE_TYPE.getKey() + ',' + BaseField.VALUE_CLASS.getKey());
      }
      return entry;
    }
    catch (final IOException e) {
      throw new IllegalStateException("Could not read base value resource " + defaultResource, e);
    }
  }
}
