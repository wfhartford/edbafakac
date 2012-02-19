package ca.cutterslade.edbafakac.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.SearchTerm;
import ca.cutterslade.edbafakac.db.gae.EntityEntryService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@SuppressWarnings("PMD")
public class FieldValueSearchTest extends AvailableImplementationsTest {

  @Parameters
  public static Collection<Object[]> getParameters() {
    final ImmutableList.Builder<Object[]> builder = ImmutableList.builder();
    for (final Iterator<EntryService> it = ServiceLoader.load(EntryService.class).iterator(); it.hasNext();) {
      final EntryService service = it.next();
      // TODO Implement search in db-gae
      if (!(service instanceof EntityEntryService)) {
        builder.add(new Object[]{ service });
      }
    }
    return builder.build();
  }

  public FieldValueSearchTest(final EntryService entryService) {
    super(entryService);
  }

  @Test
  public void findMissingKey() {
    final Iterable<Entry> search =
        getSearchService().searchForEntries(getSearchService().propertyValue("unusedKey", "value"));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
  }

  @Test
  public void findValueTest() {
    final EntryService service = getEntryService();
    final Entry entry = service.getNewEntry().setProperty("keyOne", "valueOne");
    service.saveEntry(entry);
    final Iterable<Entry> search =
        getSearchService().searchForEntries(getSearchService().propertyValue("keyOne", "valueOne"));
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
    final Iterable<Entry> search =
        getSearchService().searchForEntries(getSearchService().propertyValue("keyTwo", "valueTwo"));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
  }

  @Test
  public void findAfterUpdateTest() {
    final EntryService service = getEntryService();
    final Entry entry = service.getNewEntry().setProperty("keyThree", "valueOne");
    service.saveEntry(entry);
    final Iterable<Entry> search =
        getSearchService().searchForEntries(getSearchService().propertyValue("keyThree", "valueTwo"));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
    entry.setProperty("keyThree", "valueTwo");
    service.saveEntry(entry);
    final Iterable<Entry> secondSearch =
        getSearchService().searchForEntries(getSearchService().propertyValue("keyThree", "valueTwo"));
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
    final SearchTerm searchTerm = getSearchService().propertyValue("keyFour", "valueOne");
    final Iterable<Entry> search = getSearchService().searchForEntries(searchTerm);
    assertNotNull(search);
    final Entry result = Iterables.getOnlyElement(search);
    assertEquals(entry.getKey(), result.getKey());
    assertEquals("valueOne", result.getProperty("keyFour"));
    entry.setProperty("keyFour", "valueTwo");
    service.saveEntry(entry);
    final Iterable<Entry> secondSearch = getSearchService().searchForEntries(searchTerm);
    assertNotNull(secondSearch);
    assertTrue(Iterables.isEmpty(secondSearch));
  }
}
