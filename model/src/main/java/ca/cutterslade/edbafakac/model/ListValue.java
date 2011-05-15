package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

public class ListValue extends Value {

  private static final String SIZE_KEY = "f90cb18e-413d-4f00-864a-4235da06f642";

  public ListValue() {
    super();
  }

  ListValue(final Entry entry) {
    super(entry);
  }

  public long getSize() {
    final String value = getProperty(SIZE_KEY);
    return null == value ? 0 : Long.parseLong(value);
  }

  private long checkIndex(final long index) {
    final long size = getSize();
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(String.valueOf(index));
    }
    return size;
  }

  public Value get(final long position) {
    checkIndex(position);
    final String key = getProperty(String.valueOf(position));
    return getInstance(getEntryService().getEntry(key));
  }

  public void set(final long position, final Value value) {
    checkIndex(position);
    setProperty(String.valueOf(position), value.getKey());
  }

  public void add(final Value value) {
    setProperty(getProperty(SIZE_KEY), value.getKey());
    setProperty(SIZE_KEY, String.valueOf(getSize() + 1));
  }

  public void insert(final long position, final Value value) {
    final long size = getSize();
    if (0 > position || position > size) {
      throw new IndexOutOfBoundsException(String.valueOf(position));
    }
    for (long move = size; move > position; move--) {
      setProperty(String.valueOf(move), getProperty(String.valueOf(move - 1)));
    }
    setProperty(String.valueOf(position), value.getKey());
    setProperty(SIZE_KEY, String.valueOf(size + 1));
  }

  public void remove(final long position) {
    final long size = checkIndex(position);
    for (long move = size - 1; move >= position; move--) {
      setProperty(String.valueOf(move - 1), getProperty(String.valueOf(move)));
    }
    setProperty(SIZE_KEY, String.valueOf(size - 1));
  }
}
