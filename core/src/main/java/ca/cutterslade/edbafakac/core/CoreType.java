package ca.cutterslade.edbafakac.core;

import ca.cutterslade.edbafakac.model.InitialValue;
import ca.cutterslade.edbafakac.model.Value;
import ca.cutterslade.edbafakac.model.ValueService;

public enum CoreType implements InitialValue {
  USER,
  USER_GROUP,
  LABEL,
  MENU_ITEM,
  MENU_GROUP,
  INPUT_PROMPT,
  INPUT_PROMPT_GROUP,
  INPUT_FORM,
  SEARCH_PROMPT,
  SEARCH_FORM,
  LIST_COLUMN;

  @Override
  public Value<?> getValue(final ValueService service) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("getValue has not been implemented");
  }
}
