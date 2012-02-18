package ca.cutterslade.edbafakac.db.jdbc;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.SearchTerm;
import ca.cutterslade.edbafakac.db.search.AbstractSearchService;
import ca.cutterslade.edbafakac.db.search.FieldValueSearchTerm;

public class JdbcSearchService extends AbstractSearchService<JdbcEntryService> {

  protected JdbcSearchService(final JdbcEntryService entryService) {
    super(entryService);
  }

  @Override
  public Iterable<String> searchForKeys(final SearchTerm term) {
    if (term instanceof FieldValueSearchTerm) {
      return getEntryService().searchForKeys((FieldValueSearchTerm) term);
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public Iterable<Entry> searchForEntries(final SearchTerm term) {
    if (term instanceof FieldValueSearchTerm) {
      return getEntryService().searchForEntries((FieldValueSearchTerm) term);
    }
    throw new UnsupportedOperationException();
  }

}
