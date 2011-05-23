package ca.cutterslade.edbafakac.db;


public class EntryNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String key;

  public EntryNotFoundException(final String key) {
    super("Attempted to retrieve missing entry with key '" + key + "'");
    this.key = key;
  }

  public EntryNotFoundException(final String key, final Throwable e) {
    super("Attempted to retrieve missing entry with key '" + key + "'", e);
    this.key = key;
  }

  public String getKey() {
    return key;
  }

}
