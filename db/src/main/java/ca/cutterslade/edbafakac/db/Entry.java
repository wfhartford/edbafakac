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

  public final Configuration getConfiguration() {
    return configuration;
  }

  public final Database getDatabase() {
    return Database.getExistingInstance(configuration);
  }

  public final Type<?> getType() {
    return getFieldValue(BasicField.getTypeField(configuration));
  }

  public final String getName() {
    return getFieldValue(BasicField.getNameField(configuration));
  }

  public final Iterable<Field<?>> getFields() {
    return Iterables.concat(BasicField.getBaseFields(configuration), getType().getTypeFields());
  }

  public final <T> T getFieldValue(final Field<T> field) {
    return field.getValue(this);
  }

  public final <T> void setFieldValue(final Field<T> field, final T value) {
    field.setValue(this, value);
  }

  public final boolean isReadOnly() {
    return readOnly;
  }

  final DBObject getObject() {
    return object;
  }

}
