package ca.cutterslade.edbafakac.gae.model;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import com.google.appengine.api.datastore.Entity;
import com.google.common.base.Preconditions;

public abstract class Value {

  static final String VALUE_CLASS_KEY = "c1923004-e4f0-499d-b923-5e4a850f4af4";

  private final Entity entity;

  protected static final Value getInstance(final Entity entity) {
    try {
      final String valueClass = (String) entity.getProperty(VALUE_CLASS_KEY);
      Preconditions.checkArgument(null != valueClass);
      final Class<? extends Value> clazz = Class.forName(valueClass).asSubclass(Value.class);
      return clazz.getConstructor(Entity.class).newInstance(entity);
    }
    catch (final ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
    catch (final InstantiationException e) {
      throw new IllegalArgumentException(e);
    }
    catch (final IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
    catch (final InvocationTargetException e) {
      throw new IllegalArgumentException(e);
    }
    catch (final NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }

  protected Value(final Entity entity) {
    this.entity = entity;
    final String valueClass = (String) entity.getProperty(VALUE_CLASS_KEY);
    if (null == valueClass) {
      entity.setProperty(VALUE_CLASS_KEY, getClass().getName());
    }
    else {
      Preconditions.checkArgument(getClass().getName().equals(valueClass));
    }
  }

  protected Value() {
    this(new Entity(Value.class.getName(), UUID.randomUUID().toString()));
  }

  protected Value(final Value parent) {
    this(new Entity(Value.class.getName(), UUID.randomUUID().toString(), parent.entity.getKey()));
  }

  public final String getKey() {
    return entity.getKey().getName();
  }

  public final String getParentKey() {
    return entity.getParent().getName();
  }

  protected final Object getProperty(final String propertyName) {
    return entity.getProperty(propertyName);
  }

  protected final void removeProperty(final String propertyName) {
    entity.removeProperty(propertyName);
  }

  protected final void setProperty(final String propertyName, final Object value) {
    if (null == value) {
      removeProperty(propertyName);
    }
    else {
      entity.setProperty(propertyName, value);
    }
  }

  protected final void setPropertyIfMissing(final String propertyName, final Object value) {
    if (!entity.hasProperty(propertyName)) {
      setProperty(propertyName, value);
    }
  }

}
