package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;

public final class ListValue extends Value {

  private static final String SIZE_KEY = "f90cb18e-413d-4f00-864a-4235da06f642";

  private static final String TYPE_KEY = "56ab8c1e-f86a-4617-b342-45a98926a814";

  ListValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
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

  public void setValueType(final TypeValue type) {
    if (null == type) {
      removeProperty(TYPE_KEY);
    }
    else {
      Preconditions.checkState(0 == getSize(), "Type may only be set on an empty list");
      setProperty(TYPE_KEY, type.getKey());
    }
  }

  public TypeValue getValueType(final boolean readOnly) {
    final String typeKey = getProperty(TYPE_KEY);
    return (TypeValue) (null == typeKey ? null : Values.getValue(typeKey, readOnly));
  }

  public Value get(final long position) {
    checkIndex(position);
    final String key = getProperty(String.valueOf(position));
    return Values.getValue(key, isReadOnly());
  }

  public void set(final long position, final Value value) {
    checkIndex(position);
    checkValue(value);
    setProperty(String.valueOf(position), value.getKey());
  }

  private void checkValue(final Value value) {
    Preconditions.checkArgument(null != value);
    final TypeValue type = getValueType(true);
    if (null != type) {
      Preconditions.checkArgument(value.isInstance(type));
    }
  }

  public void add(final Value value) {
    setProperty(String.valueOf(getSize()), value.getKey());
    setProperty(SIZE_KEY, String.valueOf(getSize() + 1));
  }

  void addRawValue(final String value) {
    setProperty(String.valueOf(getSize()), value);
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
