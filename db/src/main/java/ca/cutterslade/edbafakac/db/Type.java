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
    return getFieldValue(Fields.getFieldsField(getConfiguration()));
  }

  public Action<T> getConverter() {
    return (Action<T>) getFieldValue(Fields.getConverterField(getConfiguration()));
  }

  public T convertExternal(final Object object, final boolean readOnly) {
    return getConverter().perform(object, readOnly);
  }

  public Object convertInternal(final T value) {
    return getConverter().undo(value);
  }
}
