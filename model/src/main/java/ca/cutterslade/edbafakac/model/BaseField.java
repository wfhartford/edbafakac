package ca.cutterslade.edbafakac.model;

public enum BaseField {
  VALUE_NAME("619c10d4-6d66-43d3-ab50-35713f603426", BaseType.STRING),
  VALUE_TYPE("bb1ba5f1-0914-474f-94d9-3e2372a88012", BaseType.TYPE),
  VALUE_CLASS("4da490d4-74ee-450b-ba78-5ec4c34182df", BaseType.RAW),
  FIELD_TYPE("ca6ef65b-368e-4daf-a671-75ccefea814e", BaseType.TYPE),
  FIELD_KEY("80262551-d526-4efb-84c8-dfe6093af7b4", BaseType.RAW),
  TYPE_FIELDS("57cf2358-235f-43f4-8a56-436b419029be", BaseType.LIST),
  TYPE_TYPE("5408cc31-e755-4f48-a161-77796e0f70b4", BaseType.TYPE),
  TYPE_CLASS("1c979df8-f291-4d1e-b020-8ec7f77e04b4", BaseType.RAW);

  private final String key;

  private final BaseType type;

  private BaseField(final String key, final BaseType type) {
    this.key = key;
    this.type = type;
  }

  public TypeValue getType() {
    return type.getType();
  }

  // These are all equivalent
  // BaseField.*.getKey()
  // BaseFeild.*.getField().getFieldKey()
  // BaseField.*.getFiele().getRawValue(BaseField.*.getFiele())

  public FieldValue getField() {
    return Values.getValue(key, FieldValue.class, BaseField.class.getSimpleName() + '.' + toString());
  }

  public String getKey() {
    return key;
  }
}
