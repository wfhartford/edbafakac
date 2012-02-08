package ca.cutterslade.edbafakac.db.search;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.CompositeSearchTerm;
import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.NegatedSearchTerm;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.collect.ImmutableList;

public class NotSearchTerm implements CompositeSearchTerm, NegatedSearchTerm {

  private final SearchTerm term;

  private final int hash;

  NotSearchTerm(@Nonnull final SearchTerm term) {
    this.term = term;
    hash = Objects.hash(this.term);
  }

  @Override
  public boolean matches(final Entry entry, final SearchService service) {
    return !term.matches(entry, service);
  }

  @Override
  public Iterable<SearchTerm> getComponents() {
    return ImmutableList.of(term);
  }

  @Override
  public boolean combine(final Map<? extends SearchTerm, Boolean> componentResults) {
    return !componentResults.get(term).booleanValue();
  }

  @Override
  public SearchTerm getNegatedTerm() {
    return term;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final NotSearchTerm other = (NotSearchTerm) obj;
    if (hash != other.hash) {
      return false;
    }
    if (term == null) {
      if (other.term != null) {
        return false;
      }
    }
    else if (!term.equals(other.term)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "not(" + term + ")";
  }

}
