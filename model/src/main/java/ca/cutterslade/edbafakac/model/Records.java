package ca.cutterslade.edbafakac.model;

public final class Records {

  private Records() {
    throw new UnsupportedOperationException();
  }

  public static RecordValue getRecord(final String key, final RetrieveMode retrieveMode) {
    return (RecordValue) Values.getValue(key, retrieveMode);
  }
}
