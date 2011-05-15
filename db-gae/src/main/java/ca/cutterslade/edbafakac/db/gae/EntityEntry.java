package ca.cutterslade.edbafakac.db.gae;

import java.util.Map;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.appengine.api.datastore.Entity;
import com.google.common.collect.ImmutableMap;
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
  public ImmutableMap<String, String> getProperties() {
    final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    for (final Map.Entry<String, Object> entry : entity.getProperties().entrySet()) {
      builder.put(entry.getKey(), (String) entry.getValue());
    }
    return builder.build();
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
