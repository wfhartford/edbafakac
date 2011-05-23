package ca.cutterslade.edbafakac.db;

import java.util.ServiceLoader;

/**
 * Provides instances of an implementation of the {@link EntryService} interface. An application should usually only
 * deal with a single instance of this class, and should probably only call {@link #getEntryService()} on that instance
 * once.
 * 
 * @author W.F. Hartford
 */
public final class EntryServiceFactory {

  private static EntryService defaultService;

  private final Object mutex = new Object();

  private EntryService service;

  private EntryServiceFactory(final EntryService service) {
    this.service = service;
  }

  /**
   * Get the {@link EntryService} provided by this instance.
   * 
   * @return An {@link EntryService} implementation, every invocation of this method on the same instance of
   *         {@link EntryServiceFactory} will return the same instance.
   */
  public EntryService getEntryService() {
    synchronized (mutex) {
      if (null == service) {
        final ServiceLoader<EntryService> loader = ServiceLoader.load(EntryService.class);
        service = loader.iterator().next();
      }
      return service;
    }
  }

  /**
   * Set the {@link EntryService} instance to be returned by instances of {@link EntryServiceFactory} returned from
   * future calls to {@link #getInstance()}.
   * 
   * @param service
   *          The {@link EntryService} to be returned by future instances of {@link EntryServiceFactory}
   */
  public static void setDefaultEntryService(final EntryService service) {
    defaultService = service;
  }

  /**
   * Get a {@link EntryServiceFactory}. If {@link #setDefaultEntryService(EntryService)} has previously been called, the
   * returned instance's {@link #getEntryService()} method will always return the {@link EntryService} provided to
   * {@link #setDefaultEntryService(EntryService)}.
   * 
   * @return An instance of {@link EntryServiceFactory}
   */
  public static EntryServiceFactory getInstance() {
    return new EntryServiceFactory(defaultService);
  }
}
