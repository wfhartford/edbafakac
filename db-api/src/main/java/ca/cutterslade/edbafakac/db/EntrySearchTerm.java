package ca.cutterslade.edbafakac.db;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Implementations define rules for matching entries which can be executed as searches by a {@link EntrySearchService}
 * implementation. A search service should be capable of executing a search based an any arbitrary {@code SearchTerm}
 * implementation, however, it is common for a search service to be optimised for implementations returned by the
 * {@code SearchService} interface itself.
 * <p>
 * Any implementation of this interface must be immutable, and should override the {@link #hashCode()} and
 * {@link #equals(Object)} methods.
 * 
 * @author W.F. Hartford
 */
@Immutable
public interface EntrySearchTerm {

  /**
   * Determine if the provided entry matches the conditions of this search term.
   * 
   * @param entry
   *          The entry to evaluate
   * @param service
   *          The search service executing the search
   * @return {@code true} if the provided entry matches the conditions of this search term, {@code false} if it does not
   */
  boolean matches(@Nonnull Entry entry, @Nonnull EntrySearchService service);

}
