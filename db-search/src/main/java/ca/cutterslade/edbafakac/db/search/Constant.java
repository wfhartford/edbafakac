package ca.cutterslade.edbafakac.db.search;

import java.util.Map;

import ca.cutterslade.edbafakac.db.CompositeSearchTerm;
import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.NegatedSearchTerm;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.collect.ImmutableList;

enum Constant implements NegatedSearchTerm, CompositeSearchTerm {
  ANY_ENTRY, NO_ENTRY;

  private final ImmutableList<? extends SearchTerm> asList = ImmutableList.of(this);

  @Override
  public boolean matches(final Entry entry, final SearchService service) {
    return this == ANY_ENTRY;
  }

  @Override
  public SearchTerm getNegatedTerm() {
    return this == ANY_ENTRY ? NO_ENTRY : ANY_ENTRY;
  }

  @Override
  public Iterable<? extends SearchTerm> getComponents() {
    return asList;
  }

  @Override
  public boolean combine(final Map<? extends SearchTerm, Boolean> componentResults) {
    return this == ANY_ENTRY;
  }
}