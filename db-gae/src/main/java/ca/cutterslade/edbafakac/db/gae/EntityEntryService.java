package ca.cutterslade.edbafakac.db.gae;

import java.util.UUID;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

public class EntityEntryService implements EntryService {

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  @Override
  public Entry getNewEntry() {
    return new EntityEntry(new Entity(EntityEntry.class.getName(), UUID.randomUUID().toString()), this);
  }

  @Override
  public Entry getNewEntry(final String key) {
    try {
      datastoreService.get(KeyFactory.createKey(EntityEntry.class.getName(), key));
      throw new EntryAlreadyExistsException(key);
    }
    catch (final EntityNotFoundException e) {
      final EntityEntry newEntry = new EntityEntry(new Entity(EntityEntry.class.getName(), key), this);
      datastoreService.put(newEntry.getEntity());
      return newEntry;
    }
  }

  @Override
  public Entry getEntry(final String key) {
    try {
      return new EntityEntry(datastoreService.get(KeyFactory.createKey(EntityEntry.class.getName(), key)), this);
    }
    catch (final EntityNotFoundException e) {
      throw new EntryNotFoundException(key, e);
    }
  }

  @Override
  public void saveEntry(final Entry entry) {
    datastoreService.put(((EntityEntry) entry).getEntity());
  }

  @Override
  public void removeEntry(final String key) {
    datastoreService.delete(KeyFactory.createKey(EntityEntry.class.getName(), key));
  }

}
