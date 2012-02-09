package ca.cutterslade.edbafakac.db;

import javax.annotation.Nonnull;

public interface SearchService {

  Iterable<String> searchForKeys(@Nonnull SearchTerm term);

  Iterable<Entry> searchForEntries(@Nonnull SearchTerm term);

  SearchTerm and(@Nonnull SearchTerm... terms);

  SearchTerm and(@Nonnull Iterable<? extends SearchTerm> terms);

  SearchTerm or(@Nonnull SearchTerm... terms);

  SearchTerm or(@Nonnull Iterable<? extends SearchTerm> terms);

  SearchTerm not(@Nonnull SearchTerm term);

  SearchTerm referencesMatch(@Nonnull String fieldKey, @Nonnull SearchTerm term);

  SearchTerm referencesMatch(@Nonnull Iterable<String> fieldKeys, @Nonnull SearchTerm term);

  SearchTerm fieldValue(@Nonnull String fieldKey, @Nonnull String... values);

  SearchTerm fieldValue(@Nonnull String fieldKey, @Nonnull Iterable<String> values);

  SearchTerm fieldValue(@Nonnull Iterable<String> fieldkeys, @Nonnull Iterable<String> values);
}
