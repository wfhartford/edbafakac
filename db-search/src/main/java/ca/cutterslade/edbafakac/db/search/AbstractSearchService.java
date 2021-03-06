package ca.cutterslade.edbafakac.db.search;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.CompositeEntrySearchTerm;
import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntrySearchTerm;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.NegatedEntrySearchTerm;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class AbstractSearchService<T extends EntryService> implements EntrySearchService {

  private final class SearchTermKeyPredicate implements Predicate<String> {

    private final EntrySearchTerm term;

    public SearchTermKeyPredicate(@Nonnull final EntrySearchTerm term) {
      this.term = term;
    }

    @Override
    public boolean apply(final String input) {
      return null == input ? false : term.matches(lookup.apply(input), AbstractSearchService.this);
    }

  }

  protected abstract class SearchFunction<S extends EntrySearchTerm> implements
      Function<EntrySearchTerm, Iterable<String>> {

    private final Class<S> termType;

    public SearchFunction(final Class<S> termType) {
      this.termType = termType;
    }

    @Override
    public Iterable<String> apply(final EntrySearchTerm input) {
      return null == input ? getNoKeys() : executeSearch(termType.cast(input));
    }

    protected abstract Iterable<String> executeSearch(@Nonnull S cast);

  }

  protected final class ConstantSearchFunction extends SearchFunction<Constant> {

    public ConstantSearchFunction() {
      super(Constant.class);
    }

    @Override
    protected Iterable<String> executeSearch(final Constant input) {
      return executeConstantSearchTerm(input);
    }
  }

  protected final class KeySearchFunction extends SearchFunction<KeySearchTerm> {

    public KeySearchFunction() {
      super(KeySearchTerm.class);
    }

    @Override
    protected Iterable<String> executeSearch(final KeySearchTerm input) {
      return executeKeySearchTerm(input);
    }
  }

  protected final class FieldValueSearchFunction extends SearchFunction<FieldValueSearchTerm> {

    public FieldValueSearchFunction() {
      super(FieldValueSearchTerm.class);
    }

    @Override
    protected Iterable<String> executeSearch(final FieldValueSearchTerm input) {
      return executeFieldValueSearch(input);
    }
  }

  protected final class ReferencesMatchSearchFunction extends SearchFunction<ReferencesMatchSearchTerm> {

    public ReferencesMatchSearchFunction() {
      super(ReferencesMatchSearchTerm.class);
    }

    @Override
    protected Iterable<String> executeSearch(final ReferencesMatchSearchTerm input) {
      return executeReferencesMatchSearch(input);
    }
  }

  protected final class AndSearchFunction extends SearchFunction<AndSearchTerm> {

    public AndSearchFunction() {
      super(AndSearchTerm.class);
    }

    @Override
    protected Iterable<String> executeSearch(final AndSearchTerm input) {
      return executeAndSearch(input);
    }
  }

  protected final class OrSearchFunction extends SearchFunction<OrSearchTerm> {

    public OrSearchFunction() {
      super(OrSearchTerm.class);
    }

    @Override
    protected Iterable<String> executeSearch(final OrSearchTerm input) {
      return executeOrSearch(input);
    }
  }

  protected final class NegatedSearchFunction extends SearchFunction<NegatedEntrySearchTerm> {

    public NegatedSearchFunction() {
      super(NegatedEntrySearchTerm.class);
    }

    @Override
    protected Iterable<String> executeSearch(final NegatedEntrySearchTerm input) {
      return executeNegatedSearch(input);
    }
  }

  protected final class CompositeSearchFunction extends SearchFunction<CompositeEntrySearchTerm> {

    public CompositeSearchFunction() {
      super(CompositeEntrySearchTerm.class);
    }

    @Override
    protected Iterable<String> executeSearch(final CompositeEntrySearchTerm input) {
      return executeUnsupportedCompositeSearch(input);
    }
  }

  protected final class EntrySearchFunction extends SearchFunction<EntrySearchTerm> {

    public EntrySearchFunction() {
      super(EntrySearchTerm.class);
    }

    @Override
    protected Iterable<String> executeSearch(final EntrySearchTerm input) {
      return executeUnsupportedSearch(input);
    }
  }

  private static final Function<NegatedEntrySearchTerm, EntrySearchTerm> NEGATED_TERM_FUNCTION =
      new Function<NegatedEntrySearchTerm, EntrySearchTerm>() {

        @Override
        public EntrySearchTerm apply(final NegatedEntrySearchTerm input) {
          return null == input ? Constant.ANY_ENTRY : input.getNegatedTerm();
        }
      };

  private static final Function<EntrySearchTerm, Iterable<? extends EntrySearchTerm>> AND_EXPLODER =
      new Function<EntrySearchTerm, Iterable<? extends EntrySearchTerm>>() {

        @Override
        public Iterable<? extends EntrySearchTerm> apply(final EntrySearchTerm input) {
          return null == input ? Constant.NO_ENTRY.asList() :
              input instanceof AndSearchTerm ? ((AndSearchTerm) input).getComponents() : ImmutableList.of(input);
        }
      };

  private static final Function<EntrySearchTerm, Iterable<? extends EntrySearchTerm>> OR_EXPLODER =
      new Function<EntrySearchTerm, Iterable<? extends EntrySearchTerm>>() {

        @Override
        public Iterable<? extends EntrySearchTerm> apply(final EntrySearchTerm input) {
          return null == input ? Constant.NO_ENTRY.asList() :
              input instanceof OrSearchTerm ? ((OrSearchTerm) input).getComponents() : ImmutableList.of(input);
        }
      };

  // @formatter:off
  private final AtomicReference<ImmutableMap<Class<? extends EntrySearchTerm>, 
        ? extends Function<EntrySearchTerm, Iterable<String>>>> searchMap =
      new AtomicReference<ImmutableMap<Class<? extends EntrySearchTerm>, 
            ? extends Function<EntrySearchTerm, Iterable<String>>>>();
  // @formatter:on

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
  public Iterable<Entry> searchForEntries(final EntrySearchTerm term) {
    return Iterables.filter(Iterables.transform(searchForKeys(term), lookup), Predicates.notNull());
  }

  @Override
  public boolean searchForMatch(final EntrySearchTerm term) {
    return !Iterables.isEmpty(searchForKeys(term));
  }

  @Override
  public EntrySearchTerm key(final String entryKey) {
    return new KeySearchTerm(entryKey);
  }

  @Override
  public EntrySearchTerm and(final EntrySearchTerm... terms) {
    return and(ImmutableSet.copyOf(terms));
  }

  @Override
  public EntrySearchTerm and(final Iterable<? extends EntrySearchTerm> terms) {
    final Iterable<EntrySearchTerm> exploded = Iterables.concat(Iterables.transform(terms, AND_EXPLODER));
    final Iterable<EntrySearchTerm> filtered = Iterables.filter(exploded,
        Predicates.not(Predicates.<EntrySearchTerm> equalTo(Constant.ANY_ENTRY)));
    final ImmutableSet<EntrySearchTerm> simplified = ImmutableSet.copyOf(filtered);
    final EntrySearchTerm term;
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
  public EntrySearchTerm or(final EntrySearchTerm... terms) {
    return or(ImmutableSet.copyOf(terms));
  }

  @Override
  public EntrySearchTerm or(final Iterable<? extends EntrySearchTerm> terms) {
    final Iterable<EntrySearchTerm> exploded = Iterables.concat(Iterables.transform(terms, OR_EXPLODER));
    final Iterable<EntrySearchTerm> filtered = Iterables.filter(exploded,
        Predicates.not(Predicates.<EntrySearchTerm> equalTo(Constant.NO_ENTRY)));
    final ImmutableSet<EntrySearchTerm> simplified = ImmutableSet.copyOf(filtered);
    final EntrySearchTerm term;
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

  private boolean containsSelfNegation(final ImmutableSet<EntrySearchTerm> simplified) {
    final Iterable<EntrySearchTerm> negatedTerms = Iterables.transform(
        Iterables.filter(simplified, NegatedEntrySearchTerm.class),
        NEGATED_TERM_FUNCTION);
    return Iterables.any(negatedTerms, Predicates.in(simplified));
  }

  @Override
  public EntrySearchTerm not(final EntrySearchTerm term) {
    return term instanceof NegatedEntrySearchTerm ?
        ((NegatedEntrySearchTerm) term).getNegatedTerm() : new NotSearchTerm(term);
  }

  @Override
  public EntrySearchTerm referencesMatch(final String fieldKey, final EntrySearchTerm term) {
    return referencesMatch(ImmutableSet.of(fieldKey), term);
  }

  @Override
  public EntrySearchTerm referencesMatch(final Iterable<String> fieldKeys, final EntrySearchTerm term) {
    return Iterables.isEmpty(fieldKeys) || Constant.NO_ENTRY.equals(term) ? Constant.NO_ENTRY :
        new ReferencesMatchSearchTerm(fieldKeys, term);
  }

  @Override
  public EntrySearchTerm propertyValue(final String fieldKey, final String... values) {
    return propertyValue(fieldKey, ImmutableSet.copyOf(values));
  }

  @Override
  public EntrySearchTerm propertyValue(final String fieldKey, final Iterable<String> values) {
    return propertyValue(ImmutableSet.of(fieldKey), values);
  }

  @Override
  public EntrySearchTerm propertyValue(final Iterable<String> fieldKeys, final Iterable<String> values) {
    return Iterables.isEmpty(fieldKeys) || Iterables.isEmpty(values) ? Constant.NO_ENTRY :
        new FieldValueSearchTerm(fieldKeys, values);
  }

  protected final ImmutableMap<Class<? extends EntrySearchTerm>, ? extends Function<EntrySearchTerm, Iterable<String>>>
      getSearchMap() {
    ImmutableMap<Class<? extends EntrySearchTerm>, ? extends Function<EntrySearchTerm, Iterable<String>>> map =
        searchMap.get();
    if (null == map) {
      map = createSearchMap();
      if (!searchMap.compareAndSet(null, map)) {
        map = searchMap.get();
      }
    }
    return map;
  }

  protected ImmutableMap<Class<? extends EntrySearchTerm>, ? extends Function<EntrySearchTerm, Iterable<String>>>
      createSearchMap() {
    return ImmutableMap.<Class<? extends EntrySearchTerm>, Function<EntrySearchTerm, Iterable<String>>> builder()
        .put(Constant.class, new ConstantSearchFunction())
        .put(KeySearchTerm.class, new KeySearchFunction())
        .put(FieldValueSearchTerm.class, new FieldValueSearchFunction())
        .put(ReferencesMatchSearchTerm.class, new ReferencesMatchSearchFunction())
        .put(AndSearchTerm.class, new AndSearchFunction())
        .put(OrSearchTerm.class, new OrSearchFunction())
        .put(NegatedEntrySearchTerm.class, new NegatedSearchFunction())
        .put(CompositeEntrySearchTerm.class, new CompositeSearchFunction())
        .put(EntrySearchTerm.class, new EntrySearchFunction())
        .build();
  }

  protected Function<EntrySearchTerm, Iterable<String>> getSearchFunction(final EntrySearchTerm term) {
    // @formatter:off
    final ImmutableMap<Class<? extends EntrySearchTerm>, 
          ? extends Function<EntrySearchTerm, Iterable<String>>> functionMap = getSearchMap();
    // @formatter:on

    final Class<? extends EntrySearchTerm> termClass = term.getClass();
    final Function<EntrySearchTerm, Iterable<String>> searchFunction;
    if (functionMap.containsKey(termClass)) {
      searchFunction = functionMap.get(termClass);
    }
    else {
      searchFunction = functionMap.get(getFirstEntrySearchTermInterface(termClass));
    }
    return searchFunction;
  }

  private Class<? extends EntrySearchTerm> getFirstEntrySearchTermInterface(
      final Class<? extends EntrySearchTerm> termClass) {
    final List<Class<? extends EntrySearchTerm>> interfaces = Lists.newArrayList();
    for (Class<?> superClass = termClass; null != superClass; superClass = superClass.getSuperclass()) {
      for (final Class<?> interfaceClass : superClass.getInterfaces()) {
        if (EntrySearchTerm.class.isAssignableFrom(interfaceClass)) {
          interfaces.add(interfaceClass.asSubclass(EntrySearchTerm.class));
        }
      }
    }
    final Set<Class<? extends EntrySearchTerm>> notMostSpecific = Sets.newHashSet();
    for (final Class<? extends EntrySearchTerm> option : interfaces) {
      for (final Class<? extends EntrySearchTerm> test : interfaces) {
        if (option.isAssignableFrom(test) && !option.equals(test)) {
          notMostSpecific.add(option);
        }
      }
    }
    return Iterables.filter(interfaces, Predicates.not(Predicates.in(notMostSpecific))).iterator().next();
  }

  @Override
  public Iterable<String> searchForKeys(final EntrySearchTerm term) {
    return getSearchFunction(term).apply(term);
  }

  private Iterable<String> executeConstantSearchTerm(final Constant input) {
    return Constant.NO_ENTRY.equals(input) ? getNoKeys() : getAllKeys();
  }

  protected Iterable<String> getNoKeys() {
    return ImmutableList.of();
  }

  protected abstract Iterable<String> getAllKeys();

  protected Iterable<String> executeKeySearchTerm(final KeySearchTerm term) {
    final String key = term.getKey();
    return entryService.entryExists(key) ? ImmutableSet.of(key) : ImmutableSet.<String> of();
  }

  /**
   * Implements the basic filtering on a {@link FieldValueSearchTerm}. This implementation, while effective, should
   * generally be overridden by implementation specific search services.
   * 
   * @param term
   *          The search term to evaluate
   * @return The keys of matching entries
   */
  protected Iterable<String> executeFieldValueSearch(final FieldValueSearchTerm term) {
    return Iterables.filter(getAllKeys(), new SearchTermKeyPredicate(term));
  }

  protected Iterable<String> executeReferencesMatchSearch(final ReferencesMatchSearchTerm term) {
    final Iterable<String> keys = searchForKeys(term.getTerm());
    return searchForKeys(propertyValue(term.getReferenceFieldKeys(), keys));
  }

  protected Iterable<String> executeNegatedSearch(final NegatedEntrySearchTerm term) {
    return Iterables.filter(getAllKeys(), Predicates.not(new SearchTermKeyPredicate(term)));
  }

  protected Iterable<String> executeOrSearch(final OrSearchTerm term) {
    final Set<String> results = Sets.newHashSet();
    for (final EntrySearchTerm component : term.getComponents()) {
      Iterables.addAll(results, searchForKeys(component));
    }
    return results;
  }

  protected Iterable<String> executeAndSearch(final AndSearchTerm term) {
    final Set<EntrySearchTerm> negatedTerms = Sets.newHashSet();
    Set<String> results = null;
    for (final EntrySearchTerm component : term.getComponents()) {
      if (component instanceof NegatedEntrySearchTerm) {
        negatedTerms.add(((NegatedEntrySearchTerm) component).getNegatedTerm());
      }
      else if (null == results) {
        results = Sets.newHashSet(searchForKeys(component));
      }
      else {
        final Set<String> narrowed = Sets.newHashSet();
        final Iterable<String> keys = searchForKeys(component);
        for (final String key : keys) {
          if (results.contains(key)) {
            narrowed.add(key);
          }
        }
        results = narrowed;
      }
      if (null != results && results.isEmpty()) {
        break;
      }
    }
    if (!negatedTerms.isEmpty()) {
      filterEntryKeys(results, not(or(negatedTerms)));
    }
    return results;
  }

  protected Iterable<String> executeUnsupportedCompositeSearch(final CompositeEntrySearchTerm term) {
    return executeUnsupportedSearch(term);
  }

  protected Iterable<String> executeUnsupportedSearch(final EntrySearchTerm term) {
    return filterEntryKeys(getAllKeys(), term);
  }

  protected Iterable<String> filterEntryKeys(final Iterable<String> keys, final EntrySearchTerm term) {
    return Iterables.filter(keys, new SearchTermKeyPredicate(term));
  }

}
