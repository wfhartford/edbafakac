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

public class ReferencesMatchSearchTest extends AvailableImplementationsTest {

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

  public ReferencesMatchSearchTest(final EntryService entryService) {
    super(entryService);
  }

  @Test
  public void testReferencesMatchSearch() {
    final EntryService service = getEntryService();
    final Entry referant = service.getNewEntry().setProperty("keyOne", "valueOne");
    service.saveEntry(referant);
    final Entry referer = service.getNewEntry().setProperty("reference", referant.getKey());
    service.saveEntry(referer);
    final Iterable<Entry> search = getSearchService().searchForEntries(
        getSearchService().referencesMatch("reference", getSearchService().propertyValue("keyOne", "valueOne")));
    assertNotNull(search);
    final Entry result = Iterables.getOnlyElement(search);
    assertEquals(referer.getKey(), result.getKey());
    assertEquals(referant.getKey(), result.getProperty("reference"));
  }

  @Test
  public void testReferencesMatchSearchNoResult() {
    final EntryService service = getEntryService();
    final Entry referant = service.getNewEntry().setProperty("keyOne", "valueOne");
    service.saveEntry(referant);
    final Entry referer = service.getNewEntry().setProperty("reference", referant.getKey());
    service.saveEntry(referer);
    final Iterable<Entry> search = getSearchService().searchForEntries(
        getSearchService().referencesMatch("reference", getSearchService().propertyValue("keyOne", "valueTwo")));
    assertNotNull(search);
    assertTrue(Iterables.isEmpty(search));
  }
}
