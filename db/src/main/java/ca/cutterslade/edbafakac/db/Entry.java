package ca.cutterslade.edbafakac.db;

import com.google.common.collect.Iterables;
import com.mongodb.DBObject;

public abstract class Entry {

  private final DBObject object;

  private final Configuration configuration;

  private final boolean readOnly;

  public Entry(final DBObject object, final Configuration configuration, final boolean readOnly) {
    this.object = object;
    this.configuration = configuration;
    this.readOnly = readOnly;
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public Database getDatabase() {
    return Database.getExistingInstance(configuration);
  }

  public Type<?> getType() {
    return getFieldValue(Fields.getTypeField(configuration));
  }

  public String getName() {
    return getFieldValue(Fields.getNameField(configuration));
  }

  public Iterable<Field<?>> getFields() {
    return Iterables.concat(Fields.getBaseFields(configuration), getType().getTypeFields());
  }

  public <T> T getFieldValue(final Field<T> field) {
    return field.getValue(this);
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  DBObject getObject() {
    return object;
  }

}
