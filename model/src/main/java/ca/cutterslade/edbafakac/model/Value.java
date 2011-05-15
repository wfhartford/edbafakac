package ca.cutterslade.edbafakac.model;

import java.lang.reflect.InvocationTargetException;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntryServiceFactory;

import com.google.common.base.Preconditions;

public abstract class Value {

  private static final class ServiceHolder {

    private static final EntryService entryService = EntryServiceFactory.INSTANCE.getEntryService();

    public static EntryService getEntryService() {
      return entryService;
    }
  }

  private static final String VALUE_CLASS_KEY = "c1923004-e4f0-499d-b923-5e4a850f4af4";

  private final Entry entry;

  protected static final Value getInstance(final Entry entry) {
    try {
      final String valueClass = entry.getProperty(VALUE_CLASS_KEY);
      Preconditions.checkArgument(null != valueClass);
      final Class<? extends Value> clazz = Class.forName(valueClass).asSubclass(Value.class);
      return clazz.getConstructor(Entry.class).newInstance(entry);
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

  protected Value(final Entry entry) {
    this.entry = entry;
    final String valueClass = entry.getProperty(VALUE_CLASS_KEY);
    if (null == valueClass) {
      entry.setProperty(VALUE_CLASS_KEY, getClass().getName());
    }
    else {
      Preconditions.checkArgument(getClass().getName().equals(valueClass));
    }
  }

  protected Value() {
    this(ServiceHolder.getEntryService().getNewEntry());
  }

  protected final EntryService getEntryService() {
    return entry.getEntryService();
  }

  public final String getKey() {
    return entry.getKey();
  }

  protected final String getProperty(final String propertyName) {
    return entry.getProperty(propertyName);
  }

  protected final void removeProperty(final String propertyName) {
    entry.removeProperty(propertyName);
  }

  protected final void setProperty(final String propertyName, final String value) {
    if (null == value) {
      removeProperty(propertyName);
    }
    else {
      entry.setProperty(propertyName, value);
    }
  }

  protected final void setPropertyIfMissing(final String propertyName, final String value) {
    if (!entry.hasProperty(propertyName)) {
      setProperty(propertyName, value);
    }
  }

}
