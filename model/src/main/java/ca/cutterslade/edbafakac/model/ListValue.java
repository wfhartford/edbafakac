package ca.cutterslade.edbafakac.model;

import java.util.Arrays;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public final class ListValue extends Value<ListValue> {

  private static final String TOO_LARGE_MESSAGE = "List would have > Long.MAX_VALUE elements";

  private static final String SIZE_KEY = "f90cb18e-413d-4f00-864a-4235da06f642";

  private static final String TYPE_KEY = "56ab8c1e-f86a-4617-b342-45a98926a814";

  ListValue(final Entry entry, final boolean readOnly) {
    super(entry, readOnly);
  }

  public static ListValue ofValues() {
    return (ListValue) BaseType.LIST.getType().getNewValue(null);
  }

  public static ListValue ofType(final TypeValue type) {
    return ofValues().setValueType(type.save());
  }

  public static ListValue ofValues(final Value<?>... values) {
    return ofValues().addAll(values);
  }

  public static ListValue ofValues(final Iterable<? extends Value<?>> values) {
    return ofValues().addAll(values);
  }

  public static ListValue ofValues(final TypeValue type, final Iterable<? extends Value<?>> values) {
    return ofType(type).addAll(values);
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

  public ListValue setValueType(final TypeValue type) {
    if (null == type) {
      removeProperty(TYPE_KEY);
    }
    else {
      Preconditions.checkState(0 == getSize(), "Type may only be set on an empty list");
      setProperty(TYPE_KEY, type.save().getKey());
    }
    return this;
  }

  ListValue setValueTypeRaw(final String key) {
    Preconditions.checkArgument(null != key);
    Preconditions.checkArgument(BaseType.FIELD.getKey().equals(key));
    Preconditions.checkState(0 == getSize(), "Type may only be set on an empty list");
    return setProperty(TYPE_KEY, key);
  }

  public TypeValue getValueType(final boolean readOnly) {
    final String typeKey = getProperty(TYPE_KEY);
    return (TypeValue) (null == typeKey ? null : Values.getValue(typeKey, readOnly));
  }

  public Value<?> get(final long position) {
    checkIndex(position);
    final String key = getProperty(String.valueOf(position));
    return Values.getValue(key, isReadOnly());
  }

  public ListValue set(final long position, final Value<?> value) {
    checkIndex(position);
    return checkValue(value).setProperty(String.valueOf(position), value.save().getKey());
  }

  private ListValue checkValue(final Value<?> value) {
    Preconditions.checkArgument(null != value);
    final TypeValue type = getValueType(true);
    if (null != type) {
      Preconditions.checkArgument(value.isInstance(type));
    }
    return this;
  }

  public ListValue add(final Value<?> value) {
    final long size = getSize();
    Preconditions.checkState(Long.MAX_VALUE > size, TOO_LARGE_MESSAGE);
    return checkValue(value)
        .setProperty(String.valueOf(size), value.save().getKey())
        .setProperty(SIZE_KEY, String.valueOf(size + 1));
  }

  public ListValue addAll(final Value<?>... values) {
    return addAll(Arrays.asList(values));
  }

  public ListValue addAll(final Iterable<? extends Value<?>> values) {
    long size = getSize();
    Preconditions.checkState(Long.MAX_VALUE - Iterables.size(values) > size, TOO_LARGE_MESSAGE);
    for (final Value<?> value : values) {
      checkValue(value);
    }
    for (final Value<?> value : values) {
      setProperty(String.valueOf(size++), value.save().getKey());
    }
    return setProperty(SIZE_KEY, String.valueOf(size));
  }

  ListValue addRawValue(final String value) {
    final long size = getSize();
    Preconditions.checkArgument(Long.MAX_VALUE > size, TOO_LARGE_MESSAGE);
    return setProperty(String.valueOf(size), value).
        setProperty(SIZE_KEY, String.valueOf(size + 1));
  }

  public ListValue insert(final long position, final Value<?> value) {
    final long size = getSize();
    Preconditions.checkArgument(Long.MAX_VALUE > size, TOO_LARGE_MESSAGE);
    checkValue(value);
    if (0 > position || position > size) {
      throw new IndexOutOfBoundsException(String.valueOf(position));
    }
    for (long move = size; move > position; move--) {
      setProperty(String.valueOf(move), getProperty(String.valueOf(move - 1)));
    }
    return setProperty(String.valueOf(position), value.save().getKey()).
        setProperty(SIZE_KEY, String.valueOf(size + 1));
  }

  public ListValue remove(final long position) {
    final long size = checkIndex(position) - 1;
    for (long move = position; move < size; move++) {
      setProperty(String.valueOf(move), getProperty(String.valueOf(move + 1)));
    }
    return removeProperty(String.valueOf(size)).setProperty(SIZE_KEY, String.valueOf(size));
  }

}
