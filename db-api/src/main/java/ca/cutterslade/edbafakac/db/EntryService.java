package ca.cutterslade.edbafakac.db;

/**
 * Main service for accessing database entries.
 * 
 * @author W.F. Hartford
 */
public interface EntryService {

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
  Entry getNewEntry(String key);

  /**
   * Retrieve an entry from the database.
   * 
   * @param key
   *          The key of the entry to retrieve
   * @return A previously existing entry with the specified key
   * @throws EntryNotFoundException
   *           If the provided key does not locate an existing entry
   */
  Entry getEntry(String key);

  /**
   * Save an entry to the database.
   * 
   * @param entry
   *          The entry to save
   */
  void saveEntry(Entry entry);

  /**
   * Remove an entry from the database. After being removed, the entry may be re-added by calling
   * {@link #saveEntry(Entry)}.
   * 
   * @param key
   *          The key of the entry to remove
   */
  void removeEntry(String key);

}
