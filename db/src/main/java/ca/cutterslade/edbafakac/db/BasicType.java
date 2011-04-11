package ca.cutterslade.edbafakac.db;

import org.bson.types.ObjectId;

import com.google.common.collect.ImmutableMap;

public enum BasicType {
  STRING("5e812af7-7363-4c93-bee4-bdc9fdb498ff") {

    @Override
    public Type<String> getType(final Configuration configuration) {
      return Types.getStringType(configuration);
    }
  },
  OBJECT_ID("d5bc6fb6-7578-4d9a-bc79-e4edecf053ae") {

    @Override
    public Type<ObjectId> getType(final Configuration configuration) {
      return Types.getObjectIdType(configuration);
    }
  },
  TYPE("6b7f2e5b-5a3c-4f43-88ac-d25e46ee3f02") {

    @Override
    public Type<Type<?>> getType(final Configuration configuration) {
      return Types.getTypeType(configuration);
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

  private final String id;

  private BasicType(final String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public static BasicType forId(final String id) {
    return BY_ID.get(id);
  }

  public abstract Type<?> getType(Configuration configuration);
}
