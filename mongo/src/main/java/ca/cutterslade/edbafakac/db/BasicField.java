package ca.cutterslade.edbafakac.db;

import java.util.Collection;

import org.bson.types.ObjectId;

import com.google.common.collect.ImmutableSet;

public enum BasicField {
  // core fields
  ID(BasicType.OBJECT_ID, "_id"),
  NAME(BasicType.STRING),
  TYPE(BasicType.TYPE),

  // field fields
  FIELD_TYPE(BasicType.TYPE),
  FIELD_KEY(BasicType.STRING),

  // type fields
  TYPE_FIELDS(BasicType.COLLECTION),
  CONVERTER(BasicType.ACTION),

  // collection fields
  COLLECTION_VALUES(BasicType.INTERNAL);

  private final BasicType type;

  private final String key;

  private BasicField(final BasicType type) {
    this(type, null);
  }

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
    return null == key ? name() : key;
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

  public static Field<Collection<?>> getCollectionValuesField(final Configuration configuration) {
    return (Field<Collection<?>>) COLLECTION_VALUES.getField(configuration);
  }

  public static Iterable<Field<?>> getBaseFields(final Configuration configuration) {
    return ImmutableSet.of(getIdField(configuration), getNameField(configuration), getTypeField(configuration));
  }
}
