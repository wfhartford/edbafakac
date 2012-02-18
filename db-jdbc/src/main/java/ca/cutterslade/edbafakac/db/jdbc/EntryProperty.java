package ca.cutterslade.edbafakac.db.jdbc;


final class EntryProperty {

  private final String entryKey;

  private final String propertyKey;

  private final String propertyValue;

  public EntryProperty(final String entryKey, final String propertyKey, final String propertyValue) {
    this.entryKey = entryKey;
    this.propertyKey = propertyKey;
    this.propertyValue = propertyValue;
  }

  protected String getEntryKey() {
    return entryKey;
  }

  protected String getPropertyKey() {
    return propertyKey;
  }

  protected String getPropertyValue() {
    return propertyValue;
  }

}
