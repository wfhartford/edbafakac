package ca.cutterslade.edbafakac.model;

public final class Fields {

  private Fields() {
    throw new UnsupportedOperationException();
  }

  public static FieldValue getNameField() {
    return BaseField.VALUE_NAME.getValue();
  }

  public static FieldValue getTypeField() {
    return BaseField.VALUE_TYPE.getValue();
  }

  public static FieldValue getTypeFieldsField() {
    return BaseField.TYPE_FIELDS.getValue();
  }

  public static FieldValue getFieldTypeField() {
    return BaseField.FIELD_TYPE.getValue();
  }

  public static FieldValue getField(final String key, final RetrieveMode retrieveMode) {
    return (FieldValue) Values.getValue(key, retrieveMode);
  }

}
