package ca.cutterslade.edbafakac.model;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;

enum BaseValue implements InitialValue {
  BOOLEAN_FALSE("9b585c88-0de0-45e1-b5e1-d1208de3c558"),
  BOOLEAN_TRUE("00513937-8727-4e99-8786-e433b409b3a5");

  private static final ImmutableMap<String, BaseValue> VALUES_BY_KEY;
  static {
    final ImmutableMap.Builder<String, BaseValue> builder = ImmutableMap.builder();
    for (final BaseValue value : values()) {
      builder.put(value.getKey(), value);
    }
    VALUES_BY_KEY = builder.build();
  }

  private final String key;

  private BaseValue(@Nonnull final String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  @Override
  public Value<?> getValue(final ValueService service) {
    return service.getValue(key, BaseValue.class.getSimpleName() + '.' + toString() + ".properties");
  }

  public static BaseValue getBaseValue(@Nonnull final String key) {
    return VALUES_BY_KEY.get(key);
  }
}
