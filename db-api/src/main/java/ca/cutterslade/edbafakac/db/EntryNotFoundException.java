package ca.cutterslade.edbafakac.db;

public class EntryNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EntryNotFoundException(final String key) {
    super();
  }

  public EntryNotFoundException(final String key, final String message, final Throwable cause) {
    super(message, cause);
  }

  public EntryNotFoundException(final String key, final String message) {
    super(message);
  }

  public EntryNotFoundException(final String key, final Throwable cause) {
    super(cause);
  }

}
