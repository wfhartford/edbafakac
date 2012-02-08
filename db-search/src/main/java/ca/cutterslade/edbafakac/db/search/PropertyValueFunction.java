package ca.cutterslade.edbafakac.db.search;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.base.Function;

class PropertyValueFunction implements Function<String, String> {

  private final Entry entry;

  PropertyValueFunction(@Nonnull final Entry entry) {
    this.entry = entry;
  }

  @Override
  public String apply(final String input) {
    return null == input ? null : entry.getProperty(input);
  }

}
