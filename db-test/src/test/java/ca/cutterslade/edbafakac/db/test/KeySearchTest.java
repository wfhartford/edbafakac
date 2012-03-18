package ca.cutterslade.edbafakac.db.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.UUID;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.gae.EntityEntryService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class KeySearchTest extends AvailableImplementationsTest {

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

  public KeySearchTest(final EntryService entryService) {
    super(entryService);
  }

  @Test
  public void testKeySearchTerm() {
    final EntryService service = getEntryService();
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueOne"));
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueTwo"));
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueThree"));
    final Entry entry = service.getNewEntry().setProperty("keyOne", "valueFour");
    service.saveEntry(entry);
    final Iterable<Entry> search = getSearchService().searchForEntries(getSearchService().key(entry.getKey()));
    assertNotNull(search);
    final Entry result = Iterables.getOnlyElement(search);
    assertEquals(entry.getKey(), result.getKey());
    assertEquals("valueFour", result.getProperty("keyOne"));
  }

  @Test
  public void testKeySearchTermNoMatch() {
    final EntryService service = getEntryService();
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueOne"));
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueTwo"));
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueThree"));
    final Iterable<Entry> search = getSearchService().searchForEntries(
        getSearchService().key(UUID.randomUUID().toString()));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
  }

  @Test
  public void testKeySearchTermNoMatchAfterDelete() {
    final EntryService service = getEntryService();
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueOne"));
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueTwo"));
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueThree"));
    final Entry entry = service.getNewEntry().setProperty("keyOne", "valueFour");
    service.saveEntry(entry);
    service.removeEntry(entry.getKey());
    final Iterable<Entry> search = getSearchService().searchForEntries(
        getSearchService().key(entry.getKey()));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
  }
}
