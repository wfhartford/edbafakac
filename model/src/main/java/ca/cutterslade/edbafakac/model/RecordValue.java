package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public abstract class RecordValue extends Value {

  RecordValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public ListValue getFields(final boolean readOnly) {
    return (ListValue) BaseField.TYPE_FIELDS.getField().getValue(getType(readOnly), readOnly);
  }
}
