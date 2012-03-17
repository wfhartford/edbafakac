package ca.cutterslade.edbafakac.model;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntrySearchTerm;

import com.google.common.base.Preconditions;

public final class FieldValue extends Value<FieldValue> {

  FieldValue(@Nonnull final ValueService service, @Nonnull final Entry entry,
      @Nonnull final RetrieveMode retrieveMode) {
    super(service, entry, retrieveMode);
  }

  public TypeValue getFieldType(@Nonnull final RetrieveMode retrieveMode) {
    return (TypeValue) BaseField.FIELD_TYPE.getValue(getValueService()).getValue(this, retrieveMode);
  }

  public Value<?> getValue(@Nonnull final Value<?> value, @Nonnull final RetrieveMode retrieveMode) {
    Preconditions.checkArgument(RetrieveMode.READ_ONLY == retrieveMode || !value.isReadOnly(),
        "Cannot provide writable field value from read only value");
    checkValueFields(value);
    final String rawValue = getRawValue(value);
    return null == rawValue ? null : getValueService().getValue(rawValue, retrieveMode);
  }

  private void checkValueFields(@Nonnull final Value<?> value) {
    // Check that the value could possibly contain the field
    // we have to omit a few things from this check because the could cause a stack overflow

    // Everything has a VALUE_TYPE field
    if (!equals(BaseField.VALUE_TYPE.getValue(getValueService())) &&
        // Types all have the fields field
        !(equals(BaseField.TYPE_FIELDS.getValue(getValueService())) &&
        value.isInstance(BaseType.TYPE.getValue(getValueService()))) &&
        // Check that the value's list of fields contains this field
        !value.getFields(RetrieveMode.READ_ONLY).contains(this)) {
      throw new IllegalArgumentException("Value does not contain specified field");
    }
  }

  public FieldValue setValue(@Nonnull final Value<?> targetValue, @Nonnull final Value<?> fieldValue) {
    checkValueFields(targetValue);
    final TypeValue fieldType = save().getFieldType(RetrieveMode.READ_ONLY);
    if (getKey().equals(BaseField.VALUE_TYPE.getKey())) {
      throw new IllegalArgumentException("Cannot set value of the value type field");
    }
    if (!fieldValue.isInstance(fieldType)) {
      throw new IllegalArgumentException("Cannot set value of a " +
          fieldType.getName(RetrieveMode.READ_ONLY).getBaseValue() +
          " field to a " + fieldValue.getType(RetrieveMode.READ_ONLY).getName(RetrieveMode.READ_ONLY).getBaseValue());
    }
    if (isUnique()) {
      final EntrySearchService searchService = getValueService().getEntryService().getSearchService();
      final EntrySearchTerm fieldWithKey = searchService.propertyValue(getKey(), fieldValue.getKey());
      final EntrySearchTerm sameType = searchService
          .propertyValue(BaseField.VALUE_TYPE.getKey(), targetValue.getProperty(BaseField.VALUE_TYPE.getKey()));
      final EntrySearchTerm notSameEntry = searchService.not(searchService.key(targetValue.getKey()));
      final EntrySearchTerm uniqueViolation = searchService.and(fieldWithKey, sameType, notSameEntry);
      if (searchService.searchForMatch(uniqueViolation)) {
        throw new IllegalArgumentException("Value violates field uniqueness");
      }
    }
    return setRawValue(targetValue.save(), fieldValue.save().getKey());
  }

  public boolean isUnique() {
    return BaseValue.BOOLEAN_TRUE.getKey().equals(getProperty(BaseField.UNIQUE.getKey()));
  }

  String getRawValue(@Nonnull final Value<?> value) {
    return value.getProperty(getKey());
  }

  FieldValue setRawValue(@Nonnull final Value<?> targetValue, @Nonnull final String rawValue) {
    targetValue.save().setProperty(getKey(), rawValue);
    return this;
  }
}
