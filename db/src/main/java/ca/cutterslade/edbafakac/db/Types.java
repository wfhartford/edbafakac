package ca.cutterslade.edbafakac.db;

import org.bson.types.ObjectId;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public final class Types {

  private Types() {
    throw new UnsupportedOperationException();
  }

  private static final Interner<Type<?>> TYPES = Interners.newWeakInterner();

  private static final DBObject TYPE_PARAMS = new BasicDBObject();

  private static final DBObject FIELD_PARAMS = new ImmutableDBObject(ImmutableMap.of());

  public static Type<Type<?>> getTypeType(final Configuration configuration) {
    return (Type<Type<?>>) TYPES.intern(new Type<Type<?>>(TYPE_PARAMS, configuration));
  }

  public static Type<Field<?>> getFieldType(final Configuration configuration) {
    return (Type<Field<?>>) TYPES.intern(new Type<Field<?>>(FIELD_PARAMS, configuration));
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
