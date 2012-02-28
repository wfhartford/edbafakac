package ca.cutterslade.edbafakac.db.search;

import java.util.Map;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntrySearchTerm;

import com.google.common.base.Predicate;

enum SearchTerms {
  ;

  private static final class SearchTermPredicate implements Predicate<Entry> {

    private final EntrySearchTerm term;

    private final EntrySearchService service;

    public SearchTermPredicate(@Nonnull final EntrySearchTerm term, @Nonnull final EntrySearchService service) {
      this.term = term;
      this.service = service;
    }

    @Override
    public boolean apply(final Entry input) {
      return null == input ? false : term.matches(input, service);
    }

  }

  private static final class EntryPredicate implements Predicate<EntrySearchTerm> {

    private final Entry entry;

    private final EntrySearchService service;

    public EntryPredicate(@Nonnull final Entry entry, @Nonnull final EntrySearchService service) {
      this.entry = entry;
      this.service = service;
    }

    @Override
    public boolean apply(final EntrySearchTerm input) {
      return null == input ? false : input.matches(entry, service);
    }

  }

  private static final class ResultMapPredicate implements Predicate<EntrySearchTerm> {

    private final Map<? extends EntrySearchTerm, Boolean> resultMap;

    public ResultMapPredicate(@Nonnull final Map<? extends EntrySearchTerm, Boolean> resultMap) {
      this.resultMap = resultMap;
    }

    @Override
    public boolean apply(final EntrySearchTerm input) {
      return resultMap.get(input).booleanValue();
    }

  }

  static Predicate<Entry> searchTermPredicate(@Nonnull final EntrySearchTerm term,
      @Nonnull final EntrySearchService service) {
    return new SearchTermPredicate(term, service);
  }

  static Predicate<EntrySearchTerm> entryPredicate(@Nonnull final Entry entry,
      @Nonnull final EntrySearchService service) {
    return new EntryPredicate(entry, service);
  }

  static Predicate<EntrySearchTerm> resultMapPredicate(
      @Nonnull final Map<? extends EntrySearchTerm, Boolean> resultMap) {
    return new ResultMapPredicate(resultMap);
  }

}
