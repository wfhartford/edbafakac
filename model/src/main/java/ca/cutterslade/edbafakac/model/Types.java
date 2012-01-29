package ca.cutterslade.edbafakac.model;

public final class Types {

  private Types() {
    throw new UnsupportedOperationException();
  }

  public static TypeValue getBooleanType() {
    return BaseType.BOOLEAN.getValue();
  }

  public static TypeValue getDateType() {
    return BaseType.DATE.getValue();
  }

  public static TypeValue getDecimalType() {
    return BaseType.DECIMAL.getValue();
  }

  public static TypeValue getIntegerType() {
    return BaseType.INTEGER.getValue();
  }

  public static TypeValue getListType() {
    return BaseType.LIST.getValue();
  }

  public static TypeValue getStringType() {
    return BaseType.STRING.getValue();
  }

  public static TypeValue getFieldType() {
    return BaseType.FIELD.getValue();
  }

  public static TypeValue getTypeType() {
    return BaseType.TYPE.getValue();
  }

  public static TypeValue getType(final String key, final RetrieveMode retrieveMode) {
    return (TypeValue) Values.getValue(key, retrieveMode);
  }

  public static TypeValue getNewType(final StringValue name) {
    return (TypeValue) getTypeType().getNewValue(name);
  }
}
