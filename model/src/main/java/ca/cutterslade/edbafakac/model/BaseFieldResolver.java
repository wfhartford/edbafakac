package ca.cutterslade.edbafakac.model;

import javax.annotation.Nonnull;

abstract class BaseFieldResolver {

  public static final String UNRESOLVED_PREFIX = "unresolved:";

  boolean isUnresolved(final String value) {
    return null != value && value.startsWith(UNRESOLVED_PREFIX);
  }

  String getUnresolvedValue(@Nonnull final String value) {
    return value.substring(UNRESOLVED_PREFIX.length());
  }

  abstract String resolve(@Nonnull ValueService service, @Nonnull String value);
}
