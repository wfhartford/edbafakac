package ca.cutterslade.edbafakac.db;

public interface EntryService {

  Entry getNewEntry();

  Entry getEntry(String key);

  void saveEntry(Entry entry);

  void removeEntry(String key);
}
