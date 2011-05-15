package ca.cutterslade.edbafakac.db;

import com.mongodb.DBObject;

public final class Item extends Entry {

  public Item(final DBObject object, final Configuration configuration, final boolean readOnly) {
    super(object, configuration, readOnly);
  }

}
