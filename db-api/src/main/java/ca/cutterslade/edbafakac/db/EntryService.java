package ca.cutterslade.edbafakac.db;

public interface EntryService {

  Entry getNewEntry();

  Entry getNewEntry(String key);

  /**
   * 
   * @param key
   * @return
   * @throws EntryNotFoundException
   *           If the provided key does not locate an existing entry
   */
  Entry getEntry(String key);

  void saveEntry(Entry entry);

  void removeEntry(String key);

}
