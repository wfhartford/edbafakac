package ca.cutterslade.edbafakac.db;

import org.bson.types.ObjectId;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mongodb.BasicDBObject;

public final class Fields {

  static final String ID_FIELD_KEY = "_id";

  static final String KEY_FIELD_KEY = "912d7e76-bafa-4de1-b9b8-9e83148b9a4d";

  private static final String NAME_FIELD_KEY = "261ea612-4fce-48a0-9404-47ffc08a8b34";

  private static final String TYPE_FIELD_KEY = "39561736-3831-4027-9e2d-06970b6e2239";

  private static final ImmutableDBObject ID_FIELD_VALUES = getFieldValues(ID_FIELD_KEY, "Object ID", BasicType.OBJECT_ID);

  private static final ImmutableDBObject NAME_FIELD_VALUES = getFieldValues(NAME_FIELD_KEY, "Object Name", BasicType.STRING);

  private static final ImmutableDBObject TYPE_FIELD_VALUES = getFieldValues(TYPE_FIELD_KEY, "Object Type", BasicType.TYPE);

  private static final Interner<Field<?>> FIELDS = Interners.newWeakInterner();

  private Fields() {
    throw new UnsupportedOperationException();
  }

  static ImmutableDBObject getFieldValues(final String key, final String name, final BasicType type) {
    return new ImmutableDBObject(new BasicDBObject().append(KEY_FIELD_KEY, key).append(NAME_FIELD_KEY, name).append(TYPE_FIELD_KEY, type.getId()));
  }

  static Field<String> getKeyField(final Configuration configuration) {
    return (KeyField) FIELDS.intern(new KeyField(configuration));
  }

  static Field<ObjectId> getIdField(final Configuration configuration) {
    return (Field<ObjectId>) FIELDS.intern(new Field<ObjectId>(ID_FIELD_VALUES, configuration));
  }

  static Field<String> getNameField(final Configuration configuration) {
    return (Field<String>) FIELDS.intern(new Field<String>(NAME_FIELD_VALUES, configuration));
  }

  static Field<Type<?>> getTypeField(final Configuration configuration) {
    return (Field<Type<?>>) FIELDS.intern(new Field(TYPE_FIELD_VALUES, configuration));
  }

  static Iterable<Field<?>> getBaseFields(final Configuration configuration) {
    return ImmutableSet.of(getIdField(configuration), getNameField(configuration), getTypeField(configuration));
  }

}
