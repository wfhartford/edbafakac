package ca.cutterslade.edbafakac.db;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public final class Type<T> extends Entry {

  static <T> Type<T> base(final Map<String, Object> values, final Configuration configuration) {
    return new Type<T>(new BasicDBObject(values), configuration, true);
  }

  public Type(final DBObject object, final Configuration configuration, final boolean readOnly) {
    super(object, configuration, readOnly);
  }

  public Iterable<Field<?>> getTypeFields() {
    return Fields.getFieldsField(getConfiguration()).getValue(this);
  }

  public Action<T> getConverter() {
    return (Action<T>) Fields.getConverterField(getConfiguration()).getValue(this);
  }

  public T convert(final Object object) {
    return getConverter().apply(object);
  }
}
