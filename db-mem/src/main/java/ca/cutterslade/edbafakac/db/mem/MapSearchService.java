package ca.cutterslade.edbafakac.db.mem;

import ca.cutterslade.edbafakac.db.search.AbstractSearchService;
import ca.cutterslade.edbafakac.db.search.FieldValueSearchTerm;

public class MapSearchService extends AbstractSearchService<MapEntryService> {

  MapSearchService(final MapEntryService entryService) {
    super(entryService);
  }

  @Override
  protected Iterable<String> executeFieldValueSearch(final FieldValueSearchTerm term) {
    return getEntryService().searchForKeys(term);
  }

  @Override
  protected Iterable<String> getAllKeys() {
    return getEntryService().getAllKeys();
  }
}
