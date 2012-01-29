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

  TypeValue(final Entry entry, final RetrieveMode retrieveMode) {
    super(entry, retrieveMode);
  }

  public Class<? extends Value<?>> getTypeClass() {
    try {
      @SuppressWarnings("unchecked")
      final Class<? extends Value<?>> clazz = (Class<? extends Value<?>>)
          Class.forName(BaseField.TYPE_CLASS.getValue().getRawValue(this)).asSubclass(Value.class);
      return clazz;
    }
    catch (final ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public Value<?> getNewValue(final StringValue name) {
    if (NAMELESS_TYPE_KEYS.contains(getKey())) {
      if (null != name) {
        throw new IllegalArgumentException(getName(RetrieveMode.READ_ONLY).getBaseValue() + " may not have a name");
      }
    }
    else if (null == name) {
      throw new IllegalArgumentException(getName(RetrieveMode.READ_ONLY).getBaseValue() + " must have a name");
    }
    final Value<?> newValue = Values.getNewValue(this);
    if (equals(BaseType.STRING.getValue())) {
      // A string value is its own name
      BaseField.VALUE_NAME.getValue().setRawValue(newValue, newValue.getKey());
    }
    else if (null != name) {
      BaseField.VALUE_NAME.getValue().setValue(newValue, name);
    }
    if (equals(BaseType.TYPE.getValue())) {
      BaseField.TYPE_CLASS.getValue().setRawValue(newValue, RecordValue.class.getName());
    }
    return newValue;
  }

  public FieldValue getNewField(final StringValue name) {
    final FieldValue value = (FieldValue) BaseType.FIELD.getValue().getNewValue(name);
    BaseField.FIELD_TYPE.getValue().setValue(value, this);
    return value;
  }

  public ListValue getTypeFields(final RetrieveMode retrieveMode) {
    return (ListValue) BaseField.TYPE_FIELDS.getValue().getValue(this, retrieveMode);
  }

  public TypeValue addField(final StringValue name, final TypeValue type) {
    return addField(type.getNewField(name));
  }

  public TypeValue addField(final FieldValue field) {
    getTypeFields(RetrieveMode.READ_WRITE).add(field);
    return this;
  }

}
