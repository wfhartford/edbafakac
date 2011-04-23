package ca.cutterslade.edbafakac.db;

import com.mongodb.DBObject;

public class Action<T> extends Entry {

  public Action(final DBObject object, final Configuration configuration, final boolean readOnly) {
    super(object, configuration, readOnly);
  }

  public T perform(final Object object, final boolean readOnly) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("perform has not been implemented");
  }

  public Object undo(final T value) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("undo has not been implemented");
  }

}
