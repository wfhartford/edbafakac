package ca.cutterslade.edbafakac.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.SearchTerm;
import ca.cutterslade.edbafakac.db.search.SearchTerms;

import com.google.common.collect.Iterables;

public class FieldValueSearchTest extends AvailableImplementationsTest {

  public FieldValueSearchTest(final EntryService entryService) {
    super(entryService);
  }

  @Test
  public void findMissingKey() {
    final Iterable<Entry> search = getEntryService().search(SearchTerms.fieldValue("unusedKey", "value"));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
  }

  @Test
  public void findValueTest() {
    final EntryService service = getEntryService();
    final Entry entry = service.getNewEntry().setProperty("keyOne", "valueOne");
    service.saveEntry(entry);
    final Iterable<Entry> search = service.search(SearchTerms.fieldValue("keyOne", "valueOne"));
    assertNotNull(search);
    final Entry result = Iterables.getOnlyElement(search);
    assertEquals(entry.getKey(), result.getKey());
    assertEquals("valueOne", result.getProperty("keyOne"));
  }

  @Test
  public void dontFindValueTest() {
    final EntryService service = getEntryService();
    final Entry entry = service.getNewEntry().setProperty("keyTwo", "valueOne");
    service.saveEntry(entry);
    final Iterable<Entry> search = service.search(SearchTerms.fieldValue("keyTwo", "valueTwo"));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
  }

  @Test
  public void findAfterUpdateTest() {
    final EntryService service = getEntryService();
    final Entry entry = service.getNewEntry().setProperty("keyThree", "valueOne");
    service.saveEntry(entry);
    final Iterable<Entry> search = service.search(SearchTerms.fieldValue("keyThree", "valueTwo"));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
    entry.setProperty("keyThree", "valueTwo");
    service.saveEntry(entry);
    final Iterable<Entry> secondSearch = service.search(SearchTerms.fieldValue("keyThree", "valueTwo"));
    assertNotNull(secondSearch);
    final Entry result = Iterables.getOnlyElement(secondSearch);
    assertEquals(entry.getKey(), result.getKey());
    assertEquals("valueTwo", result.getProperty("keyThree"));
  }

  @Test
  public void dontFindAfterUpdateTest() {
    final EntryService service = getEntryService();
    final Entry entry = service.getNewEntry().setProperty("keyFour", "valueOne");
    service.saveEntry(entry);
    final SearchTerm searchTerm = SearchTerms.fieldValue("keyFour", "valueOne");
    final Iterable<Entry> search = service.search(searchTerm);
    assertNotNull(search);
    final Entry result = Iterables.getOnlyElement(search);
    assertEquals(entry.getKey(), result.getKey());
    assertEquals("valueOne", result.getProperty("keyFour"));
    entry.setProperty("keyFour", "valueTwo");
    service.saveEntry(entry);
    final Iterable<Entry> secondSearch = service.search(searchTerm);
    assertNotNull(secondSearch);
    assertTrue(Iterables.isEmpty(search));
  }
}
