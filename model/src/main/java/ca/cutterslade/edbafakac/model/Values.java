package ca.cutterslade.edbafakac.model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentMap;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.ServiceFactory;
import ca.cutterslade.utilities.PropertiesUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

final class Values {

  private static final ConcurrentMap<String, Value<?>> BASE_VALUES = new MapMaker().makeMap();

  private Values() {
    throw new UnsupportedOperationException();
  }

  private abstract static class ServiceHolder {

    private static final EntryService ENTRY_SERVICE = ServiceFactory.getInstance().getEntryService();

    public static EntryService getEntryService() {
      return ENTRY_SERVICE;
    }
  }

  private abstract static class Initializer {

    static {
      final ServiceLoader<InitialValueProvider> providerLoader = ServiceLoader.load(InitialValueProvider.class);
      final ListMultimap<Integer, InitialValueProvider> orderedProviders =
          Multimaps.newListMultimap(Maps.<Integer, Collection<InitialValueProvider>> newTreeMap(),
              new Supplier<List<InitialValueProvider>>() {

                @Override
                public List<InitialValueProvider> get() {
                  return Lists.newArrayList();
                }
              });
      for (final InitialValueProvider provider : providerLoader) {
        orderedProviders.put(provider.getPriority(), provider);
      }
      for (final InitialValueProvider provider : orderedProviders.values()) {
        for (final Value<?> value : provider.getValues()) {
          value.save();
        }
      }
    }

    public static void init() {
      // method does nothing, just serves as a way to access this class so that the static block will be invoked once
    }
  }

  private static EntryService getEntryService() {
    return ServiceHolder.getEntryService();
  }

  static Value<?> getNewValue(final TypeValue type) {
    Preconditions.checkArgument(null != type);
    final Entry entry = getEntryService().getNewEntry();
    entry.setProperty(BaseField.VALUE_TYPE.getKey(), type.getKey());
    entry.setProperty(BaseField.VALUE_CLASS.getKey(), BaseField.TYPE_CLASS.getValue().getRawValue(type));
    return Value.getInstance(entry, false);
  }

  static Value<?> getValue(final String key, final boolean readOnly) {
    Initializer.init();
    Value<?> value = BASE_VALUES.get(key);
    if (null == value) {
      value = Value.getInstance(getEntryService().getEntry(key), readOnly);
      if (value.isBaseValue()) {
        Preconditions.checkArgument(readOnly,
            "Cannot provide writable value of %s", value.getName(true).getBaseValue());
        final Value<?> oldValue = BASE_VALUES.putIfAbsent(key, value);
        value = null == oldValue ? value : oldValue;
      }
    }
    else if (!readOnly) {
      throw new IllegalArgumentException("Cannot provide writable value of " + value.getName(true).getBaseValue());
    }
    return value;
  }

  static Value<?> getValue(final String key, final String defaultResource) {
    Value<?> value;
    try {
      value = getValue(key, true);
    }
    catch (final EntryNotFoundException e) {
      final Entry entry = readBaseEntry(key, defaultResource);
      value = Value.getInstance(entry, true);
      final Value<?> oldValue = BASE_VALUES.putIfAbsent(key, value);
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
        final String value = null == BaseField.getResolver(fieldKey) ? ent.getValue() :
            BaseFieldResolver.UNRESOLVED_PREFIX + ent.getValue();
        entry.setProperty(fieldKey, value);
      }
      if (BaseType.TYPE.getKey().equals(entry.getProperty(BaseField.VALUE_TYPE.getKey())) &&
          !entry.hasProperty(BaseField.TYPE_FIELDS.getKey())) {
        // set the core set of fields for the base types that did not have them set in their properties file
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
