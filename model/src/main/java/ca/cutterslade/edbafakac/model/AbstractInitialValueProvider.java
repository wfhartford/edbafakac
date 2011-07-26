package ca.cutterslade.edbafakac.model;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public abstract class AbstractInitialValueProvider implements InitialValueProvider {

  private static final Function<InitialValue, Value<?>> VALUE_FUNCTION = new Function<InitialValue, Value<?>>() {

    @Override
    public Value<?> apply(final InitialValue input) {
      return input.getValue();
    }
  };

  @Override
  public Iterable<Value<?>> getValues() {
    return Iterables.transform(getInitialValues(), VALUE_FUNCTION);
  }

  protected abstract Iterable<? extends InitialValue> getInitialValues();
}
