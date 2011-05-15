package ca.cutterslade.edbafakac.db;

import com.google.common.collect.ImmutableSet;

public interface Entry {

  String getKey();

  void setProperty(String key, String value);

  String getProperty(String key);

  void removeProperty(String key);

  ImmutableSet<String> getPropertyKeys();

  boolean hasProperty(String key);

  EntryService getEntryService();
}
