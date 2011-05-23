package ca.cutterslade.edbafakac.db.mem;

import java.util.Map;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class MapEntry implements Entry {

  private final Map<String, String> map;

  private final EntryService service;

  private final String entryKey;

  public MapEntry(final String entryKey, final Map<String, String> map, final EntryService service) {
    this.entryKey = entryKey;
    this.map = map;
    this.service = service;
  }

  @Override
  public String getKey() {
    return entryKey;
  }

  @Override
  public void setProperty(final String key, final String value) {
    map.put(key, value);
  }

  @Override
  public String getProperty(final String key) {
    return map.get(key);
  }

  @Override
  public void removeProperty(final String key) {
    map.remove(key);
  }

  @Override
  public ImmutableSet<String> getPropertyKeys() {
    return ImmutableSet.copyOf(map.keySet());
  }

  @Override
  public boolean hasProperty(final String key) {
    return map.containsKey(key);
  }

  @Override
  public EntryService getEntryService() {
    return service;
  }

  @Override
  public ImmutableMap<String, String> getProperties() {
    return ImmutableMap.copyOf(map);
  }

  @Override
  public String toString() {
    return "MapEntry " + entryKey + ": " + map + "]";
  }

}
