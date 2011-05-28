package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.collect.ImmutableSet;

public final class TypeValue extends Value<TypeValue> {

  private static final ImmutableSet<String> NAMELESS_TYPE_KEYS = ImmutableSet.of(
      BaseType.LIST.getKey(),
      BaseType.STRING.getKey(),
      BaseType.DATE.getKey(),
      BaseType.BOOLEAN.getKey(),
      BaseType.DECIMAL.getKey(),
      BaseType.INTEGER.getKey());

  TypeValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public Class<? extends Value<?>> getTypeClass() {
    try {
      @SuppressWarnings("unchecked")
      final Class<? extends Value<?>> clazz = (Class<? extends Value<?>>)
          Class.forName(BaseField.TYPE_CLASS.getField().getRawValue(this)).asSubclass(Value.class);
      return clazz;
    }
    catch (final ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public Value<?> getNewValue(final StringValue name) {
    if (NAMELESS_TYPE_KEYS.contains(getKey())) {
      if (null != name) {
        throw new IllegalArgumentException(getName(true).getBaseValue() + " may not have a name");
      }
    }
    else if (null == name) {
      throw new IllegalArgumentException(getName(true).getBaseValue() + " must have a name");
    }
    final Value<?> newValue = Values.getNewValue(this);
    if (equals(BaseType.STRING.getType())) {
      // A string value is its own name
      BaseField.VALUE_NAME.getField().setRawValue(newValue, newValue.getKey());
    }
    else if (null != name) {
      BaseField.VALUE_NAME.getField().setValue(newValue, name);
    }
    if (equals(BaseType.TYPE.getType())) {
      BaseField.TYPE_CLASS.getField().setRawValue(newValue, RecordValue.class.getName());
    }
    return newValue;
  }

  public FieldValue getNewField(final StringValue name) {
    final FieldValue value = (FieldValue) BaseType.FIELD.getType().getNewValue(name);
    BaseField.FIELD_TYPE.getField().setValue(value, this);
    return value;
  }

  public ListValue getTypeFields(final boolean readOnly) {
    return (ListValue) BaseField.TYPE_FIELDS.getField().getValue(this, readOnly);
  }

  public TypeValue addField(final StringValue name, final TypeValue type) {
    return addField(type.getNewField(name));
  }

  public TypeValue addField(final FieldValue field) {
    getTypeFields(false).add(field);
    return this;
  }

}
