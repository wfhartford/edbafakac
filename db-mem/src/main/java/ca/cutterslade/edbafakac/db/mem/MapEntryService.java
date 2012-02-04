package ca.cutterslade.edbafakac.db.mem;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

public class MapEntryService implements EntryService {

  private static final ImmutableSet<String> RESERVED_KEYS = ImmutableSet.of(Entry.WRITE_TIME_KEY);

  private final ConcurrentMap<String, ImmutableMap<String, String>> entries = new MapMaker().makeMap();

  @Override
  public Entry getNewEntry() {
    return new MapEntry(UUID.randomUUID().toString(), Maps.<String, String> newHashMap(), this, true);
  }

  @Override
  public Entry getNewEntry(final String key) {
    final MapEntry newEntry = new MapEntry(key, Maps.<String, String> newHashMap(), this, false);
    if (null != entries.putIfAbsent(key, newEntry.getProperties())) {
      throw new EntryAlreadyExistsException(key);
    }
    return newEntry;
  }

  @Override
  public Entry getEntry(final String key) {
    final ImmutableMap<String, String> entry = entries.get(key);
    if (null == entry) {
      throw new EntryNotFoundException(key);
    }
    return new MapEntry(key, Maps.newHashMap(entry), this, false);
  }

  @Override
  public void saveEntry(final Entry entry) {
    ((MapEntry) entry).setWriteTime(System.currentTimeMillis());
    saveEntryWithoutUpdatingWriteTime(entry);
  }

  @Override
  public void saveEntryWithoutUpdatingWriteTime(final Entry entry) {
    Preconditions.checkArgument(entry.hasProperty(Entry.WRITE_TIME_KEY));
    entries.put(entry.getKey(), entry.getProperties());
    ((MapEntry) entry).saved();
  }

  @Override
  public void removeEntry(final String key) {
    entries.remove(key);
  }

  @Override
  public ImmutableSet<String> getReservedKeys() {
    return RESERVED_KEYS;
  }

  public void clear() {
    entries.clear();
  }

}
