package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public abstract class RecordValue extends Value {

  protected RecordValue() {
    super();
  }

  RecordValue(final Entry entry) {
    super(entry);
  }

}
