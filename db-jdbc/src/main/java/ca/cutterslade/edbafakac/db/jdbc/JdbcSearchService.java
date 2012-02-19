package ca.cutterslade.edbafakac.db.jdbc;

import ca.cutterslade.edbafakac.db.search.AbstractSearchService;
import ca.cutterslade.edbafakac.db.search.FieldValueSearchTerm;

public class JdbcSearchService extends AbstractSearchService<JdbcEntryService> {

  protected JdbcSearchService(final JdbcEntryService entryService) {
    super(entryService);
  }

  @Override
  protected Iterable<String> getAllKeys() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("getAllKeys has not been implemented");
  }

  @Override
  protected Iterable<String> executeFieldValueSearch(final FieldValueSearchTerm term) {
    return getEntryService().searchForKeys(term);
  }

}
