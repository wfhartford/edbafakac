package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public final class FieldValue extends Value<FieldValue> {

  FieldValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public TypeValue getFieldType(final boolean readOnly) {
    return (TypeValue) BaseField.FIELD_TYPE.getField().getValue(this, readOnly);
  }

  public Value<?> getValue(final Value<?> value, final boolean readOnly) {
    Preconditions.checkArgument(readOnly || !value.isReadOnly(),
        "Cannot provide writable field value from read only value");
    final String rawValue = getRawValue(value);
    return null == rawValue ? null : Values.getValue(rawValue, readOnly);
  }

  public FieldValue setValue(final Value<?> targetValue, final Value<?> fieldValue) {
    final TypeValue fieldType = save().getFieldType(true);
    if (!fieldValue.isInstance(fieldType)) {
      throw new IllegalArgumentException("Cannot set value of a " + fieldType.getName(true).getBaseValue() +
          " field to a " + fieldValue.getType(true).getName(true).getBaseValue());
    }
    return setRawValue(targetValue.save(), fieldValue.save().getKey());
  }

  String getRawValue(final Value<?> value) {
    return value.getProperty(getKey());
  }

  FieldValue setRawValue(final Value<?> targetValue, final String rawValue) {
    targetValue.save().setProperty(getKey(), rawValue);
    return this;
  }
}
