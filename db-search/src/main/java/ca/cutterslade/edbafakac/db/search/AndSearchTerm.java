package ca.cutterslade.edbafakac.db.search;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.CompositeEntrySearchTerm;
import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntrySearchTerm;

import com.google.common.collect.ImmutableSet;

public class AndSearchTerm implements CompositeEntrySearchTerm {

  private final ImmutableSet<EntrySearchTerm> terms;

  private final int hash;

  AndSearchTerm(@Nonnull final Iterable<? extends EntrySearchTerm> terms) {
    this.terms = ImmutableSet.copyOf(terms);
    hash = Objects.hash(this.terms);
  }

  @Override
  public boolean matches(final Entry entry, final EntrySearchService service) {
    boolean match = true;
    for (final EntrySearchTerm term : terms) {
      if (!term.matches(entry, service)) {
        match = false;
        break;
      }
    }
    return match;
  }

  @Override
  public Iterable<EntrySearchTerm> getComponents() {
    return terms;
  }

  @Override
  public boolean combine(final Map<? extends EntrySearchTerm, Boolean> componentResults) {
    return !componentResults.values().contains(Boolean.FALSE);
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  @SuppressWarnings("PMD.OnlyOneReturn")
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
