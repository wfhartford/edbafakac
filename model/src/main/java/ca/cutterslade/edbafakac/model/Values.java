package ca.cutterslade.edbafakac.model;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.SearchService;
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

enum Values {
  ;

  private static final ConcurrentMap<String, Value<?>> BASE_VALUES = new MapMaker().makeMap();

  private enum ServiceHolder {
    ;

    private static final EntryService ENTRY_SERVICE = ServiceFactory.getInstance().getEntryService();

    public static EntryService getEntryService() {
      return ENTRY_SERVICE;
    }
  }

  private enum Initializer {
    ;

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

  static SearchService getSearchService() {
    return getEntryService().getSearchService();
  }

  static Value<?> getNewValue(@Nonnull final TypeValue type) {
    final Entry entry = getEntryService().getNewEntry();
    entry.setProperty(BaseField.VALUE_TYPE.getKey(), type.getKey());
    entry.setProperty(BaseField.VALUE_CLASS.getKey(), BaseField.TYPE_CLASS.getValue().getRawValue(type));
    return Value.getInstance(entry, RetrieveMode.READ_WRITE);
  }

  static Value<?> getValue(@Nonnull final String key, @Nonnull final RetrieveMode retrieveMode) {
    Initializer.init();
    Value<?> value = BASE_VALUES.get(key);
    if (null == value) {
      value = Value.getInstance(getEntryService().getEntry(key), retrieveMode);
      if (value.isBaseValue()) {
        Preconditions.checkArgument(RetrieveMode.READ_ONLY == retrieveMode,
            "Cannot provide writable value of %s", value.getName(RetrieveMode.READ_ONLY).getBaseValue());
        final Value<?> oldValue = BASE_VALUES.putIfAbsent(key, value);
        value = null == oldValue ? value : oldValue;
      }
    }
    else if (RetrieveMode.READ_WRITE == retrieveMode) {
      throw new IllegalArgumentException("Cannot provide writable value of " +
          value.getName(RetrieveMode.READ_ONLY).getBaseValue());
    }
    return value;
  }

  static Value<?> getValue(@Nonnull final String key, final String defaultResource) {
    Value<?> value;
    try {
      value = getValue(key, RetrieveMode.READ_ONLY);
    }
    catch (final EntryNotFoundException e) {
      final Entry entry = readBaseEntry(key, defaultResource);
      value = Value.getInstance(entry, RetrieveMode.READ_ONLY);
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

  private static Entry readBaseEntry(@Nonnull final String key, @Nonnull final String defaultResource) {
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
