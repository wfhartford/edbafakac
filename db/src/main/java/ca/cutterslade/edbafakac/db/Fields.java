package ca.cutterslade.edbafakac.db;

import org.bson.types.ObjectId;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public final class Fields {

  static final String ID_FIELD_KEY = "_id";

  static final String NAME_FIELD_KEY = "261ea612-4fce-48a0-9404-47ffc08a8b34";

  static final String TYPE_FIELD_KEY = "39561736-3831-4027-9e2d-06970b6e2239";

  static final String KEY_FIELD_KEY = "912d7e76-bafa-4de1-b9b8-9e83148b9a4d";

  static final String FIELD_TYPE_FIELD_KEY = "15dd12a8-46c9-45f7-a5cd-985a7a583c84";

  static final String FIELD_SUB_TYPE_FIELD_KEY = "1327226d-0d55-44ce-a80c-a00180741996";

  static final String TYPE_ID_FIELD_KEY = "ab7b25d5-bcfb-4940-830e-38fcb6638d68";

  private static final ImmutableMap<String, Object> ID_FIELD_VALUES =
      getFieldValues("Object ID", BasicType.OBJECT_ID, ID_FIELD_KEY);

  private static final ImmutableMap<String, Object> NAME_FIELD_VALUES =
      getFieldValues("Object Name", BasicType.STRING, NAME_FIELD_KEY);

  private static final ImmutableMap<String, Object> TYPE_FIELD_VALUES =
      getFieldValues("Object Type", BasicType.TYPE, TYPE_FIELD_KEY);

  private static final ImmutableMap<String, Object> KEY_FIELD_VALUES =
      getFieldValues("Key", BasicType.STRING, KEY_FIELD_KEY);

  private static final ImmutableMap<String, Object> FIELD_TYPE_FIELD_VALUES =
      getFieldValues("Field Type", BasicType.TYPE, FIELD_TYPE_FIELD_KEY);

  private static final ImmutableMap<String, Object> TYPE_ID_FIELD_VALUES =
      getFieldValues("Type Type", BasicType.STRING, TYPE_ID_FIELD_KEY);

  private static final Interner<Field<?>> FIELDS = Interners.newWeakInterner();

  private Fields() {
    throw new UnsupportedOperationException();
  }

  private static ImmutableMap<String, Object> getFieldValues(final String name, final BasicType type,
      final String key) {
    return ImmutableMap.<String, Object> builder()
        .put(NAME_FIELD_KEY, name)
        .put(TYPE_FIELD_KEY, BasicType.FIELD.getId())
        .put(FIELD_TYPE_FIELD_KEY, type.getId())
        .put(KEY_FIELD_KEY, key)
        .build();
  }

  private static <T> Field<T> intern(final Field<T> field) {
    return (Field<T>) FIELDS.intern(field);
  }

  static Field<ObjectId> getIdField(final Configuration configuration) {
    return intern(Field.<ObjectId> base(ID_FIELD_VALUES, configuration));
  }

  static Field<String> getNameField(final Configuration configuration) {
    return intern(Field.<String> base(NAME_FIELD_VALUES, configuration));
  }

  static Field<Type<?>> getTypeField(final Configuration configuration) {
    return intern(Field.<Type<?>> base(TYPE_FIELD_VALUES, configuration));
  }

  static Field<String> getKeyField(final Configuration configuration) {
    return intern(new KeyField(KEY_FIELD_VALUES, configuration));
  }

  static Field<Type<?>> getFieldTypeField(final Configuration configuration) {
    return intern(Field.<Type<?>> base(FIELD_TYPE_FIELD_VALUES, configuration));
  }

  static Field<String> getTypeIDField(final Configuration configuration) {
    return intern(Field.<String> base(TYPE_ID_FIELD_VALUES, configuration));
  }

  static Iterable<Field<?>> getBaseFields(final Configuration configuration) {
    return ImmutableSet.of(getIdField(configuration), getNameField(configuration), getTypeField(configuration));
  }

  public static Field<Iterable<Field<?>>> getFieldsField(final Configuration configuration) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("getFieldsField has not been implemented");
  }

  public static Field<Action<?>> getConverterField(final Configuration configuration) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("getConverterField has not been implemented");
  }

}
