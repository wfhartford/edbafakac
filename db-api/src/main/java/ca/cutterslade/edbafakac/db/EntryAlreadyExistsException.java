package ca.cutterslade.edbafakac.db;

public class EntryAlreadyExistsException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final String key;

  public EntryAlreadyExistsException(final String key) {
    super();
    this.key = key;
  }

  public EntryAlreadyExistsException(final String key, final String message, final Throwable cause) {
    super(message, cause);
    this.key = key;
  }

  public EntryAlreadyExistsException(final String key, final String message) {
    super(message);
    this.key = key;
  }

  public EntryAlreadyExistsException(final String key, final Throwable cause) {
    super(cause);
    this.key = key;
  }
}
