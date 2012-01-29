package ca.cutterslade.edbafakac.model;

import java.lang.reflect.InvocationTargetException;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public abstract class Value<T extends Value<T>> {

  private final Entry entry;

  private final ImmutableMap<String, String> pristine;

  private final RetrieveMode retrieveMode;

  static final Value<?> getInstance(final Entry entry, final RetrieveMode retrieveMode) {
    try {
      final String valueClass = entry.getProperty(BaseField.VALUE_CLASS.getKey());
      Preconditions.checkArgument(null != valueClass);
      @SuppressWarnings("unchecked")
      final Class<? extends Value<?>> clazz =
          (Class<? extends Value<?>>) Class.forName(valueClass).asSubclass(Value.class);
      return clazz.getDeclaredConstructor(Entry.class, RetrieveMode.class).newInstance(entry, retrieveMode);
    }
    catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException
        | NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }

  Value(final Entry entry, final RetrieveMode retrieveMode) {
    Preconditions.checkArgument(null != entry);
    Preconditions.checkArgument(null != retrieveMode);
    this.entry = entry;
    this.retrieveMode = retrieveMode;
    final String valueClass = entry.getProperty(BaseField.VALUE_CLASS.getKey());
    if (null == valueClass) {
      entry.setProperty(BaseField.VALUE_CLASS.getKey(), getClass().getName());
    }
    else {
      Preconditions.checkArgument(getClass().getName().equals(valueClass));
    }
    pristine = entry.getProperties();
  }

  public final String getKey() {
    return entry.getKey();
  }

  final String getProperty(final String propertyName) {
    String value = entry.getProperty(propertyName);
    if (isBaseValue()) {
      final BaseFieldResolver resolver = BaseField.getResolver(propertyName);
      if (null != resolver && resolver.isUnresolved(value)) {
        value = resolver.resolve(value);
        entry.setProperty(propertyName, value);
      }
    }
    return value;
  }

  final Iterable<String> getUnknownPropertyKeys(final String... ignore) {
    final ImmutableSet.Builder<String> notIncluded = ImmutableSet.builder();
    if (null != ignore) {
      notIncluded.add(ignore);
    }
    notIncluded.addAll(BaseField.getBaseFieldKeys());
    return ImmutableList.copyOf(Sets.difference(entry.getPropertyKeys(), notIncluded.build()));
  }

  final T checkWritable() {
    Preconditions.checkState(!isReadOnly(), "Value is read only");
    return getThis();
  }

  final T removeProperty(final String propertyName) {
    checkWritable();
    entry.removeProperty(propertyName);
    return getThis();
  }

  final T setProperty(final String propertyName, final String value) {
    checkWritable();
    Preconditions.checkArgument(null != value);
    entry.setProperty(propertyName, value);
    return getThis();
  }

  final T setPropertyIfMissing(final String propertyName, final String value) {
    checkWritable();
    if (!entry.hasProperty(propertyName)) {
      setProperty(propertyName, value);
    }
    return getThis();
  }

  public final T save() {
    return save(false);
  }

  final T save(final boolean saveReadOnly) {
    if (!saveReadOnly && isReadOnly()) {
      Preconditions.checkState(!entry.isDirty(), "A dirty read-only values should be impossible");
    }
    else if (entry.isDirty()) {
      ImmutableMap<String, String> current;
      try {
        current = entry.getEntryService().getEntry(getKey()).getProperties();
      }
      catch (final EntryNotFoundException e) {
        current = null;
      }
      onBeforeSave(pristine, current, entry.getProperties());
      entry.getEntryService().saveEntry(entry);
    }
    return getThis();
  }

  void onBeforeSave(final ImmutableMap<String, String> previouslyRead,
      final ImmutableMap<String, String> justRead, final ImmutableMap<String, String> toWrite) {
  }

  public final boolean isInstance(final TypeValue type) {
    return getType(RetrieveMode.READ_ONLY).getKey().equals(type.getKey());
  }

  public final boolean isReadOnly() {
    return RetrieveMode.READ_ONLY == retrieveMode;
  }

  public final RetrieveMode getRetrieveMode() {
    return retrieveMode;
  }

  @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
  public final T asReadOnly() {
    @SuppressWarnings("unchecked")
    final T asReadOnly = (T) (isReadOnly() ? this : Values.getValue(getKey(), RetrieveMode.READ_ONLY));
    return asReadOnly;
  }

  public final StringValue getName(final RetrieveMode retrieveMode) {
    return (StringValue) getFieldValue(Fields.getNameField(), retrieveMode);
  }

  public final TypeValue getType(final RetrieveMode retrieveMode) {
    return (TypeValue) getFieldValue(Fields.getTypeField(), retrieveMode);
  }

  public final ListValue getFields(final RetrieveMode retrieveMode) {
    return (ListValue) getType(RetrieveMode.READ_ONLY).getFieldValue(Fields.getTypeFieldsField(), retrieveMode);
  }

  public final Value<?> getFieldValue(final FieldValue field, final RetrieveMode retrieveMode) {
    return field.getValue(this, retrieveMode);
  }

  public final T setFieldValue(final FieldValue field, final Value<?> value) {
    field.setValue(this, value.save()).save();
    return getThis();
  }

  final boolean isBaseValue() {
    return null != BaseField.getBaseField(getKey()) ||
        null != BaseType.getBaseType(getKey()) ||
        null != BaseValue.getBaseValue(getKey());
  }

  @SuppressWarnings("unchecked")
  protected final T getThis() {
    return (T) this;
  }

  public Long getWriteTime() {
    return entry.getWriteTime();
  }

  @Override
  public final int hashCode() {
    return getKey().hashCode();
  }

  @Override
  public final boolean equals(final Object obj) {
    return this == obj || (null != obj && getClass() == obj.getClass() && getKey().equals(((Value<?>) obj).getKey()));
  }

  @Override
  public final String toString() {
    return getClass().getSimpleName() + " [key=" + getKey() + ']';
  }

}
