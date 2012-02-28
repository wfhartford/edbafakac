package ca.cutterslade.edbafakac.db;

/**
 * Indicates that a specific {@link EntrySearchTerm} cannot be evaluated by the {@link EntrySearchService} which was
 * asked to execute the search.
 * 
 * @see EntrySearchService#searchForKeys(EntrySearchTerm)
 * @see EntrySearchService#searchForEntries(EntrySearchTerm)
 * 
 * @author W.F. Hartford
 */
public class UnsupportedSearchException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final EntrySearchTerm term;

  /**
   * Create an instance specifying a descriptive reason as to why the search cannot be executed, and the term which
   * cannot be executed.
   * 
   * @param reason
   *          The reason that the term is not supported by the implementation
   * @param term
   *          The search term which is not supported
   */
  public UnsupportedSearchException(final String reason, final EntrySearchTerm term) {
    super(reason);
    this.term = term;
  }

  /**
   * Get the search term which is not supported.
   * 
   * @return The unsupported search term
   */
  public EntrySearchTerm getTerm() {
    return term;
  }

}
