package ca.cutterslade.edbafakac.db.mem;

import ca.cutterslade.edbafakac.db.SearchTerm;
import ca.cutterslade.edbafakac.db.search.AbstractSearchService;
import ca.cutterslade.edbafakac.db.search.FieldValueSearchTerm;

class MapSearchService extends AbstractSearchService<MapEntryService> {

  MapSearchService(final MapEntryService entryService) {
    super(entryService);
  }

  @Override
  public Iterable<String> searchForKeys(final SearchTerm term) {
    if (term instanceof FieldValueSearchTerm) {
      return getEntryService().searchForKeys((FieldValueSearchTerm) term);
    }
    throw new UnsupportedOperationException();
  }

}
