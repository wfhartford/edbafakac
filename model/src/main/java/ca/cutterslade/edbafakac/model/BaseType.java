package ca.cutterslade.edbafakac.model;

import com.google.common.collect.ImmutableMap;

enum BaseType {
  RAW("0361bf6b-2203-4ea4-ae82-762a01eb4ead"),
  STRING("9fc6d0a8-5988-48a1-88a1-a90aea3f6fd3"),
  INTEGER("ccdf477d-b1c8-4910-b017-891a1ca53030"),
  DECIMAL("1ed1046e-c171-4cf7-86ee-6fe45e35c8a7"),
  DATE("48a98182-be1a-4039-932e-dc9e20f92dfa"),
  BOOLEAN("f13b77e7-212b-4afa-8ae4-4433d020a846"),
  LIST("ef61a2c8-a213-48bb-8400-24bde59f1c24"),
  FIELD("874c4eff-f577-4367-9e08-7dc6dc5f8949"),
  TYPE("4823897a-3f19-402e-99b2-42d43d71e399");

  private static final ImmutableMap<String, BaseType> TYPES_BY_KEY;
  static {
    final ImmutableMap.Builder<String, BaseType> builder = ImmutableMap.builder();
    for (final BaseType type : values()) {
      builder.put(type.getKey(), type);
    }
    TYPES_BY_KEY = builder.build();
  }

  private final String key;

  private BaseType(final String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public TypeValue getType() {
    return (TypeValue) Values.getValue(key, BaseType.class.getSimpleName() + '.' + toString() + ".properties");
  }

  static BaseType getBaseType(final String key) {
    return TYPES_BY_KEY.get(key);
  }
}
