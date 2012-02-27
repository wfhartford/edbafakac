package ca.cutterslade.edbafakac.model;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

public final class ListValue extends Value<ListValue> {

  private static final String TOO_LARGE_MESSAGE = "List would have > Long.MAX_VALUE elements";

  private static final String SIZE_KEY = "f90cb18e-413d-4f00-864a-4235da06f642";

  private static final String TYPE_KEY = "56ab8c1e-f86a-4617-b342-45a98926a814";

  ListValue(@Nonnull final ValueService service, @Nonnull final Entry entry,
      @Nonnull final RetrieveMode retrieveMode) {
    super(service, entry, retrieveMode);
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

  public ListValue setValueType(@Nullable final TypeValue type) {
    if (null == type) {
      removeProperty(TYPE_KEY);
    }
    else {
      Preconditions.checkState(0 == getSize(), "Type may only be set on an empty list");
      setProperty(TYPE_KEY, type.save().getKey());
    }
    return this;
  }

  ListValue setValueTypeRaw(@Nonnull final String key) {
    Preconditions.checkArgument(BaseType.FIELD.getKey().equals(key));
    Preconditions.checkState(0 == getSize(), "Type may only be set on an empty list");
    return setProperty(TYPE_KEY, key);
  }

  public TypeValue getValueType(@Nonnull final RetrieveMode retrieveMode) {
    final String typeKey = getProperty(TYPE_KEY);
    return (TypeValue) (null == typeKey ? null : getValueService().getValue(typeKey, retrieveMode));
  }

  public Value<?> get(final long position) {
    checkIndex(position);
    final String key = getProperty(String.valueOf(position));
    return getValueService().getValue(key, getRetrieveMode());
  }

  public ListValue set(final long position, @Nonnull final Value<?> value) {
    checkIndex(position);
    return checkValue(value).setProperty(String.valueOf(position), value.save().getKey());
  }

  private ListValue checkValue(@Nonnull final Value<?> value) {
    final TypeValue type = getValueType(RetrieveMode.READ_ONLY);
    if (null != type) {
      Preconditions.checkArgument(value.isInstance(type));
    }
    return this;
  }

  public ListValue add(@Nonnull final Value<?> value) {
    final long size = getSize();
    Preconditions.checkState(Long.MAX_VALUE > size, TOO_LARGE_MESSAGE);
    return checkValue(value)
        .setProperty(String.valueOf(size), value.save().getKey())
        .setProperty(SIZE_KEY, String.valueOf(size + 1));
  }

  public ListValue addAll(@Nonnull final Value<?>... values) {
    return addAll(Arrays.asList(values));
  }

  public ListValue addAll(@Nonnull final Iterable<? extends Value<?>> values) {
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

  ListValue addRawValue(@Nonnull final String value) {
    final long size = getSize();
    Preconditions.checkArgument(Long.MAX_VALUE > size, TOO_LARGE_MESSAGE);
    return setProperty(String.valueOf(size), value).
        setProperty(SIZE_KEY, String.valueOf(size + 1));
  }

  public ListValue insert(final long position, @Nonnull final Value<?> value) {
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

  public long indexOf(@Nonnull final Value<?> value) {
    final String key = value.getKey();
    long index = -1;
    final long size = getSize();
    for (long i = 0; -1 == index && i < size; i++) {
      if (key.equals(getProperty(String.valueOf(i)))) {
        index = i;
      }
    }
    return index;
  }

  public boolean contains(@Nonnull final Value<?> value) {
    return -1 != indexOf(value);
  }

}
