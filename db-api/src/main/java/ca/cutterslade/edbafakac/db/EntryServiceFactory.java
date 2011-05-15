package ca.cutterslade.edbafakac.db;

import java.util.ServiceLoader;

public enum EntryServiceFactory {
  INSTANCE;

  private final Object mutex = new Object();

  private EntryService service;

  public void setEntryService(final EntryService service) {
    synchronized (mutex) {
      this.service = service;
    }
  }

  public EntryService getEntryService() {
    synchronized (mutex) {
      if (null == service) {
        final ServiceLoader<EntryService> loader = ServiceLoader.load(EntryService.class);
        service = loader.iterator().next();
      }
      return service;
    }
  }
}
