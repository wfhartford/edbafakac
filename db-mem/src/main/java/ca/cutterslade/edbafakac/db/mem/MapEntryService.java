package ca.cutterslade.edbafakac.db.mem;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;

public class MapEntryService implements EntryService {

  private final ConcurrentMap<String, ImmutableMap<String, String>> entries = new MapMaker().makeMap();

  @Override
  public Entry getNewEntry() {
    return new MapEntry(UUID.randomUUID().toString(), Maps.<String, String> newHashMap(), this);
  }

  @Override
  public Entry getNewEntry(final String key) {
    Preconditions.checkArgument(null != key);
    final MapEntry newEntry = new MapEntry(key, Maps.<String, String> newHashMap(), this);
    if (null != entries.putIfAbsent(key, newEntry.getProperties())) {
      throw new EntryAlreadyExistsException(key);
    }
    return newEntry;
  }

  @Override
  public Entry getEntry(final String key) {
    Preconditions.checkArgument(null != key);
    final ImmutableMap<String, String> entry = entries.get(key);
    if (null == entry) {
      throw new EntryNotFoundException(key);
    }
    return new MapEntry(key, entry, this);
  }

  @Override
  public void saveEntry(final Entry entry) {
    Preconditions.checkArgument(null != entry);
    entries.put(entry.getKey(), entry.getProperties());
  }

  @Override
  public void removeEntry(final String key) {
    Preconditions.checkArgument(null != key);
    entries.remove(key);
  }

  public void clear() {
    entries.clear();
  }

}
