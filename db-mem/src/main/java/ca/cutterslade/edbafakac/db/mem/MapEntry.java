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
  public void setProperty(final String key, final String value) {
    Preconditions.checkArgument(null != key, "Cannot set property with null key");
    Preconditions.checkArgument(null != value, "Cannot set property with null value");
    if (!value.equals(map.put(key, value))) {
      dirty = true;
    }
  }

  @Override
  public String getProperty(final String key) {
    Preconditions.checkArgument(null != key, "Cannot retrieve a property with null key");
    return map.get(key);
  }

  @Override
  public boolean hasProperty(final String key) {
    Preconditions.checkArgument(null != key, "Cannot test for existance of a property with null key");
    return map.containsKey(key);
  }

  @Override
  public void removeProperty(final String key) {
    Preconditions.checkArgument(null != key, "Cannot remove a property with null key");
    if (null != map.remove(key)) {
      dirty = true;
    }
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
