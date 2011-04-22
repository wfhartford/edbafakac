package ca.cutterslade.edbafakac.db;

import org.bson.types.ObjectId;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public final class Types {

  private Types() {
    throw new UnsupportedOperationException();
  }

  private static final Interner<Type<?>> TYPES = Interners.newWeakInterner();

  private static final ImmutableMap<String, Object> TYPE_TYPE_VALUES = ImmutableMap.of();

  private static final ImmutableMap<String, Object> FIELD_TYPE_VALUES = ImmutableMap.of();

  private static <T> Type<T> intern(final Type<T> type) {
    return (Type<T>) TYPES.intern(type);
  }

  public static Type<Type<?>> getTypeType(final Configuration configuration) {
    return intern(Type.<Type<?>> base(TYPE_TYPE_VALUES, configuration));
  }

  public static Type<Field<?>> getFieldType(final Configuration configuration) {
    return intern(Type.<Field<?>> base(FIELD_TYPE_VALUES, configuration));
  }

  public static Type<ObjectId> getObjectIdType(final Configuration configuration) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("getObjectIdType has not been implemented");
  }

  public static Type<String> getStringType(final Configuration configuration) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("getStringType has not been implemented");
  }
}
