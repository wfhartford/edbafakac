package ca.cutterslade.edbafakac.model;

import java.lang.reflect.InvocationTargetException;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public abstract class Value {

  private final Entry entry;

  private final ImmutableMap<String, String> pristine;

  private final boolean readOnly;

  protected static final Value getInstance(final Entry entry, final boolean readOnly) {
    try {
      final String valueClass = entry.getProperty(BaseField.VALUE_CLASS.getKey());
      Preconditions.checkArgument(null != valueClass);
      final Class<? extends Value> clazz = Class.forName(valueClass).asSubclass(Value.class);
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

  private EntryService getEntryService() {
    return entry.getEntryService();
  }

  public final String getKey() {
    return entry.getKey();
  }

  public final TypeValue getType(final boolean readOnly) {
    return (TypeValue) BaseField.VALUE_TYPE.getField().getValue(this, readOnly);
  }

  protected final String getProperty(final String propertyName) {
    String value = entry.getProperty(propertyName);
    if (isBaseValue()) {
      final BaseFieldResolver resolver = BaseField.getBaseField(propertyName).getResolver();
      if (null != resolver && resolver.isUnresolved(value)) {
        value = resolver.resolve(value);
        entry.setProperty(propertyName, value);
      }
    }
    return value;
  }

  private void checkWritable() {
    Preconditions.checkState(!readOnly, "Value is read only");
  }

  protected final void removeProperty(final String propertyName) {
    checkWritable();
    entry.removeProperty(propertyName);
  }

  protected final void setProperty(final String propertyName, final String value) {
    checkWritable();
    Preconditions.checkArgument(null != value);
    entry.setProperty(propertyName, value);
  }

  protected final void setPropertyIfMissing(final String propertyName, final String value) {
    checkWritable();
    if (!entry.hasProperty(propertyName)) {
      setProperty(propertyName, value);
    }
  }

  public final void save() {
    checkWritable();
    ImmutableMap<String, String> current;
    try {
      current = getEntryService().getEntry(getKey()).getProperties();
    }
    catch (final EntryNotFoundException e) {
      current = null;
    }
    onBeforeSave(pristine, current, entry.getProperties());
    getEntryService().saveEntry(entry);
  }

  protected void onBeforeSave(final ImmutableMap<String, String> previouslyRead,
      final ImmutableMap<String, String> justRead, final ImmutableMap<String, String> toWrite) {
  }

  public boolean isInstance(final TypeValue type) {
    return getType(true).getKey().equals(type.getKey());
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public Value asReadOnly() {
    return readOnly ? this : Values.getValue(getKey(), true);
  }

  public StringValue getName() {
    return (StringValue) BaseField.VALUE_NAME.getField().getValue(this, true);
  }

  public boolean isBaseValue() {
    return null != BaseField.getBaseField(getKey()) || null != BaseType.getBaseType(getKey());
  }

  public ListValue getFields(final boolean readOnly) {
    return (ListValue) BaseField.TYPE_FIELDS.getField().getValue(getType(readOnly), readOnly);
  }

  @Override
  public final int hashCode() {
    return getKey().hashCode();
  }

  @Override
  public final boolean equals(final Object obj) {
    return this == obj || (null != obj && getClass() == obj.getClass() && getKey().equals(((Value) obj).getKey()));
  }

  @Override
  public final String toString() {
    return getClass().getSimpleName() + " [key=" + getKey() + ']';
  }

}
