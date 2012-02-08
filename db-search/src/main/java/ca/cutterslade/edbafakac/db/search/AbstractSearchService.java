package ca.cutterslade.edbafakac.db.search;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.NegatedSearchTerm;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public abstract class AbstractSearchService implements SearchService {

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

  private final EntryService entryService;

  protected AbstractSearchService(final EntryService entryService) {
    this.entryService = entryService;
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
    return Iterables.any(terms, Predicates.<SearchTerm> equalTo(Constant.NO_ENTRY)) ?
        Constant.NO_ENTRY : new AndSearchTerm(terms);
  }

  @Override
  public SearchTerm or(final SearchTerm... terms) {
    return or(ImmutableSet.copyOf(terms));
  }

  @Override
  public SearchTerm or(final Iterable<? extends SearchTerm> terms) {
    return Iterables.any(terms, Predicates.<SearchTerm> equalTo(Constant.ANY_ENTRY)) ?
        Constant.ANY_ENTRY : new OrSearchTerm(terms);
  }

  @Override
  public SearchTerm not(final SearchTerm term) {
    return term instanceof NegatedSearchTerm ? ((NegatedSearchTerm) term).getNegatedTerm() : new NotSearchTerm(term);
  }

  @Override
  public SearchTerm referencesMatch(final String fieldKey, final SearchTerm term) {
    return new ReferencesMatchSearchTerm(fieldKey, term);
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
