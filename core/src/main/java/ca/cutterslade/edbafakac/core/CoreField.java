package ca.cutterslade.edbafakac.core;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.model.FieldValue;
import ca.cutterslade.edbafakac.model.InitialValue;
import ca.cutterslade.edbafakac.model.RetrieveMode;
import ca.cutterslade.edbafakac.model.TypeValue;
import ca.cutterslade.edbafakac.model.Value;
import ca.cutterslade.edbafakac.model.ValueService;

public enum CoreField implements InitialValue {
  PARENT("e7836205-1d28-487c-a6ed-1fd7a08ed839") {

    @Override
    protected TypeValue getFieldType() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("getFieldType has not been implemented");
    }

  };

  private final String key;

  private CoreField(@Nonnull final String key) {
    this.key = key;
  }

  @Override
  public Value<?> getValue(final ValueService service) {
    FieldValue value;
    try {
      value = service.getField(key, RetrieveMode.READ_ONLY);
    }
    catch (final EntryNotFoundException e) {
      value = getInitialValue(service);
    }
    return value;
  }

  private FieldValue getInitialValue(final ValueService service) {
    final FieldValue value = getFieldType().getNewField(service.stringWithBase(name(), false));
    initializeFieldValue(value);
    return value;
  }

  protected void initializeFieldValue(final FieldValue value) {
  }

  protected abstract TypeValue getFieldType();
}
