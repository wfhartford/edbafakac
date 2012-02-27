package ca.cutterslade.edbafakac.model;

import java.util.Arrays;

import com.google.common.collect.Iterables;

public class BaseValueProvider extends AbstractInitialValueProvider {

  @Override
  public int getPriority() {
    return 0;
  }

  @Override
  protected Iterable<? extends InitialValue> getInitialValues(final ValueService service) {
    return Iterables.concat(
        Arrays.asList(BaseType.values()),
        Arrays.asList(BaseField.values()),
        Arrays.asList(BaseValue.values()));
  }

}
