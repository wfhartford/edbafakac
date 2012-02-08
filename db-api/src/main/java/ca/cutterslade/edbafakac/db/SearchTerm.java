package ca.cutterslade.edbafakac.db;

import javax.annotation.Nonnull;

public interface SearchTerm {

  boolean matches(@Nonnull Entry entry, @Nonnull SearchService service);

}
