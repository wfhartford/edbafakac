package ca.cutterslade.edbafakac.model;

import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class BaseValueProvider implements InitialValueProvider {

  private static final Function<BaseType, Value<?>> BASE_TYPE_VALUE_FUNCTION = new Function<BaseType, Value<?>>() {

    @Override
    public Value<?> apply(final BaseType input) {
      return input.getType();
    }
  };

  private static final Function<BaseField, Value<?>> BASE_FIELD_VALUE_FUNCTION = new Function<BaseField, Value<?>>() {

    @Override
    public Value<?> apply(final BaseField input) {
      return input.getField();
    }
  };

  private static final Function<BaseValue, Value<?>> BASE_VALUE_VALUE_FUNCTION = new Function<BaseValue, Value<?>>() {

    @Override
    public Value<?> apply(final BaseValue input) {
      return input.getValue();
    }
  };

  @Override
  public int getPriority() {
    return 0;
  }

  @Override
  public Iterable<Value<?>> getInitialValues() {
    return Iterables.concat(
        Iterables.transform(Arrays.asList(BaseType.values()), BASE_TYPE_VALUE_FUNCTION),
        Iterables.transform(Arrays.asList(BaseField.values()), BASE_FIELD_VALUE_FUNCTION),
        Iterables.transform(Arrays.asList(BaseValue.values()), BASE_VALUE_VALUE_FUNCTION));
  }

}
