package ca.cutterslade.edbafakac.db;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public interface Entry {

  String getKey();

  void setProperty(String key, String value);

  String getProperty(String key);

  void removeProperty(String key);

  ImmutableMap<String, String> getProperties();

  ImmutableSet<String> getPropertyKeys();
}
