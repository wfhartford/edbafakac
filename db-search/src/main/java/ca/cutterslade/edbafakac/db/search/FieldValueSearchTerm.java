package ca.cutterslade.edbafakac.db.search;

import java.util.Objects;

import javax.annotation.Nonnull;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntrySearchTerm;

import com.google.common.collect.ImmutableSet;

public class FieldValueSearchTerm implements EntrySearchTerm {

  private final ImmutableSet<String> fieldKeys;

  private final ImmutableSet<String> values;

  private final int hash;

  FieldValueSearchTerm(@Nonnull final Iterable<String> fieldKeys, @Nonnull final Iterable<String> values) {
    this.fieldKeys = ImmutableSet.copyOf(fieldKeys);
    this.values = ImmutableSet.copyOf(values);
    hash = Objects.hash(this.fieldKeys, this.values);
  }

  @Override
  public boolean matches(final Entry entry, final EntrySearchService service) {
    boolean match = false;
    for (final String key : fieldKeys) {
      if (values.contains(entry.getProperty(key))) {
        match = true;
        break;
      }
    }
    return match;
  }

  public ImmutableSet<String> getFieldKeys() {
    return fieldKeys;
  }

  public ImmutableSet<String> getValues() {
    return values;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  @SuppressWarnings({ "PMD.OnlyOneReturn", "PMD.NPathComplexity", "PMD.CyclomaticComplexity" })
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
    final FieldValueSearchTerm other = (FieldValueSearchTerm) obj;
    if (hash != other.hash) {
      return false;
    }
    if (fieldKeys == null) {
      if (other.fieldKeys != null) {
        return false;
      }
    }
    else if (!fieldKeys.equals(other.fieldKeys)) {
      return false;
    }
    if (values == null) {
      if (other.values != null) {
        return false;
      }
    }
    else if (!values.equals(other.values)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return fieldKeys + "=" + values;
  }

}
