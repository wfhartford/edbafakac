package ca.cutterslade.edbafakac.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.util.Entries;

public class ImportExportTest extends AvailableImplementationsTest {

  private static final String ENTRY_KEY = "074b8281-4e86-4bf5-a12b-27b7bc5c9e3e";

  private static final String PROPERTY_KEY = "8e9b0335-371e-4789-b8ce-2f8692c45035";

  private static final String PROPERTY_VALUE = "6921b53d-81cd-4aaa-b672-df972b1655ff";

  private static final String NASTY_VALUE =
      "Commas (,), New-Lines (\n), and Backslashes (\\) are special cases: \\,\n\\\n\n";

  private static final String EXPORTED_FORM = ENTRY_KEY + ',' + PROPERTY_KEY + ',' + PROPERTY_VALUE + '\n';

  private static final String LONG_STRING;
  static {
    final StringBuilder builder = new StringBuilder(1024 * 16);
    for (int i = 0; i < 1024 * 16; i++) {
      builder.append(i % 10);
    }
    Assert.assertEquals(1024 * 16, builder.length());
    LONG_STRING = builder.toString();

  }

  public ImportExportTest(final EntryService entryService) {
    super(entryService);
  }

  @After
  public void removeEntry() {
    getEntryService().removeEntry(ENTRY_KEY);
  }

  @Test
  public void exportTest() {
    final Entry entry = getEntryService().getNewEntry(ENTRY_KEY);
    entry.setProperty(PROPERTY_KEY, PROPERTY_VALUE);
    final String exported = Entries.exportEntry(entry);
    assertEquals(EXPORTED_FORM, exported);
  }

  @Test
  public void importTest() {
    final Entry entry = Entries.importEntry(getEntryService(), EXPORTED_FORM);
    assertEquals(ENTRY_KEY, entry.getKey());
    assertTrue(entry.getPropertyKeys().contains(PROPERTY_KEY));
    assertEquals(PROPERTY_VALUE, entry.getProperty(PROPERTY_KEY));
  }

  @Test
  public void randomEntryExportImportTest() {
    final String propertyKey = UUID.randomUUID().toString();
    final String propertyValue = UUID.randomUUID().toString();
    Entry entry = getEntryService().getNewEntry().setProperty(propertyKey, propertyValue);
    final String entryKey = entry.getKey();
    final String exported =
        Entries.exportEntry(entry);
    entry = Entries.importEntry(getEntryService(), exported);
    assertEquals(entryKey, entry.getKey());
    assertTrue(entry.getPropertyKeys().contains(propertyKey));
    assertEquals(propertyValue, entry.getProperty(propertyKey));
  }

  @Test
  public void randomEntryImportExportImportTest() {
    final String entryKey = UUID.randomUUID().toString();
    final String propertyKey = UUID.randomUUID().toString();
    final String propertyValue = UUID.randomUUID().toString();
    final String exported = entryKey + ',' + propertyKey + ',' + propertyValue + '\n';
    Entry entry = Entries.importEntry(getEntryService(), exported);
    final Long writeTime = entry.getWriteTime();
    assertNotNull(writeTime);
    getEntryService().removeEntry(entryKey);
    entry = Entries.importEntry(getEntryService(),
        Entries.exportEntry(entry));
    assertEquals(entryKey, entry.getKey());
    assertEquals(writeTime, entry.getWriteTime());
    assertTrue(entry.getPropertyKeys().contains(propertyKey));
    assertEquals(propertyValue, entry.getProperty(propertyKey));
  }

  @Test
  public void nastyValueTest() {
    final Entry entry = Entries.importEntry(getEntryService(),
        Entries.exportEntry(getEntryService().getNewEntry().setProperty(PROPERTY_KEY, NASTY_VALUE)));
    assertEquals(NASTY_VALUE, entry.getProperty(PROPERTY_KEY));
  }

  @Test
  public void largeEntryTest() {
    final Entry entry = getEntryService().getNewEntry();
    for (int i = 0; i < 1024 * 8; i++) {
      entry.setProperty(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }
    getEntryService().saveEntry(entry);
    getEntryService().removeEntry(entry.getKey());
    final Entry rebuilt = Entries.importEntry(getEntryService(), Entries.exportEntry(entry));
    assertEquals(entry.getKey(), rebuilt.getKey());
    assertEquals(entry.getProperties(), rebuilt.getProperties());
  }

  @Test
  public void largeValueTest() {
    Entry entry = getEntryService().getNewEntry().setProperty(PROPERTY_KEY, LONG_STRING);
    entry = Entries.importEntry(getEntryService(), Entries.exportEntry(entry));
    assertEquals(LONG_STRING, entry.getProperty(PROPERTY_KEY));
    assertNotNull(entry.getWriteTime());
  }
}
