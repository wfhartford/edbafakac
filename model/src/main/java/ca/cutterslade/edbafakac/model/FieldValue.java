package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public final class FieldValue extends RecordValue {

  protected FieldValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public String getFieldKey() {
    return getProperty(BaseField.FIELD_KEY.getKey());
  }

  public TypeValue getFieldType(final boolean readOnly) {
    return (TypeValue) BaseField.FIELD_TYPE.getField().getValue(this, readOnly);
  }

  public Value getValue(final Value value, final boolean readOnly) {
    return Values.getValue(getRawValue(value), readOnly);
  }

  public String getRawValue(final Value value) {
    return value.getProperty(getFieldKey());
  }
}
