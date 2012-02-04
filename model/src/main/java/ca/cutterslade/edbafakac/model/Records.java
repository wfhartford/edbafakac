package ca.cutterslade.edbafakac.model;

import javax.annotation.Nonnull;

public enum Records {
  ;

  public static RecordValue getRecord(@Nonnull final String key, @Nonnull final RetrieveMode retrieveMode) {
    return (RecordValue) Values.getValue(key, retrieveMode);
  }
}
