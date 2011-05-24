package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public final class FieldValue extends Value {

  FieldValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public String getFieldKey() {
    return getProperty(BaseField.FIELD_KEY.getKey());
  }

  public TypeValue getFieldType(final boolean readOnly) {
    return (TypeValue) BaseField.FIELD_TYPE.getField().getValue(this, readOnly);
  }

  public Value getValue(final Value value, final boolean readOnly) {
    Preconditions.checkArgument(readOnly || !value.isReadOnly(),
        "Cannot provide writable field value from read only value");
    final String rawValue = getRawValue(value);
    return null == rawValue ? null : Values.getValue(rawValue, readOnly);
  }

  public void setValue(final Value targetValue, final Value fieldValue) {
    setRawValue(targetValue, fieldValue.getKey());
  }

  public String getRawValue(final Value value) {
    return value.getProperty(getFieldKey());
  }

  public void setRawValue(final Value targetValue, final String rawValue) {
    Preconditions.checkArgument(!targetValue.isReadOnly(), "Cannot set field value on read only value");
    targetValue.setProperty(getFieldKey(), rawValue);
  }
}
