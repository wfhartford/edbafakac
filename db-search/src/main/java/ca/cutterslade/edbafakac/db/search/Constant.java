package ca.cutterslade.edbafakac.db.search;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.NegatedEntrySearchTerm;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntrySearchTerm;

import com.google.common.collect.ImmutableList;

public enum Constant implements NegatedEntrySearchTerm {
  ANY_ENTRY, NO_ENTRY;

  private final ImmutableList<? extends EntrySearchTerm> list = ImmutableList.of(this);

  @Override
  public boolean matches(final Entry entry, final EntrySearchService service) {
    return this == ANY_ENTRY;
  }

  @Override
  public EntrySearchTerm getNegatedTerm() {
    return this == ANY_ENTRY ? NO_ENTRY : ANY_ENTRY;
  }

  protected ImmutableList<? extends EntrySearchTerm> asList() {
    return list;
  }

}
