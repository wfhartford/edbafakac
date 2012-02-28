package ca.cutterslade.edbafakac.model;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public abstract class AbstractInitialValueProvider implements InitialValueProvider {

  private static final class InitialValueFunction implements Function<InitialValue, Value<?>> {

    private final ValueService service;

    public InitialValueFunction(final ValueService service) {
      this.service = service;
    }

    @Override
    public Value<?> apply(final InitialValue input) {
      if (null == input) {
        throw new IllegalArgumentException();
      }
      return input.getValue(service);
    }
  }

  @Override
  public Iterable<Value<?>> getValues(final ValueService service) {
    return Iterables.transform(getInitialValues(service), new InitialValueFunction(service));
  }

  protected abstract Iterable<? extends InitialValue> getInitialValues(ValueService service);
}
