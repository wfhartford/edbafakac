package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public final class FieldValue extends Value<FieldValue> {

  FieldValue(final Entry entry, final RetrieveMode retrieveMode) {
    super(entry, retrieveMode);
  }

  public TypeValue getFieldType(final RetrieveMode retrieveMode) {
    return (TypeValue) BaseField.FIELD_TYPE.getValue().getValue(this, retrieveMode);
  }

  public Value<?> getValue(final Value<?> value, final RetrieveMode retrieveMode) {
    Preconditions.checkArgument(RetrieveMode.READ_ONLY == retrieveMode || !value.isReadOnly(),
        "Cannot provide writable field value from read only value");
    checkValueFields(value);
    final String rawValue = getRawValue(value);
    return null == rawValue ? null : Values.getValue(rawValue, retrieveMode);
  }

  private void checkValueFields(final Value<?> value) {
    // Check that the value could possibly contain the field
    // we have to omit a few things from this check because the could cause a stack overflow

    // Everything has a VALUE_TYPE field
    if (!equals(BaseField.VALUE_TYPE.getValue()) &&
        // Types all have the fields field
        !(equals(BaseField.TYPE_FIELDS.getValue()) && value.isInstance(BaseType.TYPE.getValue())) &&
        // Check that the value's list of fields contains this field
        !value.getFields(RetrieveMode.READ_ONLY).contains(this)) {
      throw new IllegalArgumentException("Value does not contain specified field");
    }
  }

  public FieldValue setValue(final Value<?> targetValue, final Value<?> fieldValue) {
    checkValueFields(targetValue);
    final TypeValue fieldType = save().getFieldType(RetrieveMode.READ_ONLY);
    if (getKey().equals(BaseField.VALUE_TYPE.getKey())) {
      throw new IllegalArgumentException("Cannot set value of the value type field");
    }
    if (!fieldValue.isInstance(fieldType)) {
      throw new IllegalArgumentException("Cannot set value of a " +
          fieldType.getName(RetrieveMode.READ_ONLY).getBaseValue() +
          " field to a " + fieldValue.getType(RetrieveMode.READ_ONLY).getName(RetrieveMode.READ_ONLY).getBaseValue());
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
