package ca.cutterslade.edbafakac.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntryServiceFactory;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.utilities.PropertiesUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

public final class ValueService {

  private static final Supplier<List<InitialValueProvider>> ARRAY_LIST_SUPPLIER =
      new Supplier<List<InitialValueProvider>>() {

        @Override
        public List<InitialValueProvider> get() {
          return Lists.newArrayList();
        }
      };

  private final ConcurrentMap<String, Value<?>> baseValues = new MapMaker().makeMap();

  private final EntryService entryService;

  private ValueService(final EntryService entryService) {
    this.entryService = entryService;
    final ServiceLoader<InitialValueProvider> providerLoader = ServiceLoader.load(InitialValueProvider.class);
    final ListMultimap<Integer, InitialValueProvider> orderedProviders =
        Multimaps.newListMultimap(Maps.<Integer, Collection<InitialValueProvider>> newTreeMap(), ARRAY_LIST_SUPPLIER);
    for (final InitialValueProvider provider : providerLoader) {
      orderedProviders.put(provider.getPriority(), provider);
    }
    for (final InitialValueProvider provider : orderedProviders.values()) {
      for (final Value<?> value : provider.getValues(this)) {
        value.save();
      }
    }
  }

  public static ValueService getInstance() {
    return new ValueService(EntryServiceFactory.getInstance().getEntryService());
  }

  public static ValueService getInstance(final EntryService entryService) {
    return new ValueService(entryService);
  }

  EntryService getEntryService() {
    return entryService;
  }

  SearchService getSearchService() {
    return getEntryService().getSearchService();
  }

  Value<?> getNewValue(@Nonnull final TypeValue type) {
    final Entry entry = getEntryService().getNewEntry();
    entry.setProperty(BaseField.VALUE_TYPE.getKey(), type.getKey());
    entry.setProperty(BaseField.VALUE_CLASS.getKey(), BaseField.TYPE_CLASS.getValue(this).getRawValue(type));
    return Value.getInstance(this, entry, RetrieveMode.READ_WRITE);
  }

  Value<?> getValue(@Nonnull final String key, @Nonnull final RetrieveMode retrieveMode) {
    Value<?> value = baseValues.get(key);
    if (null == value) {
      value = Value.getInstance(this, getEntryService().getEntry(key), retrieveMode);
      if (value.isBaseValue()) {
        Preconditions.checkArgument(RetrieveMode.READ_ONLY == retrieveMode,
            "Cannot provide writable value of %s", value.getName(RetrieveMode.READ_ONLY).getBaseValue());
        final Value<?> oldValue = baseValues.putIfAbsent(key, value);
        value = null == oldValue ? value : oldValue;
      }
    }
    else if (RetrieveMode.READ_WRITE == retrieveMode) {
      throw new IllegalArgumentException("Cannot provide writable value of " +
          value.getName(RetrieveMode.READ_ONLY).getBaseValue());
    }
    return value;
  }

  Value<?> getValue(@Nonnull final String key, final String defaultResource) {
    Value<?> value;
    try {
      value = getValue(key, RetrieveMode.READ_ONLY);
    }
    catch (final EntryNotFoundException e) {
      final Entry entry = readBaseEntry(key, defaultResource);
      value = Value.getInstance(this, entry, RetrieveMode.READ_ONLY);
      final Value<?> oldValue = baseValues.putIfAbsent(key, value);
      if (null == oldValue) {
        getEntryService().saveEntry(entry);
      }
      else {
        value = oldValue;
      }
    }
    return value;
  }

