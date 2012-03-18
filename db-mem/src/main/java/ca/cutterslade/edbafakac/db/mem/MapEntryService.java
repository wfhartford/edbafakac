package ca.cutterslade.edbafakac.db.mem;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.search.AbstractSearchService;
import ca.cutterslade.edbafakac.db.search.FieldValueSearchTerm;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MapEntryService implements EntryService {

  private static final ImmutableSet<String> RESERVED_KEYS = ImmutableSet.of(Entry.WRITE_TIME_KEY);

  private final class FieldValueSearchResult implements Iterable<String> {

    private final Object resultsMutex = new Object();

    private final Object dirtyMutex = new Object();

    private final FieldValueSearchTerm term;

    private final Set<String> results = Sets.newHashSet();

    private Set<String> dirtyEntries = Sets.newHashSet(entries.keySet());

    public FieldValueSearchResult(final FieldValueSearchTerm term) {
      this.term = term;
    }

    public ImmutableSet<String> getResults() {
      synchronized (resultsMutex) {
        refreshDirtyEntries();
        return ImmutableSet.copyOf(results);
      }
    }

    private void refreshDirtyEntries() {
      final Set<String> dirty;
      synchronized (dirtyMutex) {
        dirty = dirtyEntries;
        dirtyEntries = Sets.newHashSet();
      }
      for (final String key : dirty) {
        results.remove(key);
        final ImmutableMap<String, String> values = entries.get(key);
        if (null != values) {
          for (final String field : term.getFieldKeys()) {
            for (final String value : term.getValues()) {
              if (value.equals(values.get(field))) {
                results.add(key);
                break;
              }
            }
          }
        }
      }
    }

    public void addDirtyEntry(final String key) {
      synchronized (dirtyMutex) {
        dirtyEntries.add(key);
      }
    }

    @Override
    public Iterator<String> iterator() {
      return getResults().iterator();
    }

  }

  private final ConcurrentMap<String, ImmutableMap<String, String>> entries = new MapMaker().makeMap();

  private final LoadingCache<FieldValueSearchTerm, FieldValueSearchResult> fieldValueSearchResultCache =
      CacheBuilder.newBuilder().build(new CacheLoader<FieldValueSearchTerm, FieldValueSearchResult>() {

        @Override
        public FieldValueSearchResult load(final FieldValueSearchTerm key) {
          return new FieldValueSearchResult(key);
        }
      });

  private final ReadWriteLock searchTermLock = new ReentrantReadWriteLock();

  private final AbstractSearchService<MapEntryService> searchService = new MapSearchService(this);

  @Override
  public EntrySearchService getSearchService() {
    return searchService;
  }

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
    addDirtyEntryKey(key);
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
  public boolean entryExists(final String key) {
    return entries.containsKey(key);
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
    addDirtyEntryKey(entry.getKey());
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

  Iterable<String> searchForKeys(final FieldValueSearchTerm term) {
    final Iterable<String> results;
    final Lock lock = searchTermLock.writeLock();
    lock.lock();
    try {
      results = fieldValueSearchResultCache.getUnchecked(term);
    }
    finally {
      lock.unlock();
    }
    return results;
  }

  Iterable<String> getAllKeys() {
    return Collections.unmodifiableSet(entries.keySet());
  }

  private void addDirtyEntryKey(final String key) {
    final Lock lock = searchTermLock.readLock();
    lock.lock();
    try {
      for (final FieldValueSearchResult result : fieldValueSearchResultCache.asMap().values()) {
        result.addDirtyEntry(key);
      }
    }
    finally {
      lock.unlock();
    }
  }

}
