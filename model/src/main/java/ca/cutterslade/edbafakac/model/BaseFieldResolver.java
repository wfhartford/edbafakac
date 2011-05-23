package ca.cutterslade.edbafakac.model;

abstract class BaseFieldResolver {

  public static final String UNRESOLVED_PREFIX = "unresolved:";

  boolean isUnresolved(final String value) {
    return null != value && value.startsWith(UNRESOLVED_PREFIX);
  }

  String getUnresolvedValue(final String value) {
    return value.substring(UNRESOLVED_PREFIX.length());
  }

  abstract String resolve(String value);
}
