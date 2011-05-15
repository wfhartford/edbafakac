package ca.cutterslade.edbafakac.db;

import java.util.ServiceLoader;

public class EntryServiceFactory {

  public EntryService getEntryService() {
    final ServiceLoader<EntryService> loader = ServiceLoader.load(EntryService.class);
    return loader.iterator().next();
  }
}
