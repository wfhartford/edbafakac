package ca.cutterslade.edbafakac.db.search;

import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.CompositeEntrySearchTerm;
import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.NegatedEntrySearchTerm;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntrySearchTerm;

import com.google.common.collect.ImmutableList;

public class NotSearchTerm implements CompositeEntrySearchTerm, NegatedEntrySearchTerm {

  private final EntrySearchTerm term;

  private final ImmutableList<EntrySearchTerm> terms;

  private final int hash;

  NotSearchTerm(@Nonnull final EntrySearchTerm term) {
    this.term = term;
    this.terms = ImmutableList.of(term);
    hash = Objects.hash(this.term);
  }

  @Override
  public boolean matches(final Entry entry, final EntrySearchService service) {
    return !term.matches(entry, service);
  }

  @Override
  public Iterable<EntrySearchTerm> getComponents() {
    return terms;
  }

  @Override
  public boolean combine(final Map<? extends EntrySearchTerm, Boolean> componentResults) {
    return !componentResults.get(term).booleanValue();
  }

  @Override
  public EntrySearchTerm getNegatedTerm() {
    return term;
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
