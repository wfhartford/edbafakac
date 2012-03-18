package ca.cutterslade.edbafakac.db;

import javax.annotation.Nonnull;

/**
 * Allows searching for entries based on arbitrary implementations of the {@link EntrySearchTerm} interface. Whenever
 * possible, searches should be performed using {@link EntrySearchTerm} implementations returned by the methods of this
 * interface. Implementations will frequently have special optimisations, such as indexing, which can only be leveraged
 * by search terms managed by the implementation itself.
 * 
 * @author W.F. Hartford
 */
public interface EntrySearchService {

  /**
   * Execute a search returning the keys of all matching entries. An implementation should be able to search using any
   * arbitrary {@link EntrySearchTerm} implementation, it may choose not to support certain combinations in which case
   * an UnsupportedSearchException should be thrown.
   * 
   * @param term
   *          The term to search for
   * @return An {@link Iterable} containing the keys of all entries matching the search term
   * @throws UnsupportedSearchException
   *           if the provided search term cannot be evaluated
   */
  Iterable<String> searchForKeys(@Nonnull EntrySearchTerm term);

  /**
   * Execute a search returning all matching entries. An implementation should be able to search using any arbitrary
   * {@link EntrySearchTerm} implementation, it may choose not to support certain combinations in which case an
   * UnsupportedSearchException should be thrown.
   * 
   * @param term
   *          The term to search for
   * @return An {@link Iterable} containing all entries matching the search term
   * @throws UnsupportedSearchException
   *           if the provided search term cannot be evaluated
   */
  Iterable<Entry> searchForEntries(@Nonnull EntrySearchTerm term);

  /**
   * Execute a search to determine if matching entries exist.. An implementation should be able to search using any
   * arbitrary {@link EntrySearchTerm} implementation, it may choose not to support certain combinations in which case
   * an UnsupportedSearchException should be thrown.
   * 
   * @param term
   *          The term to search for
   * @return {@code true} if one or more entries exist which match the provided search term, {@code false} if no entry
   *         exists which would match the term
   * @throws UnsupportedSearchException
   *           if the provided search term cannot be evaluated
   */
  boolean searchForMatch(@Nonnull EntrySearchTerm term);

  /**
   * Create a {@link EntrySearchTerm} by combining the provided terms using boolean AND logic. In order to match the
   * returned term, an entry must match every provided term. If no terms are provided, the returned term will match any
   * entry.
   * 
   * @param terms
   *          The terms to combine using AND logic
   * @return A term which matches any entry which matches all of the provided terms
   */
  EntrySearchTerm and(@Nonnull EntrySearchTerm... terms);

  /**
   * Create a {@link EntrySearchTerm} by combining the provided terms using boolean AND logic. In order to match the
   * returned term, an entry must match every provided term. If no terms are provided, the returned term will match any
   * entry.
   * 
   * @param terms
   *          The terms to combine using AND logic
   * @return A term which matches any entry which matches all of the provided terms
   */
  EntrySearchTerm and(@Nonnull Iterable<? extends EntrySearchTerm> terms);

  /**
   * Create a {@link EntrySearchTerm} by combining the provided terms using boolean OR logic. In order to match the
   * returned term, an entry must match at least one provided term. If no terms are provided, the returned term will not
   * match any entry.
   * 
   * @param terms
   *          The terms to combine using OR logic
   * @return A term which matches any entry which matches at least one of the provided terms
   */
  EntrySearchTerm or(@Nonnull EntrySearchTerm... terms);

  /**
   * Create a {@link EntrySearchTerm} by combining the provided terms using boolean OR logic. In order to match the
   * returned term, an entry must match at least one provided term. If no terms are provided, the returned term will not
   * match any entry.
   * 
   * @param terms
   *          The terms to combine using OR logic
   * @return A term which matches any entry which matches at least one of the provided terms
   */
  EntrySearchTerm or(@Nonnull Iterable<? extends EntrySearchTerm> terms);

  /**
   * Create a {@link EntrySearchTerm} by negating the provided term. In order to match the returned term, an entry not
   * must match the provided term.
   * 
   * @param term
   *          The term to negate
   * @return A term which matches any entry which does not match the provided term
   */
  EntrySearchTerm not(@Nonnull EntrySearchTerm term);

  /**
   * Create a {@link EntrySearchTerm} which matches any entry which has the key of any entry which matches the provided
   * term in the specified property value.
   * 
   * @param propertyKey
   *          The key of the property whose value must equal the key of an any entry matched by the provided term
   * @param term
   *          The search term used to identify the entries which matched entries must reference
   * @return A search term which matches entries which reference entries matched by the provided term
   */
  EntrySearchTerm referencesMatch(@Nonnull String propertyKey, @Nonnull EntrySearchTerm term);

  /**
   * Create a {@link EntrySearchTerm} which matches any entry which has the key of any entry which matches the provided
   * term in any of the specified property values.
   * 
   * @param propertyKeys
   *          The keys of the properties whose value must equal the key of an any entry matched by the provided term
   * @param term
   *          The search term used to identify the entries which matched entries must reference
   * @return A search term which matches entries which reference entries matched by the provided term
   */
  EntrySearchTerm referencesMatch(@Nonnull Iterable<String> propertyKeys, @Nonnull EntrySearchTerm term);

  /**
   * Create a {@link EntrySearchTerm} which matches any entry with has the specified property matching any of the
   * specified values. If no values are provided, no entries will be matched.
   * 
   * @param propertyKey
   *          The key of the property which must match any of the specified values
   * @param values
   *          The values to search for
   * @return A search term matching entries with any of the specified property values
   */
  EntrySearchTerm propertyValue(@Nonnull String propertyKey, @Nonnull String... values);

  /**
   * Create a {@link EntrySearchTerm} which matches any entry with has the specified property matching any of the
   * specified values. If no values are provided, no entries will be matched.
   * 
   * @param propertyKey
   *          The key of the property which must match any of the specified values
   * @param values
   *          The values to search for
   * @return A search term matching entries with any of the specified property values
   */
  EntrySearchTerm propertyValue(@Nonnull String propertyKey, @Nonnull Iterable<String> values);

  /**
   * Create a {@link EntrySearchTerm} which matches any entry with has any of the specified properties matching any of
   * the specified values. If no property keys or no values are provided, no entries will be matched.
   * 
   * @param propertyKeys
   *          The keys of the properties which must match any of the specified values
   * @param values
   *          The values to search for
   * @return A search term matching entries with any of the specified property values
   */
  EntrySearchTerm propertyValue(@Nonnull Iterable<String> propertyKeys, @Nonnull Iterable<String> values);

  /**
   * Create a {@link EntrySearchTerm} which matches the entry with the provided key if it exists.
   * 
   * @param entryKey
   *          The key of the entry to search for
   * @return A search term matching the entry with the supplied key
   */
  EntrySearchTerm key(@Nonnull String entryKey);
}
