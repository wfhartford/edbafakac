package ca.cutterslade.edbafakac.db;

import com.mongodb.DBObject;

public final class Type<T> extends Entry {

  public Type(final DBObject object, final Configuration configuration) {
    super(object, configuration);
  }

  public Iterable<Field<?>> getTypeFields() {
    return getDatabase().getFields(this);
  }

  public T convert(final Object object) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("convert has not been implemented");
  }

}
