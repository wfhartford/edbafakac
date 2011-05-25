package ca.cutterslade.edbafakac.model;

public final class Fields {

  private Fields() {
    throw new UnsupportedOperationException();
  }

  public static FieldValue getNameField() {
    return BaseField.VALUE_NAME.getField();
  }

  public static FieldValue getTypeField() {
    return BaseField.VALUE_TYPE.getField();
  }

  public static FieldValue getTypeFieldsField() {
    return BaseField.TYPE_FIELDS.getField();
  }

  public static FieldValue getFieldTypeField() {
    return BaseField.FIELD_TYPE.getField();
  }

  public static FieldValue getField(final String key, final boolean readOnly) {
    return (FieldValue) Values.getValue(key, readOnly);
  }

}
