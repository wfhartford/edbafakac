package ca.cutterslade.edbafakac.db.search;

import java.util.Objects;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.SearchTerm;

import com.google.common.collect.ImmutableSet;

public class ReferencesMatchSearchTerm implements SearchTerm {

  private final ImmutableSet<String> referenceFieldKeys;

  private final SearchTerm term;

  private final int hash;

  public ReferencesMatchSearchTerm(final String referenceFieldKey, final SearchTerm term) {
    this.referenceFieldKeys = ImmutableSet.of(referenceFieldKey);
    this.term = term;
    hash = Objects.hash(referenceFieldKeys, term);
  }

  @Override
  public boolean matches(final Entry entry, final SearchService service) {
    final Iterable<String> keys = service.searchForKeys(term);
    return new FieldValueSearchTerm(referenceFieldKeys, keys).matches(entry, service);
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
    final ReferencesMatchSearchTerm other = (ReferencesMatchSearchTerm) obj;
    if (hash != other.hash) {
      return false;
    }
    if (referenceFieldKeys == null) {
      if (other.referenceFieldKeys != null) {
        return false;
      }
    }
    else if (!referenceFieldKeys.equals(other.referenceFieldKeys)) {
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
    return referenceFieldKeys.iterator().next() + " references " + term;
  }

}
