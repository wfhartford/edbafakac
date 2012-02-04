package ca.cutterslade.edbafakac.model;

import javax.annotation.Nonnull;

public enum Fields {
  ;

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

  public static FieldValue getField(@Nonnull final String key, @Nonnull final RetrieveMode retrieveMode) {
    return (FieldValue) Values.getValue(key, retrieveMode);
  }

}
