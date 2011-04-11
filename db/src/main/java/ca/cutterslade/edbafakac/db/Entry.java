package ca.cutterslade.edbafakac.db;

import com.google.common.collect.Iterables;
import com.mongodb.DBObject;

public abstract class Entry {

  private final DBObject object;

  private final Configuration configuration;

  public Entry(final DBObject object, final Configuration configuration) {
    this.object = object;
    this.configuration = configuration;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public Database getDatabase() {
    return Database.getExistingInstance(configuration);
  }

  public Type<?> getType() {
    return getDatabase().getType(this);
  }

  public Iterable<Field<?>> getFields() {
    return Iterables.concat(Fields.getBaseFields(configuration), getType().getTypeFields());
  }

  public <T> T getFieldValue(final Field<T> field) {
    return field.getValue(this);
  }

  DBObject getObject() {
    return object;
  }

}