  private Entry readBaseEntry(@Nonnull final String key, @Nonnull final String defaultResource) {
    try {
      final ImmutableMap<String, String> values = PropertiesUtils.loadProperties(ValueService.class, defaultResource);
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

  public TypeValue getBooleanType() {
    return BaseType.BOOLEAN.getValue(this);
  }

  public TypeValue getDateType() {
    return BaseType.DATE.getValue(this);
  }

  public TypeValue getDecimalType() {
    return BaseType.DECIMAL.getValue(this);
  }

  public TypeValue getIntegerType() {
    return BaseType.INTEGER.getValue(this);
  }

  public TypeValue getListType() {
    return BaseType.LIST.getValue(this);
  }

  public TypeValue getStringType() {
    return BaseType.STRING.getValue(this);
  }

  public TypeValue getFieldType() {
    return BaseType.FIELD.getValue(this);
  }

  public TypeValue getTypeType() {
    return BaseType.TYPE.getValue(this);
  }

  public TypeValue getType(final String key, final RetrieveMode retrieveMode) {
    return (TypeValue) getValue(key, retrieveMode);
  }

  public TypeValue getNewType(final StringValue name) {
    return (TypeValue) getTypeType().getNewValue(name);
  }

  public FieldValue getNameField() {
    return BaseField.VALUE_NAME.getValue(this);
  }

  public FieldValue getTypeField() {
    return BaseField.VALUE_TYPE.getValue(this);
  }

  public FieldValue getTypeFieldsField() {
    return BaseField.TYPE_FIELDS.getValue(this);
  }

  public FieldValue getFieldTypeField() {
    return BaseField.FIELD_TYPE.getValue(this);
  }

  public FieldValue getField(@Nonnull final String key, @Nonnull final RetrieveMode retrieveMode) {
    return (FieldValue) getValue(key, retrieveMode);
  }

  public RecordValue getRecord(@Nonnull final String key, @Nonnull final RetrieveMode retrieveMode) {
    return (RecordValue) getValue(key, retrieveMode);
  }

  public StringValue stringWithBase(@Nonnull final String baseValue, final boolean simple) {
    return ((StringValue) BaseType.STRING.getValue(this).getNewValue(null))
        .setSimple(simple)
        .setBaseValue(baseValue);
  }

  public StringValue stringWithValue(@Nonnull final String value, @Nonnull final Locale locale) {
    return ((StringValue) BaseType.STRING.getValue(this).getNewValue(null))
        .setValue(value, locale);
  }

  public ListValue listOfValues() {
    return (ListValue) BaseType.LIST.getValue(this).getNewValue(null);
  }

  public ListValue listOfType(@Nonnull final TypeValue type) {
    return listOfValues().setValueType(type.save());
  }

  public ListValue listOfValues(@Nonnull final Value<?>... values) {
    return listOfValues().addAll(values);
  }

  public ListValue listOfValues(@Nonnull final Iterable<? extends Value<?>> values) {
    return listOfValues().addAll(values);
  }

  public ListValue listOfValues(@Nonnull final TypeValue type, @Nonnull final Iterable<? extends Value<?>> values) {
    return listOfType(type).addAll(values);
  }

  public IntegerValue integerWithValue(final long value) {
    return integerWithValue(BigInteger.valueOf(value));
  }

  public IntegerValue integerWithValue(@Nullable final BigInteger value) {
    return ((IntegerValue) getIntegerType().getNewValue(null)).setValue(value);
  }

  public DecimalValue decimalWithValue(final long value) {
    return decimalWithValue(BigDecimal.valueOf(value));
  }

  public DecimalValue decimalWithValue(final double value) {
    return decimalWithValue(BigDecimal.valueOf(value));
  }

  public DecimalValue decimalWithValue(@Nullable final BigDecimal value) {
    return ((DecimalValue) getDecimalType().getNewValue(null)).setValue(value);
  }

  public DateValue dateWithTime(@Nullable final Calendar calendar) {
    return ((DateValue) getDateType().getNewValue(null)).setValue(calendar);
  }

  public DateValue dateWithTime(@Nullable final Date date, @Nullable final TimeZone zone) {
    return ((DateValue) getDateType().getNewValue(null)).setValue(date, zone);
  }

  public BooleanValue getBoolean(final boolean value) {
    return value ? getBooleanTrue() : getBooleanFalse();
  }

  public BooleanValue getBooleanFalse() {
    return (BooleanValue) BaseValue.BOOLEAN_FALSE.getValue(this);
  }

  public BooleanValue getBooleanTrue() {
    return (BooleanValue) BaseValue.BOOLEAN_TRUE.getValue(this);
  }

}
