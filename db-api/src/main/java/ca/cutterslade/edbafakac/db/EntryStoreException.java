package ca.cutterslade.edbafakac.db;

import javax.annotation.Nonnull;

public class EntryStoreException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EntryStoreException(@Nonnull final Throwable cause) {
    super(cause);
  }
}
