package ca.cutterslade.edbafakac.db;

/**
 * Implemented by search terms which are negations of other search terms. The {@link SearchTerm} returned by
 * {@link #getNegatedTerm()} must be at least as simple as the object on which it is invoked. It is inappropriate and
 * potentially dangerous to implement {@link #getNegatedTerm()} by invoking the {@link SearchService#not(SearchTerm)}
 * method.
 * 
 * @author W.F. Hartford
 */
public interface NegatedSearchTerm extends SearchTerm {

  /**
   * Get the negation of this search term.
   * 
   * @return A Negation of this search term which is at least as simple as this search term
   */
  SearchTerm getNegatedTerm();
}
