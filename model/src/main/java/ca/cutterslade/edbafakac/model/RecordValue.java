package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public abstract class RecordValue extends Value {

  protected RecordValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

}
