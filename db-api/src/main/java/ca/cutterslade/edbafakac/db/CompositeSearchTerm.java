package ca.cutterslade.edbafakac.db;

import java.util.Map;

import javax.annotation.Nonnull;


public interface CompositeSearchTerm extends SearchTerm {

  Iterable<? extends SearchTerm> getComponents();

  boolean combine(@Nonnull Map<? extends SearchTerm, Boolean> componentResults);
}
