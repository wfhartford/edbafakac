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

  private final boolean readOnly;

  static final Value<?> getInstance(final Entry entry, final boolean readOnly) {
    try {
      final String valueClass = entry.getProperty(BaseField.VALUE_CLASS.getKey());
      Preconditions.checkArgument(null != valueClass);
      @SuppressWarnings("unchecked")
      final Class<? extends Value<?>> clazz =
          (Class<? extends Value<?>>) Class.forName(valueClass).asSubclass(Value.class);
      return clazz.getDeclaredConstructor(Entry.class, boolean.class).newInstance(entry, readOnly);
    }
    catch (final ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
    catch (final InstantiationException e) {
      throw new IllegalArgumentException(e);
    }
    catch (final IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
    catch (final InvocationTargetException e) {
      throw new IllegalArgumentException(e);
    }
    catch (final NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }

  Value(final Entry entry, final boolean readOnly) {
    Preconditions.checkArgument(null != entry);
    this.entry = entry;
    this.readOnly = readOnly;
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

  T checkWritable() {
    Preconditions.checkState(!readOnly, "Value is read only");
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
    checkWritable();
    ImmutableMap<String, String> current;
    try {
      current = entry.getEntryService().getEntry(getKey()).getProperties();
    }
    catch (final EntryNotFoundException e) {
      current = null;
    }
    onBeforeSave(pristine, current, entry.getProperties());
    entry.getEntryService().saveEntry(entry);
    return getThis();
  }

  void onBeforeSave(final ImmutableMap<String, String> previouslyRead,
      final ImmutableMap<String, String> justRead, final ImmutableMap<String, String> toWrite) {
  }

  public final boolean isInstance(final TypeValue type) {
    return getType(true).getKey().equals(type.getKey());
  }

  public final boolean isReadOnly() {
    return readOnly;
  }

  @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn")
  public final T asReadOnly() {
    @SuppressWarnings("unchecked")
    final T asReadOnly = (T) (readOnly ? this : Values.getValue(getKey(), true));
    return asReadOnly;
  }

  public final StringValue getName(final boolean readOnly) {
    return (StringValue) BaseField.VALUE_NAME.getField().getValue(this, readOnly);
  }

  public final TypeValue getType(final boolean readOnly) {
    return (TypeValue) BaseField.VALUE_TYPE.getField().getValue(this, readOnly);
  }

  public final ListValue getFields(final boolean readOnly) {
    return (ListValue) BaseField.TYPE_FIELDS.getField().getValue(getType(true), readOnly);
  }

  public final Value<?> getFieldValue(final FieldValue field, final boolean readOnly) {
    return field.getValue(this, readOnly);
  }

  public final T setFieldValue(final FieldValue field, final Value<?> value) {
    field.setValue(this, value);
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
