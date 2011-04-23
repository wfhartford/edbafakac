package ca.cutterslade.edbafakac.db;

public enum BasicField {
  ID("Object ID", BasicType.OBJECT_ID, "_id"),
  NAME("Name", BasicType.STRING, "6e9d37d4-803f-4ab2-aee8-1455ad3b25d9"),
  TYPE("Type", BasicType.TYPE, "cbc1377a-549f-42ec-9962-d9585073765e"),
  FIELD_TYPE("Field Type", BasicType.TYPE, "ca2f68ce-ea4a-42f9-994e-acbe39859b6b"),
  KEY("Key", BasicType.STRING, "420eddc0-d69b-48f6-af89-9038c54c5a1b");

  private final String name;

  private final BasicType type;

  private final String key;

  private BasicField(final String name, final BasicType type, final String key) {
    this.name = name;
    this.type = type;
    this.key = key;
  }
}
