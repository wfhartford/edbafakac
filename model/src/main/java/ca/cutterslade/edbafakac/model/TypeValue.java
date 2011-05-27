package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public final class TypeValue extends Value<TypeValue> {

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
    if (equals(BaseType.STRING.getType()) || equals(BaseType.LIST.getType())) {
      Preconditions.checkArgument(null == name, "Strings and lists must not have a name");
    }
    else {
      Preconditions.checkArgument(null != name, "Only strings and lists can be created without a name");
    }
    final Value<?> newValue = Values.getNewValue(this);
    if (equals(BaseType.STRING.getType())) {
      // A string value is its own name
      BaseField.VALUE_NAME.getField().setValue(newValue, newValue);
    }
    else if (!equals(BaseType.LIST.getType())) {
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
    getTypeFields(false).add(type.getNewField(name));
    return this;
  }

}
