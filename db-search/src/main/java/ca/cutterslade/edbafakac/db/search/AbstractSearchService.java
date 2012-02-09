package ca.cutterslade.edbafakac.db.search;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.NegatedSearchTerm;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public abstract class AbstractSearchService<T extends EntryService> implements SearchService {

  private static final Function<NegatedSearchTerm, SearchTerm> NEGATED_TERM_FUNCTION =
      new Function<NegatedSearchTerm, SearchTerm>() {

        @Override
        public SearchTerm apply(final NegatedSearchTerm input) {
          return null == input ? Constant.ANY_ENTRY : input.getNegatedTerm();
        }
      };

  private static final Function<SearchTerm, Iterable<? extends SearchTerm>> AND_EXPLODER =
      new Function<SearchTerm, Iterable<? extends SearchTerm>>() {

        @Override
        public Iterable<? extends SearchTerm> apply(final SearchTerm input) {
          return null == input ? Constant.NO_ENTRY.asList() :
              input instanceof AndSearchTerm ? ((AndSearchTerm) input).getComponents() : ImmutableList.of(input);
        }
      };

  private static final Function<SearchTerm, Iterable<? extends SearchTerm>> OR_EXPLODER =
      new Function<SearchTerm, Iterable<? extends SearchTerm>>() {

        @Override
        public Iterable<? extends SearchTerm> apply(final SearchTerm input) {
          return null == input ? Constant.NO_ENTRY.asList() :
              input instanceof OrSearchTerm ? ((OrSearchTerm) input).getComponents() : ImmutableList.of(input);
        }
      };

  private final Function<String, Entry> lookup = new Function<String, Entry>() {

    @Override
    public Entry apply(final String input) {
      Entry entry;
      if (null == input) {
        entry = null;
      }
      else {
        try {
          entry = entryService.getEntry(input);
        }
        catch (final EntryNotFoundException e) {
          entry = null;
        }
      }
      return entry;
    }
  };

  private final T entryService;

  protected AbstractSearchService(final T entryService) {
    this.entryService = entryService;
  }

  protected T getEntryService() {
    return entryService;
  }

  @Override
  public Iterable<Entry> searchForEntries(final SearchTerm term) {
    return Iterables.filter(Iterables.transform(searchForKeys(term), lookup), Predicates.notNull());
  }

  @Override
  public SearchTerm and(final SearchTerm... terms) {
    return and(ImmutableSet.copyOf(terms));
  }

  @Override
  public SearchTerm and(final Iterable<? extends SearchTerm> terms) {
    final Iterable<SearchTerm> exploded = Iterables.concat(Iterables.transform(terms, AND_EXPLODER));
    final Iterable<SearchTerm> filtered = Iterables.filter(exploded,
        Predicates.not(Predicates.<SearchTerm> equalTo(Constant.ANY_ENTRY)));
    final ImmutableSet<SearchTerm> simplified = ImmutableSet.copyOf(filtered);
    final SearchTerm term;
    if (simplified.contains(Constant.NO_ENTRY)) {
      term = Constant.NO_ENTRY;
    }
    else if (1 == simplified.size()) {
      term = Iterables.getOnlyElement(simplified);
    }
    else if (containsSelfNegation(simplified)) {
      term = Constant.NO_ENTRY;
    }
    else {
      term = new AndSearchTerm(simplified);
    }
    return term;
  }

  @Override
  public SearchTerm or(final SearchTerm... terms) {
    return or(ImmutableSet.copyOf(terms));
  }

  @Override
  public SearchTerm or(final Iterable<? extends SearchTerm> terms) {
    final Iterable<SearchTerm> exploded = Iterables.concat(Iterables.transform(terms, OR_EXPLODER));
    final Iterable<SearchTerm> filtered = Iterables.filter(exploded,
        Predicates.not(Predicates.<SearchTerm> equalTo(Constant.NO_ENTRY)));
    final ImmutableSet<SearchTerm> simplified = ImmutableSet.copyOf(filtered);
    final SearchTerm term;
    if (simplified.contains(Constant.ANY_ENTRY)) {
      term = Constant.ANY_ENTRY;
    }
    else if (1 == simplified.size()) {
      term = Iterables.getOnlyElement(simplified);
    }
    else if (containsSelfNegation(simplified)) {
      term = Constant.ANY_ENTRY;
    }
    else {
      term = new OrSearchTerm(simplified);
    }
    return term;
  }

  private boolean containsSelfNegation(final ImmutableSet<SearchTerm> simplified) {
    final Iterable<SearchTerm> negatedTerms = Iterables.transform(
        Iterables.filter(simplified, NegatedSearchTerm.class),
        NEGATED_TERM_FUNCTION);
    return Iterables.any(negatedTerms, Predicates.in(simplified));
  }

  @Override
  public SearchTerm not(final SearchTerm term) {
    return term instanceof NegatedSearchTerm ? ((NegatedSearchTerm) term).getNegatedTerm() : new NotSearchTerm(term);
  }

  @Override
  public SearchTerm referencesMatch(final String fieldKey, final SearchTerm term) {
    return referencesMatch(ImmutableSet.of(fieldKey), term);
  }

  @Override
  public SearchTerm referencesMatch(final Iterable<String> fieldKeys, final SearchTerm term) {
    return new ReferencesMatchSearchTerm(fieldKeys, term);
  }

  @Override
  public SearchTerm fieldValue(final String fieldKey, final String... values) {
    return fieldValue(fieldKey, ImmutableSet.copyOf(values));
  }

  @Override
  public SearchTerm fieldValue(final String fieldKey, final Iterable<String> values) {
    return fieldValue(ImmutableSet.of(fieldKey), values);
  }

  @Override
  public SearchTerm fieldValue(final Iterable<String> fieldKeys, final Iterable<String> values) {
    return new FieldValueSearchTerm(fieldKeys, values);
  }

}
