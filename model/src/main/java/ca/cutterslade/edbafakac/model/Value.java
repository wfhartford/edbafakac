package ca.cutterslade.edbafakac.model;

import java.lang.reflect.InvocationTargetException;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public abstract class Value {

  private static final String VALUE_CLASS_KEY = "c1923004-e4f0-499d-b923-5e4a850f4af4";

  private final Entry entry;

  private final ImmutableMap<String, String> pristine;

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
    pristine = entry.getProperties();
  }

  protected Value() {
    this(Values.getNewEntry());
  }

  private final EntryService getEntryService() {
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
    Preconditions.checkArgument(null != value);
    entry.setProperty(propertyName, value);
  }

  protected final void setPropertyIfMissing(final String propertyName, final String value) {
    if (!entry.hasProperty(propertyName)) {
      setProperty(propertyName, value);
    }
  }

  public final void save() {
    ImmutableMap<String, String> current;
    try {
      current = getEntryService().getEntry(getKey()).getProperties();
    }
    catch (final EntryNotFoundException e) {
      current = null;
    }
    onBeforeSave(pristine, current, entry.getProperties());
    getEntryService().saveEntry(entry);
  }

  protected void onBeforeSave(final ImmutableMap<String, String> previouslyRead,
      final ImmutableMap<String, String> justRead, final ImmutableMap<String, String> toWrite) {
  }

  public boolean isInstance(final TypeValue type) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("isInstance has not been implemented");
  }

}
