package ca.cutterslade.edbafakac.db;

import org.bson.types.ObjectId;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public enum BasicType {
  STRING("String", "5e812af7-7363-4c93-bee4-bdc9fdb498ff") {

    @Override
    public Type<String> getType(final Configuration configuration) {
      return getStringType(configuration);
    }
  },
  OBJECT_ID("Object ID", "d5bc6fb6-7578-4d9a-bc79-e4edecf053ae") {

    @Override
    public Type<ObjectId> getType(final Configuration configuration) {
      return getObjectIdType(configuration);
    }
  },
  TYPE("Type", "6b7f2e5b-5a3c-4f43-88ac-d25e46ee3f02") {

    @Override
    public Type<Type<?>> getType(final Configuration configuration) {
      return getTypeType(configuration);
    }
  },
  COLLECTION("Collection", "eb47bbee-467d-427d-ad1f-df5b40b26fc9") {

    @Override
    public Type<Iterable<?>> getType(final Configuration configuration) {
      return getCollectionType(configuration);
    }
  },
  FIELD("Field", "3090c01c-602a-416b-bc04-4602ab06002d") {

    @Override
    public Type<?> getType(final Configuration configuration) {
      return getFieldType(configuration);
    }
  };

  private static final ImmutableMap<String, BasicType> BY_ID;
  static {
    final ImmutableMap.Builder<String, BasicType> builder = ImmutableMap.builder();
    for (final BasicType type : values()) {
      builder.put(type.id, type);
    }
    BY_ID = builder.build();
  }

  private static final Interner<Type<?>> TYPES = Interners.newWeakInterner();

  private final String name;

  private final String id;

  private BasicType(final String name, final String id) {
    this.name = name;
    this.id = id;
  }

  public abstract Type<?> getType(Configuration configuration);

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public static BasicType forId(final String id) {
    return BY_ID.get(id);
  }

  public ImmutableMap<String, Object> getTypeValues() {
    return ImmutableMap.<String, Object> builder()
        .put(Fields.TYPE_ID_FIELD_KEY, getId())
        .put(Fields.NAME_FIELD_KEY, getName())
        .put(Fields.TYPE_FIELD_KEY, TYPE.getId())
        .build();
  }

  private static <T> Type<T> intern(final Type<T> type) {
    return (Type<T>) TYPES.intern(type);
  }

  public static Type<Type<?>> getTypeType(final Configuration configuration) {
    return intern(Type.<Type<?>> base(TYPE.getTypeValues(), configuration));
  }

  public static Type<Field<?>> getFieldType(final Configuration configuration) {
    return intern(Type.<Field<?>> base(FIELD.getTypeValues(), configuration));
  }

  public static Type<ObjectId> getObjectIdType(final Configuration configuration) {
    return intern(Type.<ObjectId> base(OBJECT_ID.getTypeValues(), configuration));
  }

  public static Type<String> getStringType(final Configuration configuration) {
    return intern(Type.<String> base(STRING.getTypeValues(), configuration));
  }

  public static Type<Iterable<?>> getCollectionType(final Configuration configuration) {
    return intern(Type.<Iterable<?>> base(COLLECTION.getTypeValues(), configuration));
  }
}
