package ca.cutterslade.edbafakac.db;

import java.util.ServiceLoader;

import javax.annotation.Nonnull;

/**
 * Provides instances of this package's service interfaces. An application should usually only deal with a single
 * instance of this class, and should probably only call its get methods once.
 * 
 * @author W.F. Hartford
 */
public final class EntryServiceFactory {

  private static EntryService defaultEntryService;

  private final Object entryServiceMutex = new Object();

  private EntryService entryService;

  private EntryServiceFactory(@Nonnull final EntryService entryService) {
    this.entryService = entryService;
  }

  /**
   * Get the {@link EntryService} provided by this instance.
   * 
   * @return An {@link EntryService} implementation, every invocation of this method on the same instance of
   *         {@link EntryServiceFactory} will return the same instance.
   */
  public EntryService getEntryService() {
    synchronized (entryServiceMutex) {
      if (null == entryService) {
        final ServiceLoader<EntryService> loader = ServiceLoader.load(EntryService.class);
        entryService = loader.iterator().next();
      }
      return entryService;
    }
  }

  /**
   * Set the {@link EntryService} instance to be returned by instances of {@link EntryServiceFactory} returned from future
   * calls to {@link #getInstance()}.
   * 
   * @param service
   *          The {@link EntryService} to be returned by future instances of {@link EntryServiceFactory}
   */
  public static void setDefaultEntryService(@Nonnull final EntryService service) {
    defaultEntryService = service;
  }

  /**
   * Get a {@link EntryServiceFactory}. If {@link #setDefaultEntryService(EntryService)} has previously been called, the
   * returned instance's {@link #getEntryService()} method will always return the {@link EntryService} provided to
   * {@link #setDefaultEntryService(EntryService)}.
   * 
   * @return An instance of {@link EntryServiceFactory}
   */
  public static EntryServiceFactory getInstance() {
    return new EntryServiceFactory(defaultEntryService);
  }
}
