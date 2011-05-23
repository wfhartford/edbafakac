package ca.cutterslade.edbafakac.db;

public class EntryAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String key;

  public EntryAlreadyExistsException(final String key) {
    super("Attempted to create a new entry specifying already used key '" + key + "'");
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
