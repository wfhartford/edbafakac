package ca.cutterslade.edbafakac.db.gae;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.appengine.api.datastore.Entity;
import com.google.common.collect.ImmutableSet;

public class EntityEntry implements Entry {

  private final Entity entity;

  private final EntityEntryService service;

  public EntityEntry(final Entity entity, final EntityEntryService service) {
    this.entity = entity;
    this.service = service;
  }

  @Override
  public String getKey() {
    return entity.getKey().getName();
  }

  @Override
  public void setProperty(final String key, final String value) {
    entity.setProperty(key, value);
  }

  @Override
  public String getProperty(final String key) {
    return (String) entity.getProperty(key);
  }

  @Override
  public boolean hasProperty(final String key) {
    return entity.hasProperty(key);
  }

  @Override
  public void removeProperty(final String key) {
    entity.removeProperty(key);
  }

  @Override
  public ImmutableSet<String> getPropertyKeys() {
    return ImmutableSet.copyOf(entity.getProperties().keySet());
  }

  @Override
  public EntryService getEntryService() {
    return service;
  }

  Entity getEntity() {
    return entity;
  }

}
