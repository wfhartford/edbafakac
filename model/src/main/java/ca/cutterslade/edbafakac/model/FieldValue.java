package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public final class FieldValue extends RecordValue {

  public FieldValue() {
    super();
  }

  FieldValue(final Entry entry) {
    super(entry);
  }

  public String getFieldKey() {
    return getProperty(BaseField.FIELD_KEY.getKey());
  }

  public TypeValue getFieldType() {
    return (TypeValue) BaseField.FIELD_TYPE.getField().getValue(this);
  }

  public Value getValue(final Value value) {
    return Values.getValue(getRawValue(value), getFieldType().getTypeClass());
  }

  public String getRawValue(final Value value) {
    return getProperty(getFieldKey());
  }
}
