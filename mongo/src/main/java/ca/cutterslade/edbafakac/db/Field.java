package ca.cutterslade.edbafakac.db;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
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
    return (Type<T>) getFieldValue(BasicField.getFieldTypeField(getConfiguration()));
  }

  public T getValue(final Entry entry) {
    return getValue(entry.getObject(), entry.isReadOnly());
  }

  T getValue(final DBObject entry, final boolean readOnly) {
    return getFieldType().convertExternal(entry.get(getFieldKey()), readOnly);
  }

  public void setValue(final Entry entry, final T value) {
    Preconditions.checkArgument(!entry.isReadOnly(), "Entry is read only");
    Preconditions.checkArgument(Iterables.contains(entry.getFields(), this));
    entry.getObject().put(getFieldKey(), getFieldType().convertInternal(value));
  }

  protected String getFieldKey() {
    return getFieldValue(BasicField.getFieldKeyField(getConfiguration()));
  }

}
