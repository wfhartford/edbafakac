package ca.cutterslade.edbafakac.db;

import java.util.Map;

import javax.annotation.Nonnull;


public interface CompositeEntrySearchTerm extends EntrySearchTerm {

  Iterable<? extends EntrySearchTerm> getComponents();

  boolean combine(@Nonnull Map<? extends EntrySearchTerm, Boolean> componentResults);
}
