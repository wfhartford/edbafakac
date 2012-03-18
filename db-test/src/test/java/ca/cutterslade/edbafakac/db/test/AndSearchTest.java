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
import ca.cutterslade.edbafakac.db.gae.EntityEntryService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class AndSearchTest extends AvailableImplementationsTest {

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

  public AndSearchTest(final EntryService entryService) {
    super(entryService);
  }

  @Test
  public void simpleAndTermTest() {
    final EntryService service = getEntryService();
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueOne").setProperty("keyTwo", "valueOne"));
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueOne").setProperty("keyTwo", "valueTwo"));
    service.saveEntry(service.getNewEntry().setProperty("keyOne", "valueOne").setProperty("keyTwo", "valueThree"));
    final Entry entry = service.getNewEntry()
        .setProperty("keyOne", "valueOne")
        .setProperty("keyTwo", "valueFour");
    service.saveEntry(entry);
    final Iterable<Entry> search = service.getSearchService().searchForEntries(service.getSearchService().and(
        service.getSearchService().propertyValue("keyOne", "valueOne"),
        service.getSearchService().propertyValue("keyTwo", "valueFour")));
    assertNotNull(search);
    final Entry result = Iterables.getOnlyElement(search);
    assertEquals(entry.getKey(), result.getKey());
    assertEquals("valueOne", result.getProperty("keyOne"));
    assertEquals("valueFour", result.getProperty("keyTwo"));
  }

  @Test
  public void simpleAndTermTestNoResult() {
    final EntryService service = getEntryService();
    service.saveEntry(service.getNewEntry().setProperty("keyThree", "valueOne").setProperty("keyFour", "valueOne"));
    service.saveEntry(service.getNewEntry().setProperty("keyThree", "valueOne").setProperty("keyFour", "valueTwo"));
    service.saveEntry(service.getNewEntry().setProperty("keyThree", "valueOne").setProperty("keyFour", "valueThree"));
    service.saveEntry(service.getNewEntry().setProperty("keyThree", "valueOne").setProperty("keyFour", "valueFour"));
    final Iterable<Entry> search = service.getSearchService().searchForEntries(service.getSearchService().and(
        service.getSearchService().propertyValue("keyThree", "valueOne"),
        service.getSearchService().propertyValue("keyFour", "valueFive")));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
  }

}
