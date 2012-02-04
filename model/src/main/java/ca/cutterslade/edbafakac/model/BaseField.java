package ca.cutterslade.edbafakac.model;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

enum BaseField implements InitialValue {
  VALUE_NAME("619c10d4-6d66-43d3-ab50-35713f603426", new BaseFieldResolver() {

    @Override
    String resolve(final String value) {
      return StringValue.withBase(getUnresolvedValue(value), false).save(true).getKey();
    }
  }),
  VALUE_TYPE("bb1ba5f1-0914-474f-94d9-3e2372a88012", null),
  VALUE_CLASS("4da490d4-74ee-450b-ba78-5ec4c34182df", null),
  FIELD_TYPE("ca6ef65b-368e-4daf-a671-75ccefea814e", null),
  TYPE_FIELDS("57cf2358-235f-43f4-8a56-436b419029be", new BaseFieldResolver() {

    @Override
    String resolve(final String value) {
      final ListValue resolvedValue = (ListValue) Types.getListType().getNewValue(null);
      resolvedValue.setValueTypeRaw(BaseType.FIELD.getKey());
      for (final String typeKey : getUnresolvedValue(value).split(",")) {
        final String trimmedKey = typeKey.trim();
        if (!trimmedKey.isEmpty()) {
          resolvedValue.addRawValue(trimmedKey);
        }
      }
      return resolvedValue.save(true).getKey();
    }
  }),
  TYPE_CLASS("1c979df8-f291-4d1e-b020-8ec7f77e04b4", null);

  private static final ImmutableMap<String, BaseField> FIELDS_BY_KEY;
  static {
    final ImmutableMap.Builder<String, BaseField> builder = ImmutableMap.builder();
    for (final BaseField field : values()) {
      builder.put(field.getKey(), field);
    }
    FIELDS_BY_KEY = builder.build();
  }

  private final String key;

  private final BaseFieldResolver resolver;

  private BaseField(@Nonnull final String key, final BaseFieldResolver resolver) {
    this.key = key;
    this.resolver = resolver;
  }

  @Override
  public FieldValue getValue() {
    return (FieldValue) Values.getValue(key, BaseField.class.getSimpleName() + '.' + toString() + ".properties");
  }

  public String getKey() {
    return key;
  }

  static BaseField getBaseField(@Nonnull final String key) {
    return FIELDS_BY_KEY.get(key);
  }

  static ImmutableSet<String> getBaseFieldKeys() {
    return FIELDS_BY_KEY.keySet();
  }

  static BaseFieldResolver getResolver(@Nonnull final String key) {
    final BaseField baseField = FIELDS_BY_KEY.get(key);
    return null == baseField ? null : baseField.getResolver();
  }

  BaseFieldResolver getResolver() {
    return resolver;
  }
}
