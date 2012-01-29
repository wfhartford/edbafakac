package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public final class RecordValue extends Value<RecordValue> {

  RecordValue(final Entry entry, final RetrieveMode retrieveMode) {
    super(entry, retrieveMode);
  }
}
