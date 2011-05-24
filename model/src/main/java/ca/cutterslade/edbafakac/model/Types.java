package ca.cutterslade.edbafakac.model;

public final class Types {

  private Types() {
    throw new UnsupportedOperationException();
  }

  public static TypeValue getBooleanType() {
    return BaseType.BOOLEAN.getType();
  }

  public static TypeValue getDateType() {
    return BaseType.DATE.getType();
  }

  public static TypeValue getDecimalType() {
    return BaseType.DECIMAL.getType();
  }

  public static TypeValue getIntegerType() {
    return BaseType.INTEGER.getType();
  }

  public static TypeValue getListType() {
    return BaseType.LIST.getType();
  }

  public static TypeValue getStringType() {
    return BaseType.STRING.getType();
  }

  public static TypeValue getRawType() {
    return BaseType.RAW.getType();
  }

  public static TypeValue getFieldType() {
    return BaseType.FIELD.getType();
  }

  public static TypeValue getTypeType() {
    return BaseType.TYPE.getType();
  }

  public static TypeValue getNewType(final StringValue name) {
    return (TypeValue) getTypeType().getNewValue(name);
  }
}
