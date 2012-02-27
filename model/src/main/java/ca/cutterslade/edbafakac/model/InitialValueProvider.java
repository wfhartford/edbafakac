package ca.cutterslade.edbafakac.model;

public interface InitialValueProvider {

  int getPriority();

  Iterable<Value<?>> getValues(ValueService service);
}
