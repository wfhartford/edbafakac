package ca.cutterslade.edbafakac.db.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@RunWith(Parameterized.class)
public class DBImplsTest {

  private static final String KEY = "e37d1e64-ed18-47dd-8501-baca4fea5b40";

  private static final String VALUE = "b4124b63-d57b-40f4-935a-e751bcca07da";

  private static final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final EntryService entryService;

  @Parameters
  public static Collection<Object[]> getParameters() {
    final ImmutableList.Builder<Object[]> builder = ImmutableList.builder();
    for (final Iterator<EntryService> it = ServiceLoader.load(EntryService.class).iterator(); it.hasNext();) {
      builder.add(new Object[]{ it.next() });
    }
    return builder.build();
  }

  public DBImplsTest(final EntryService entryService) {
    this.entryService = entryService;
  }

  @BeforeClass
  public static void setUp() {
    helper.setUp();
  }

  @AfterClass
  public static void tearDown() {
    helper.tearDown();
  }

  @Test
  public void getEntryTest() {
    final Entry entry = entryService.getNewEntry();
    Assert.assertNotNull(entry);
    Assert.assertNotNull(entry.getKey());
    Assert.assertTrue(entry.getPropertyKeys().isEmpty());
    Assert.assertNull(entry.getProperty(KEY));
    entry.setProperty(KEY, VALUE);
    Assert.assertEquals(VALUE, entry.getProperty(KEY));
  }

  @Test
  public void saveEntryTest() {
    Entry entry = entryService.getNewEntry();
    entry.setProperty(KEY, VALUE);
    entryService.saveEntry(entry);
    entry = entryService.getEntry(entry.getKey());
    Assert.assertEquals(VALUE, entry.getProperty(KEY));
  }

  @Test(expected = EntryNotFoundException.class)
  public void noSaveEntryTest() {
    final Entry entry = entryService.getNewEntry();
    entryService.getEntry(entry.getKey());
  }

  @Test
  public void noSaveModificationTest() {
    Entry entry = entryService.getNewEntry();
    entryService.saveEntry(entry);
    entry.setProperty(KEY, VALUE);
    entry = entryService.getEntry(entry.getKey());
    Assert.assertFalse(entry.hasProperty(KEY));
    Assert.assertNull(entry.getProperty(KEY));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullPropertyKeyTest() {
    entryService.getNewEntry().setProperty(null, UUID.randomUUID().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullPropertyValueTest() {
    entryService.getNewEntry().setProperty(UUID.randomUUID().toString(), null);
  }

  @Test(expected = EntryAlreadyExistsException.class)
  public void newEntryWithUsedKeyTest() {
    final Entry entry = entryService.getNewEntry();
    entryService.saveEntry(entry);
    entryService.getNewEntry(entry.getKey());
  }
}
