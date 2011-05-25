package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public final class TypeValue extends Value {

  TypeValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public Class<? extends Value> getTypeClass() {
    try {
      return Class.forName(BaseField.TYPE_CLASS.getField().getRawValue(this)).asSubclass(Value.class);
    }
    catch (final ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public Value getNewValue(final StringValue name) {
    Preconditions.checkArgument(null != name || equals(BaseType.STRING.getType()) || equals(BaseType.LIST.getType()),
        "Only strings and lists can be created without a name");
    final Value newValue = Values.getNewValue(this);
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
    BaseField.VALUE_TYPE.getField().setRawValue(newValue, getKey());
    BaseField.VALUE_CLASS.getField().setRawValue(newValue, BaseField.TYPE_CLASS.getField().getRawValue(this));
    return newValue;
  }

  public FieldValue getNewField(final StringValue name) {
    final FieldValue value = (FieldValue) BaseType.FIELD.getType().getNewValue(name);
    BaseField.FIELD_TYPE.getField().setValue(value, this);
    BaseField.FIELD_KEY.getField().setRawValue(value, value.getKey());
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
