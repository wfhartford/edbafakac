package ca.cutterslade.edbafakac.db;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

/**
 * Main service for accessing database entries.
 * 
 * @author W.F. Hartford
 */
public interface EntryService {

  /**
   * Get an implementation of the {@link EntrySearchService} interface capable of finding entries accessible using this
   * entry service.
   * 
   * @return An implementation of the {@link EntrySearchService} interface
   */
  EntrySearchService getSearchService();

  /**
   * Create a new entry with a random, unused key.
   * 
   * @return A new entry, not in the database until it is saved
   */
  Entry getNewEntry();

  /**
   * Create a new entry with the specified key.
   * 
   * @param key
   *          The key to used for the new entry.
   * @return A new entry with the specified key, which has been added to the database because of this method call
   * @throws EntryAlreadyExistsException
   *           If an entry already exists with the specified key
   */
  Entry getNewEntry(@Nonnull String key);

  /**
   * Retrieve an entry from the database.
   * 
   * @param key
   *          The key of the entry to retrieve
   * @return A previously existing entry with the specified key
   * @throws EntryNotFoundException
   *           If the provided key does not locate an existing entry
   */
  Entry getEntry(@Nonnull String key);

  /**
   * Determine if the provided key identifies an existing entry.
   * 
   * @param key
   *          The key of the entry
   * @return {@code true} if the provided key identifies an entry, {@code false} if it does not.
   */
  boolean entryExists(@Nonnull String key);

  /**
   * Save an entry to the database.
   * 
   * @param entry
   *          The entry to save
   */
  void saveEntry(@Nonnull Entry entry);

  /**
   * Save an entry to the database without adjusting its write time;
   * 
   * @param entry
   *          The entry to save
   */
  void saveEntryWithoutUpdatingWriteTime(@Nonnull Entry entry);

  /**
   * Remove an entry from the database. After being removed, the entry may be re-added by calling
   * {@link #saveEntry(Entry)}.
   * 
   * @param key
   *          The key of the entry to remove
   */
  void removeEntry(@Nonnull String key);

  /**
   * Get the {@link EntryService} implementation's reserved keys. This method must return the same set of keys over the
   * lifetime of an application.
   * 
   * @return The keys reserved by the EntryService implementation, including {@link Entry#WRITE_TIME_KEY}
   */
  ImmutableSet<String> getReservedKeys();

}
