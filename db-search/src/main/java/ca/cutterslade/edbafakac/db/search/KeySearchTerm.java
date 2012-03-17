package ca.cutterslade.edbafakac.db.search;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntrySearchTerm;

public final class KeySearchTerm implements EntrySearchTerm {

  private final String key;

  public KeySearchTerm(@Nonnull final String key) {
    this.key = key;
  }

  @Override
  public boolean matches(final Entry entry, final EntrySearchService service) {
    return key.equals(entry.getKey());
  }

  public String getKey() {
    return key;
  }

}
