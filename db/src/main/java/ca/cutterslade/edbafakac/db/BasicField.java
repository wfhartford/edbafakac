package ca.cutterslade.edbafakac.db;

import java.util.Collection;

import org.bson.types.ObjectId;

import com.google.common.collect.ImmutableSet;

public enum BasicField {
  ID(BasicType.OBJECT_ID, "_id"),
  NAME(BasicType.STRING, "6e9d37d4-803f-4ab2-aee8-1455ad3b25d9"),
  TYPE(BasicType.TYPE, "cbc1377a-549f-42ec-9962-d9585073765e"),
  FIELD_TYPE(BasicType.TYPE, "ca2f68ce-ea4a-42f9-994e-acbe39859b6b"),
  FIELD_KEY(BasicType.STRING, "420eddc0-d69b-48f6-af89-9038c54c5a1b"),
  TYPE_FIELDS(BasicType.COLLECTION, "d5df529c-ad4a-4497-b85d-e3bf55e1ab66"),
  CONVERTER(BasicType.ACTION, "93fb8764-26dd-4ebb-ba9f-86e6a47050df");

  private final BasicType type;

  private final String key;

  private BasicField(final BasicType type, final String key) {
    this.type = type;
    this.key = key;
  }

  public Field<?> getField(final Configuration configuration) {
    return Database.getExistingInstance(configuration).getField(name());
  };

  public BasicType getType() {
    return type;
  }

  public String getKey() {
    return key;
  }

  @Override
  public String toString() {
    return "BasicField." + name();
  }

  public static Field<ObjectId> getIdField(final Configuration configuration) {
    return (Field<ObjectId>) ID.getField(configuration);
  }

  public static Field<String> getNameField(final Configuration configuration) {
    return (Field<String>) NAME.getField(configuration);
  }

  public static Field<Type<?>> getTypeField(final Configuration configuration) {
    return (Field<Type<?>>) TYPE.getField(configuration);
  }

  public static Field<Type<?>> getFieldTypeField(final Configuration configuration) {
    return (Field<Type<?>>) FIELD_TYPE.getField(configuration);
  }

  public static Field<String> getFieldKeyField(final Configuration configuration) {
    return (Field<String>) FIELD_KEY.getField(configuration);
  }

  public static Field<Collection<Field<?>>> getTypeFieldsField(final Configuration configuration) {
    return (Field<Collection<Field<?>>>) TYPE_FIELDS.getField(configuration);
  }

  public static Field<Action<?>> getConverterField(final Configuration configuration) {
    return (Field<Action<?>>) CONVERTER.getField(configuration);
  }

  public static Iterable<Field<?>> getBaseFields(final Configuration configuration) {
    return ImmutableSet.of(getIdField(configuration), getNameField(configuration), getTypeField(configuration));
  }
}
