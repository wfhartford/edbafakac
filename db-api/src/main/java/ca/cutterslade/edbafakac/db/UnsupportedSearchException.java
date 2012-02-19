package ca.cutterslade.edbafakac.db;

/**
 * Indicates that a specific {@link SearchTerm} cannot be evaluated by the {@link SearchService} which was asked to
 * execute the search.
 * 
 * @see SearchService#searchForKeys(SearchTerm)
 * @see SearchService#searchForEntries(SearchTerm)
 * 
 * @author W.F. Hartford
 */
public class UnsupportedSearchException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final SearchTerm term;

  /**
   * Create an instance specifying a descriptive reason as to why the search cannot be executed, and the term which
   * cannot be executed.
   * 
   * @param reason
   *          The reason that the term is not supported by the implementation
   * @param term
   *          The search term which is not supported
   */
  public UnsupportedSearchException(final String reason, final SearchTerm term) {
    super(reason);
    this.term = term;
  }

  /**
   * Get the search term which is not supported.
   * 
   * @return The unsupported search term
   */
  public SearchTerm getTerm() {
    return term;
  }

}
