package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public final class TypeValue extends RecordValue {

  TypeValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public Class<? extends Value> getTypeClass() {
    try {
      return Class.forName(BaseField.TYPE_CLASS.getField().getRawValue(this)).asSubclass(Value.class);
    }
    catch (final ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  public Value getNewValue() {
    return Values.getNewValue(this);
  }

  public ListValue getTypeFields(final boolean readOnly) {
    return (ListValue) BaseField.TYPE_FIELDS.getField().getValue(this, readOnly);
  }
}
