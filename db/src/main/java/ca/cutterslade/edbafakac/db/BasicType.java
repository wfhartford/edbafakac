package ca.cutterslade.edbafakac.db;

public enum BasicType {
  STRING(false),
  OBJECT_ID(false),
  TYPE(true),
  COLLECTION(true),
  FIELD(true),
  ACTION(true);

  private final boolean entryType;

  private BasicType(final boolean entryType) {
    this.entryType = entryType;
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
}
