package ca.cutterslade.edbafakac.db;

import org.bson.types.ObjectId;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public enum BasicType {
  INTERNAL(false) {

    @Override
    public boolean isInstance(final Object o) {
      return false;
    }
  },
  STRING(false) {

    @Override
    public boolean isInstance(final Object o) {
      return o instanceof String;
    }

  },
  OBJECT_ID(false) {

    @Override
    public boolean isInstance(final Object o) {
      return o instanceof ObjectId;
    }

  },
  TYPE(true, BasicField.CONVERTER, BasicField.TYPE_FIELDS),
  COLLECTION(true, BasicField.FIELD_TYPE, BasicField.COLLECTION_VALUES),
  FIELD(true, BasicField.FIELD_KEY, BasicField.FIELD_TYPE),
  ACTION(true);

  private final boolean entryType;

  private final ImmutableSet<BasicField> fields;

  private BasicType(final boolean entryType, final BasicField... fields) {
    Preconditions.checkArgument(!entryType || null == fields || 0 == fields.length);
    this.entryType = entryType;
    this.fields = null == fields ? ImmutableSet.<BasicField> of() : ImmutableSet.copyOf(fields);
  }

  public boolean isEntryType() {
    return entryType;
  }

  public Type<?> getType(final Configuration configuration) {
    return Database.getExistingInstance(configuration).getType(name());

  }

  static boolean isEntryType(final Type<?> type) {
    try {
      final BasicType basic = valueOf(type.getName());
      return basic.isEntryType();
    }
    catch (final IllegalArgumentException e) {
      return true;
    }
  }

  @Override
  public String toString() {
    return "BasicType." + name();
  }

  public boolean isInstance(final Object o) {
    throw new UnsupportedOperationException("isInstance is not supported for entry types");
  }
}
