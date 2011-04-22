package ca.cutterslade.edbafakac.db;

import com.mongodb.DBObject;

public class Action<T> extends Entry {

  public Action(final DBObject object, final Configuration configuration, final boolean readOnly) {
    super(object, configuration, readOnly);
  }

  public T apply(final Object object) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("apply has not been implemented");
  }

}
