package ca.cutterslade.edbafakac.db.gae;

import java.util.Map;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class EntityEntry implements Entry {

  private final Entity entity;

  private final EntityEntryService service;

  private boolean dirty;

  EntityEntry(final Entity entity, final EntityEntryService service, final boolean dirty) {
    Preconditions.checkArgument(null != entity);
    Preconditions.checkArgument(null != service);
    this.entity = entity;
    this.service = service;
    this.dirty = dirty;
  }

  @Override
  public String getKey() {
    return entity.getKey().getName();
  }

  @Override
  public Long getWriteTime() {
    final Text value = (Text) entity.getProperty(WRITE_TIME_KEY);
    return null == value ? null : Long.valueOf(value.getValue());
  }

  @Override
  public void setWriteTime(final long millis) {
    entity.setProperty(WRITE_TIME_KEY, new Text(String.valueOf(millis)));
  }

  @Override
  public EntityEntry setProperty(final String key, final String value) {
    Preconditions.checkArgument(null != key, "Cannot set property with null key");
    Preconditions.checkArgument(null != value, "Cannot set property with null value");
    Preconditions.checkArgument(!service.getReservedKeys().contains(key),
        "EntryService %s has reserved the key %s", service.getClass().getName(), key);
    if (!value.equals(entity.getProperty(key))) {
      entity.setProperty(key, new Text(value));
      dirty = true;
    }
    return this;
  }

  @Override
  public String getProperty(final String key) {
    final Text value = ((Text) entity.getProperty(key));
    return null == value ? null : value.getValue();
  }

  @Override
  public boolean hasProperty(final String key) {
    Preconditions.checkArgument(null != key, "Cannot test for existance of a property with null key");
    return entity.hasProperty(key);
  }

  @Override
  public EntityEntry removeProperty(final String key) {
    Preconditions.checkArgument(null != key, "Cannot remove a property with null key");
    if (entity.hasProperty(key)) {
      entity.removeProperty(key);
      dirty = true;
    }
    return this;
  }

  @Override
  public ImmutableMap<String, String> getProperties() {
    final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    for (final Map.Entry<String, Object> entry : entity.getProperties().entrySet()) {
      builder.put(entry.getKey(), ((Text) entry.getValue()).getValue());
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

  @Override
  public boolean isDirty() {
    return dirty;
  }

  public void saved() {
    dirty = false;
  }

  Entity getEntity() {
    return entity;
  }

}
