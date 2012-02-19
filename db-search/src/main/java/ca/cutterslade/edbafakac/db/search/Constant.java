package ca.cutterslade.edbafakac.db.search;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.NegatedSearchTerm;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.collect.ImmutableList;

public enum Constant implements NegatedSearchTerm {
  ANY_ENTRY, NO_ENTRY;

  private final ImmutableList<? extends SearchTerm> list = ImmutableList.of(this);

  @Override
  public boolean matches(final Entry entry, final SearchService service) {
    return this == ANY_ENTRY;
  }

  @Override
  public SearchTerm getNegatedTerm() {
    return this == ANY_ENTRY ? NO_ENTRY : ANY_ENTRY;
  }

  protected ImmutableList<? extends SearchTerm> asList() {
    return list;
  }

}
