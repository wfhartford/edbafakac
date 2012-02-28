package ca.cutterslade.edbafakac.db.gae;

import java.util.UUID;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntrySearchService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class EntityEntryService implements EntryService {

  private static final ImmutableSet<String> RESERVED_KEYS = ImmutableSet.of(Entry.WRITE_TIME_KEY);

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  @Override
  public Entry getNewEntry() {
    return new EntityEntry(new Entity(EntityEntry.class.getName(), UUID.randomUUID().toString()), this, true);
  }

  @Override
  public Entry getNewEntry(final String key) {
    try {
      datastoreService.get(KeyFactory.createKey(EntityEntry.class.getName(), key));
      throw new EntryAlreadyExistsException(key);
    }
    catch (final EntityNotFoundException e) {
      final EntityEntry newEntry = new EntityEntry(new Entity(EntityEntry.class.getName(), key), this, false);
      datastoreService.put(newEntry.getEntity());
      return newEntry;
    }
  }

  @Override
  public Entry getEntry(final String key) {
    try {
      return new EntityEntry(datastoreService.get(KeyFactory.createKey(EntityEntry.class.getName(), key)), this, false);
    }
    catch (final EntityNotFoundException e) {
      throw new EntryNotFoundException(key, e);
    }
  }

  @Override
  public void saveEntry(final Entry entry) {
    ((EntityEntry) entry).setWriteTime(System.currentTimeMillis());
    saveEntryWithoutUpdatingWriteTime(entry);
    datastoreService.put(((EntityEntry) entry).getEntity());
    ((EntityEntry) entry).saved();
  }

  @Override
  public void saveEntryWithoutUpdatingWriteTime(final Entry entry) {
    Preconditions.checkArgument(entry.hasProperty(Entry.WRITE_TIME_KEY));
    datastoreService.put(((EntityEntry) entry).getEntity());
    ((EntityEntry) entry).saved();
  }

  @Override
  public void removeEntry(final String key) {
    datastoreService.delete(KeyFactory.createKey(EntityEntry.class.getName(), key));
  }

  @Override
  public ImmutableSet<String> getReservedKeys() {
    return RESERVED_KEYS;
  }

  @Override
  public EntrySearchService getSearchService() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("getSearchService has not been implemented");
  }

}
