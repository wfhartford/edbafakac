package ca.cutterslade.edbafakac.db;

import java.util.Map;

import com.google.common.base.Preconditions;
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
    return getFieldValue(BasicField.getTypeFieldsField(getConfiguration()));
  }

  public Action<T> getConverter() {
    return (Action<T>) getFieldValue(BasicField.getConverterField(getConfiguration()));
  }

  public T convertExternal(final Object object, final boolean readOnly) {
    return getConverter().perform(object, readOnly);
  }

  public Object convertInternal(final T value) {
    return getConverter().undo(value);
  }

  Type<? extends Entry> asEntryType() {
    Preconditions.checkState(BasicType.isEntryType(this));
    @SuppressWarnings("unchecked")
    final Type<? extends Entry> entryType = (Type<? extends Entry>) this;
    return entryType;
  }

  boolean isInstance(final Object o) {
    if (BasicType.isEntryType(this)) {
      return o instanceof Entry && equals(((Entry) o).getType());
    }
    else {
      return BasicType.valueOf(getName()).isInstance(o);
    }
  }
}
