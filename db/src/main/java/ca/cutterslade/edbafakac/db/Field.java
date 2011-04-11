package ca.cutterslade.edbafakac.db;

import com.mongodb.DBObject;

public class Field<T> extends Entry {

  Field(final DBObject object, final Configuration configuration) {
    super(object, configuration);
  }

  public Type<T> getFieldType() {
    return (Type<T>) Fields.getTypeField(getConfiguration()).getValue(this);
  }

  public T getValue(final Entry entry) {
    return getFieldType().convert(entry.getObject().get(getFieldKey()));
  }

  protected String getFieldKey() {
    return Fields.getKeyField(getConfiguration()).getValue(this);
  }
}
