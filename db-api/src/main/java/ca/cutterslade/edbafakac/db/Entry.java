package ca.cutterslade.edbafakac.db;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * The basic database data object. An entry has a string for a key and any number of properties (string key value
 * pairs). An entry also provides access to the {@link EntryService} with produced it.
 * 
 * @author W.F. Hartford
 */
public interface Entry {

  String WRITE_TIME_KEY = "88f07fea-f2b0-4762-aee0-0ccfd61ed091";

  /**
   * Get the key of this entry, the key may not be modified once an entry exists. When an entry is created by the
   * {@link EntryService#getNewEntry()} method, it is assigned a random, unused key. A pre-set key can be specified when
   * an entry is created by calling the {@link EntryService#getNewEntry(String)} method.
   * 
   * @return The key of this entry
   */
  String getKey();

  /**
   * Get the time (in milliseconds) since this entry was last written; the write time must be maintained by the
   * {@link EntryService}.
   * 
   * @return The last modification time, or {@code null} if the entry has not been written and the write time has not
   *         been set via the {@link #setWriteTime(long)}
   */
  Long getWriteTime();

  /**
   * Set the entry's last write time; the write time is maintained by the EntryService, so this is only needed under
   * rare circumstances.
   * 
   * @param millis
   *          The time (in milliseconds) to set the write time to
   */
  void setWriteTime(long millis);

  /**
   * Set a property, overwriting the current value if one exists.
   * 
   * @param key
   *          The key of the property to set
   * @param value
   *          The value of the property
   * @return This entry, to enable chaining
   * @throws IllegalArgumentException
   *           if {@code key} or {@code value} is {@code null}
   */
  Entry setProperty(@Nonnull String key, @Nonnull String value);

  /**
   * Retrieve the value of a property.
   * 
   * @param key
   *          The key of the property to retrieve
   * @return The value of the property, or {@code null} if this entry has no property with the specified key
   * @throws IllegalArgumentException
   *           if {@code key} is {@code null}
   */
  String getProperty(@Nonnull String key);

  /**
   * Determine if this entry has a property with the specified key.
   * 
   * @param key
   *          The key of the property
   * @return {@code true} if the specified property exists, {@code false} if it does not
   * @throws IllegalArgumentException
   *           if {@code key} is {@code null}
   */
  boolean hasProperty(@Nonnull String key);

  /**
   * Remove the value associated with the provided property key. This method has no effect if the property is not set.
   * 
   * @param key
   *          The key of the property to remove
   * @return This entry, to enable chaining
   * @throws IllegalArgumentException
   *           if {@code key} is {@code null}
   */
  Entry removeProperty(@Nonnull String key);

  /**
   * Get all properties of this entry as a map.
   * 
   * @return An {@link ImmutableMap} containing all properties of this entry.
   */
  ImmutableMap<String, String> getProperties();

  /**
   * Get the keys of all properties of this entry.
   * 
   * @return An {@link ImmutableSet} containing the keys of all properties in this entry.
   */
  ImmutableSet<String> getPropertyKeys();

  /**
   * Determine if this entry has been modified since it was last saved.
   * 
   * @return {@code true} if this entry has been modified since it was saved, {@code false} if it has not been modified.
   */
  boolean isDirty();

  /**
   * Get the {@link EntryService} which produced this entry.
   * 
   * @return The {@link EntryService} which produced this entry
   */
  EntryService getEntryService();
}
