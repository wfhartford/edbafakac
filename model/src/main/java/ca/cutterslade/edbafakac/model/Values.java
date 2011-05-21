package ca.cutterslade.edbafakac.model;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntryServiceFactory;

public final class Values {

  private Values() {
    throw new UnsupportedOperationException();
  }

  private static final class ServiceHolder {

    private static final EntryService entryService = EntryServiceFactory.INSTANCE.getEntryService();

    public static EntryService getEntryService() {
      return entryService;
    }
  }

  private static EntryService getEntryService() {
    return ServiceHolder.getEntryService();
  }

  public static Entry getNewEntry() {
    return getEntryService().getNewEntry();
  }

  public static <T> T getValue(final String key, final Class<T> clazz) {
    return null;
  }
}
