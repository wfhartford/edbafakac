package ca.cutterslade.edbafakac.model;

public enum BaseField {
  VALUE_NAME(BaseType.STRING),
  FIELD_TYPE(BaseType.TYPE),
  TYPE_FIELDS(BaseType.LIST);

  private final BaseType type;

  private BaseField(final BaseType type) {
    this.type = type;
  }

  public TypeValue getType() {
    // TODO
    throw new UnsupportedOperationException("getType has not been implemented");
  }
}
