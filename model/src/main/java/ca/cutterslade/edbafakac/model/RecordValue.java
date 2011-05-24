package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public final class RecordValue extends Value {

  RecordValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }
}
