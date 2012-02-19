package ca.cutterslade.edbafakac.db;

import javax.annotation.Nonnull;

/**
 * Indicates an unexpected error accessing the underlying entry storage system. May wrap checked exceptions such as
 * {@link java.io.IOException} or {@link java.sql.SQLException} depending on the DB implementation.
 * 
 * @author W.F. Hartford
 */
public class EntryStoreException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EntryStoreException(@Nonnull final Throwable cause) {
    super(cause);
  }
}
