package ca.cutterslade.edbafakac.db.search;

import java.util.Map;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.base.Predicate;

enum SearchTerms {
  ;

  private static final class SearchTermPredicate implements Predicate<Entry> {

    private final SearchTerm term;

    private final SearchService service;

    public SearchTermPredicate(@Nonnull final SearchTerm term, @Nonnull final SearchService service) {
      this.term = term;
      this.service = service;
    }

    @Override
    public boolean apply(final Entry input) {
      return null == input ? false : term.matches(input, service);
    }

  }

  private static final class EntryPredicate implements Predicate<SearchTerm> {

    private final Entry entry;

    private final SearchService service;

    public EntryPredicate(@Nonnull final Entry entry, @Nonnull final SearchService service) {
      this.entry = entry;
      this.service = service;
    }

    @Override
    public boolean apply(final SearchTerm input) {
      return null == input ? false : input.matches(entry, service);
    }

  }

  private static final class ResultMapPredicate implements Predicate<SearchTerm> {

    private final Map<? extends SearchTerm, Boolean> resultMap;

    public ResultMapPredicate(@Nonnull final Map<? extends SearchTerm, Boolean> resultMap) {
      this.resultMap = resultMap;
    }

    @Override
    public boolean apply(final SearchTerm input) {
      return resultMap.get(input).booleanValue();
    }

  }

  static Predicate<Entry> searchTermPredicate(@Nonnull final SearchTerm term,
      @Nonnull final SearchService service) {
    return new SearchTermPredicate(term, service);
  }

  static Predicate<SearchTerm> entryPredicate(@Nonnull final Entry entry, @Nonnull final SearchService service) {
    return new EntryPredicate(entry, service);
  }

  static Predicate<SearchTerm> resultMapPredicate(@Nonnull final Map<? extends SearchTerm, Boolean> resultMap) {
    return new ResultMapPredicate(resultMap);
  }

}
