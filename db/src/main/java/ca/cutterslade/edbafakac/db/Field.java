package ca.cutterslade.edbafakac.db;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Field<T> extends Entry {

  static <T> Field<T> base(final Map<String, Object> values, final Configuration configuration) {
    return new Field<T>(new BasicDBObject(values), configuration, true);
  }

  Field(final DBObject object, final Configuration configuration, final boolean readOnly) {
    super(object, configuration, readOnly);
  }

  public Type<T> getFieldType() {
    return (Type<T>) Fields.getFieldTypeField(getConfiguration()).getValue(this);
  }

  public T getValue(final Entry entry) {
    return getFieldType().convert(entry.getObject().get(getFieldKey()));
  }

  protected String getFieldKey() {
    return Fields.getKeyField(getConfiguration()).getValue(this);
  }
}
