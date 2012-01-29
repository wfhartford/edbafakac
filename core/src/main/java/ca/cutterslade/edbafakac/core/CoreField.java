package ca.cutterslade.edbafakac.core;

import ca.cutterslade.edbafakac.model.FieldValue;
import ca.cutterslade.edbafakac.model.InitialValue;
import ca.cutterslade.edbafakac.model.StringValue;
import ca.cutterslade.edbafakac.model.TypeValue;
import ca.cutterslade.edbafakac.model.Value;

public enum CoreField implements InitialValue {
  PARENT {

    @Override
    protected TypeValue getFieldType() {
    }
  };

  @Override
  public Value<?> getValue() {
    // TODO get already created value
    FieldValue value = null;
    if (null == value) {
      value = getInitialValue();
    }
    return value;
  }

  private FieldValue getInitialValue() {
    final FieldValue value = getFieldType().getNewField(StringValue.withBase(name(), false));
    initializeFieldValue(value);
    return value;
  }

  protected void initializeFieldValue(final FieldValue value) {
  }

  protected abstract TypeValue getFieldType();
}
