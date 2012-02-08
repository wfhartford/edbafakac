package ca.cutterslade.edbafakac.db.search;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.CompositeSearchTerm;
import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class AndSearchTerm implements CompositeSearchTerm {

  private final ImmutableSet<SearchTerm> terms;

  private final int hash;

  AndSearchTerm(@Nonnull final Iterable<? extends SearchTerm> terms) {
    this.terms = ImmutableSet.copyOf(terms);
    hash = Objects.hash(this.terms);
  }

  @Override
  public boolean matches(final Entry entry, final SearchService service) {
    return Iterables.all(terms, SearchTerms.entryPredicate(entry, service));
  }

  @Override
  public Iterable<SearchTerm> getComponents() {
    return terms;
  }

  @Override
  public boolean combine(final Map<? extends SearchTerm, Boolean> componentResults) {
    return !componentResults.values().contains(Boolean.FALSE);
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
    final AndSearchTerm other = (AndSearchTerm) obj;
    if (hash != other.hash) {
      return false;
    }
    if (terms == null) {
      if (other.terms != null) {
        return false;
      }
    }
    else if (!terms.equals(other.terms)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "and" + terms;
  }

}
