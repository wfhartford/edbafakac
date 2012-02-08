package ca.cutterslade.edbafakac.db.search;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.NegatedSearchTerm;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

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

  private static final Function<SearchTerm, Iterable<? extends SearchTerm>> AND_EXPLODER =
      new Function<SearchTerm, Iterable<? extends SearchTerm>>() {

        @Override
        public Iterable<? extends SearchTerm> apply(final SearchTerm input) {
          return null == input ? Constant.NO_ENTRY.getComponents() :
              input instanceof AndSearchTerm ? ((AndSearchTerm) input).getComponents() : ImmutableList.of(input);
        }
      };

  private static final Function<SearchTerm, Iterable<? extends SearchTerm>> OR_EXPLODER =
      new Function<SearchTerm, Iterable<? extends SearchTerm>>() {

        @Override
        public Iterable<? extends SearchTerm> apply(final SearchTerm input) {
          return null == input ? Constant.NO_ENTRY.getComponents() :
              input instanceof OrSearchTerm ? ((OrSearchTerm) input).getComponents() : ImmutableList.of(input);
        }
      };

  public static Predicate<Entry> searchTermPredicate(@Nonnull final SearchTerm term,
      @Nonnull final SearchService service) {
    return new SearchTermPredicate(term, service);
  }

  public static Predicate<SearchTerm> entryPredicate(@Nonnull final Entry entry, @Nonnull final SearchService service) {
    return new EntryPredicate(entry, service);
  }

  public static Predicate<SearchTerm> resultMapPredicate(@Nonnull final Map<? extends SearchTerm, Boolean> resultMap) {
    return new ResultMapPredicate(resultMap);
  }

  public static SearchTerm not(@Nonnull final SearchTerm term) {
    return term instanceof NegatedSearchTerm ? ((NegatedSearchTerm) term).getNegatedTerm() : new NotSearchTerm(term);
  }

  public static SearchTerm and(@Nonnull final SearchTerm... terms) {
    return and(Arrays.asList(terms));
  }

  public static SearchTerm and(@Nonnull final Iterable<SearchTerm> terms) {
    return Iterables.isEmpty(terms) ? Constant.ANY_ENTRY :
        new AndSearchTerm(Iterables.concat(Iterables.transform(terms, AND_EXPLODER)));
  }

  public static SearchTerm or(@Nonnull final SearchTerm... terms) {
    return or(Arrays.asList(terms));
  }

  public static SearchTerm or(@Nonnull final Iterable<SearchTerm> terms) {
    return Iterables.isEmpty(terms) ? Constant.NO_ENTRY :
        new OrSearchTerm(Iterables.concat(Iterables.transform(terms, OR_EXPLODER)));
  }

  public static SearchTerm fieldValue(final String fieldKey, final String value) {
    return fieldsWithValues(ImmutableSet.of(value), ImmutableSet.of(fieldKey));
  }

  public static SearchTerm fieldsWithValues(final Iterable<String> fieldKeys, final Iterable<String> values) {
    return new FieldValueSearchTerm(fieldKeys, values);
  }
}
