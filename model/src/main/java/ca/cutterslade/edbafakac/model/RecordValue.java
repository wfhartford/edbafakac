package ca.cutterslade.edbafakac.model;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;

public final class RecordValue extends Value<RecordValue> {

  RecordValue(@Nonnull final ValueService service, @Nonnull final Entry entry,
      @Nonnull final RetrieveMode retrieveMode) {
    super(service, entry, retrieveMode);
  }
}
