package ca.cutterslade.edbafakac.db;

import java.util.Map;
import java.util.Set;

import org.bson.BSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mongodb.DBObject;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ImmutableDBObject implements DBObject {

  private final DBObject backing;

  ImmutableDBObject(final DBObject backing) {
    this.backing = backing;
  }

  public static Object wrap(final Object object) {
    Object wrapped;
    if (object instanceof DBObject) {
      wrapped = new ImmutableDBObject((DBObject) object);
    }
    else {
      wrapped = object;
    }
    return wrapped;
  }

  @Override
  public Object put(final String key, final Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void markAsPartialObject() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(final BSONObject object) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isPartialObject() {
    return backing.isPartialObject();
  }

  @Override
  public void putAll(final Map map) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object get(final String key) {
    return wrap(backing.get(key));
  }

  @Override
  public Map toMap() {
    return ImmutableMap.copyOf(backing.toMap());
  }

  @Override
  public Object removeField(final String key) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public boolean containsKey(final String key) {
    return backing.containsKey(key);
  }

  @Override
  public boolean containsField(final String field) {
    return backing.containsField(field);
  }

  @Override
  public Set<String> keySet() {
    return ImmutableSet.copyOf(backing.keySet());
  }

}
