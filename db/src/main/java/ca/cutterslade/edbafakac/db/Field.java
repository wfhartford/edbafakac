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
    return (Type<T>) getFieldValue(Fields.getFieldTypeField(getConfiguration()));
  }

  public T getValue(final Entry entry) {
    return getFieldType().convertExternal(entry.getObject().get(getFieldKey()), entry.isReadOnly());
  }

  public void setValue(final Entry entry, final T value) {
    Preconditions.checkArgument(!entry.isReadOnly(), "Entry is read only");
    Preconditions.checkArgument(Iterables.contains(entry.getFields(), this));
    entry.getObject().put(getFieldKey(), getFieldType().convertInternal(value));
  }

  protected String getFieldKey() {
    return getFieldValue(Fields.getKeyField(getConfiguration()));
  }
}
