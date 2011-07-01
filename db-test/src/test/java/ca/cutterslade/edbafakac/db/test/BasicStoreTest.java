package ca.cutterslade.edbafakac.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@SuppressWarnings("PMD")
@RunWith(Parameterized.class)
public class BasicStoreTest extends AvailableImplementationsTest {

  private static final String KEY = "e37d1e64-ed18-47dd-8501-baca4fea5b40";

  private static final String VALUE = "b4124b63-d57b-40f4-935a-e751bcca07da";

  public BasicStoreTest(final EntryService entryService) {
    super(entryService);
  }

  @Test
  public void getEntryTest() {
    final Entry entry = getEntryService().getNewEntry();
    Assert.assertTrue(entry.isDirty());
    Assert.assertNotNull(entry);
    Assert.assertNotNull(entry.getKey());
    Assert.assertTrue(entry.getPropertyKeys().isEmpty());
    Assert.assertNull(entry.getProperty(KEY));
    entry.setProperty(KEY, VALUE);
    Assert.assertEquals(VALUE, entry.getProperty(KEY));
    Assert.assertTrue(entry.isDirty());
  }

  @Test
  public void saveEntryTest() {
    Entry entry = getEntryService().getNewEntry();
    Assert.assertTrue(entry.isDirty());
    entry.setProperty(KEY, VALUE);
    Assert.assertTrue(entry.isDirty());
    getEntryService().saveEntry(entry);
    Assert.assertFalse(entry.isDirty());
    entry = getEntryService().getEntry(entry.getKey());
    Assert.assertEquals(VALUE, entry.getProperty(KEY));
    Assert.assertFalse(entry.isDirty());
  }

  @Test(expected = EntryNotFoundException.class)
  public void noSaveEntryTest() {
    final Entry entry = getEntryService().getNewEntry();
    getEntryService().getEntry(entry.getKey());
  }

  @Test
  public void noSaveModificationTest() {
    Entry entry = getEntryService().getNewEntry();
    Assert.assertTrue(entry.isDirty());
    getEntryService().saveEntry(entry);
    Assert.assertFalse(entry.isDirty());
    entry.setProperty(KEY, VALUE);
    Assert.assertTrue(entry.isDirty());
    entry = getEntryService().getEntry(entry.getKey());
    Assert.assertFalse(entry.isDirty());
    Assert.assertFalse(entry.hasProperty(KEY));
    Assert.assertNull(entry.getProperty(KEY));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullPropertyKeyTest() {
    getEntryService().getNewEntry().setProperty(null, VALUE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullPropertyValueTest() {
    getEntryService().getNewEntry().setProperty(KEY, null);
  }

  @Test(expected = EntryAlreadyExistsException.class)
  public void newEntryWithUsedKeyTest() {
    final Entry entry = getEntryService().getNewEntry();
    Assert.assertTrue(entry.isDirty());
    getEntryService().saveEntry(entry);
    Assert.assertFalse(entry.isDirty());
    getEntryService().getNewEntry(entry.getKey());
  }

  @Test(expected = EntryNotFoundException.class)
  public void removeTest() {
    final Entry entry = getEntryService().getNewEntry();
    getEntryService().saveEntry(entry);
    getEntryService().removeEntry(entry.getKey());
    getEntryService().getEntry(entry.getKey());
  }

  @Test
  public void presetKeyTest() {
    final Entry entry = getEntryService().getNewEntry(KEY);
    Assert.assertFalse(entry.isDirty());
    Assert.assertEquals(KEY, entry.getKey());
    Assert.assertNotNull(getEntryService().getEntry(entry.getKey()));
  }

  @Test
  public void presetKeyNotSavedTest() {
    final Entry entry = getEntryService().getNewEntry();
    // this does not throw an exception because the first entry was never saved
    Assert.assertEquals(entry.getKey(), getEntryService().getNewEntry(entry.getKey()).getKey());
  }

  @Test(expected = EntryAlreadyExistsException.class)
  public void presetKeySavedTest() {
    final Entry entry = getEntryService().getNewEntry();
    getEntryService().saveEntry(entry);
    getEntryService().getNewEntry(entry.getKey());
  }

  @Test
  public void presetKeyAfterRemoveTest() {
    final Entry entry = getEntryService().getNewEntry();
    Assert.assertTrue(entry.isDirty());
    getEntryService().saveEntry(entry);
    Assert.assertFalse(entry.isDirty());
    getEntryService().removeEntry(entry.getKey());
    Assert.assertEquals(entry.getKey(), getEntryService().getNewEntry(entry.getKey()).getKey());
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullPresetKeyTest() {
    getEntryService().getNewEntry(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullGetEntryTest() {
    getEntryService().getEntry(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullRemovePropertyTest() {
    getEntryService().getNewEntry().removeProperty(null);
  }

  @Test
  public void hasPropertyTest() {
    final Entry entry = getEntryService().getNewEntry();
    Assert.assertTrue(entry.isDirty());
    entry.setProperty(KEY, VALUE);
    Assert.assertTrue(entry.isDirty());
    Assert.assertTrue(entry.hasProperty(KEY));
  }

  @Test
  public void getPropertiesTest() {
    final Entry entry = getEntryService().getNewEntry();
    Assert.assertTrue(entry.isDirty());
    entry.setProperty(KEY, VALUE);
    Assert.assertTrue(entry.isDirty());
    final ImmutableMap<String, String> properties = entry.getProperties();
    Assert.assertEquals(1, properties.size());
    Assert.assertEquals(VALUE, properties.get(KEY));
  }

  @Test
  public void getPropertyKeysTest() {
    final Entry entry = getEntryService().getNewEntry();
    Assert.assertTrue(entry.isDirty());
    entry.setProperty(KEY, VALUE);
    Assert.assertTrue(entry.isDirty());
    final ImmutableSet<String> propertyKeys = entry.getPropertyKeys();
    Assert.assertEquals(1, propertyKeys.size());
    Assert.assertEquals(KEY, Iterables.getOnlyElement(propertyKeys));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullSaveEntryTest() {
    getEntryService().saveEntry(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullRemoveEntryTest() {
    getEntryService().removeEntry(null);
  }

  @Test
  public void removePropertyTest() {
    Entry entry = getEntryService().getNewEntry();
    assertTrue(entry.isDirty());
    getEntryService().saveEntry(entry);
    assertFalse(entry.isDirty());
    entry = getEntryService().getEntry(entry.getKey());
    assertFalse(entry.isDirty());
    entry.setProperty(KEY, VALUE);
    assertTrue(entry.isDirty());
    getEntryService().saveEntry(entry);
    assertFalse(entry.isDirty());
    assertEquals(VALUE, entry.getProperty(KEY));
    entry = getEntryService().getEntry(entry.getKey());
    assertFalse(entry.isDirty());
    entry.removeProperty(KEY);
    assertTrue(entry.isDirty());
    assertFalse(entry.hasProperty(KEY));
    assertNull(entry.getProperty(KEY));
  }

  @Test
  public void testWriteTime() {
    final Entry entry = getEntryService().getNewEntry();
    assertNull(entry.getWriteTime());
    final long writeTime = System.currentTimeMillis();
    getEntryService().saveEntry(entry);
    assertNotNull(entry.getWriteTime());
    assertTrue(entry.getWriteTime().longValue() <= System.currentTimeMillis());
    assertTrue(writeTime <= entry.getWriteTime().longValue());
  }
}
