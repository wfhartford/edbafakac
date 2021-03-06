package ca.cutterslade.edbafakac.db.mem;

import java.util.Map;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class MapEntry implements Entry {

  private final Map<String, String> map;

  private final EntryService service;

  private final String entryKey;

  private boolean dirty;

  // Long param list is better than an alternative
  // this is just called by EntryService implementations
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public MapEntry(final String entryKey, final Map<String, String> map, final EntryService service,
      final boolean dirty) {
    Preconditions.checkArgument(null != entryKey);
    Preconditions.checkArgument(null != map);
    Preconditions.checkArgument(null != service);
    this.entryKey = entryKey;
    this.map = map;
    this.service = service;
    this.dirty = dirty;
  }

  @Override
  public String getKey() {
    return entryKey;
  }

  @Override
  public Long getWriteTime() {
    final String value = map.get(WRITE_TIME_KEY);
    return null == value ? null : Long.valueOf(value);
  }

  @Override
  public void setWriteTime(final long millis) {
    map.put(WRITE_TIME_KEY, String.valueOf(millis));
  }

  @Override
  public MapEntry setProperty(final String key, final String value) {
    Preconditions.checkArgument(!service.getReservedKeys().contains(key),
        "EntryService %s has reserved the key %s", service.getClass().getName(), key);
    if (!value.equals(map.put(key, value))) {
      dirty = true;
    }
    return this;
  }

  @Override
  public String getProperty(final String key) {
    return map.get(key);
  }

  @Override
  public boolean hasProperty(final String key) {
    return map.containsKey(key);
  }

  @Override
  public MapEntry removeProperty(final String key) {
    if (null != map.remove(key)) {
      dirty = true;
    }
    return this;
  }

  @Override
  public ImmutableMap<String, String> getProperties() {
    return ImmutableMap.copyOf(map);
  }

  @Override
  public ImmutableSet<String> getPropertyKeys() {
    return ImmutableSet.copyOf(map.keySet());
  }

  @Override
  public boolean isDirty() {
    return dirty;
  }

  @Override
  public EntryService getEntryService() {
    return service;
  }

  @Override
  public String toString() {
    return "MapEntry " + entryKey + ": " + map + "]";
  }

  public void saved() {
    dirty = false;
  }

}
