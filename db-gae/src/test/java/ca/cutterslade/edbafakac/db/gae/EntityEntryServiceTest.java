package ca.cutterslade.edbafakac.db.gae;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntryServiceFactory;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class EntityEntryServiceTest {

  private static final String KEY = "e37d1e64-ed18-47dd-8501-baca4fea5b40";

  private static final String VALUE = "b4124b63-d57b-40f4-935a-e751bcca07da";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private EntryService service = EntryServiceFactory.INSTANCE.getEntryService();

  @Before
  public void setUp() {
    helper.setUp();
    service = EntryServiceFactory.INSTANCE.getEntryService();
  }

  @After
  public void tearDown() {
    service = null;
    helper.tearDown();
  }

  @Test
  public void getEntryTest() {
    final Entry entry = service.getNewEntry();
    Assert.assertNotNull(entry);
    Assert.assertNotNull(entry.getKey());
    Assert.assertTrue(entry.getPropertyKeys().isEmpty());
    entry.setProperty(KEY, VALUE);
    Assert.assertEquals(VALUE, entry.getProperty(KEY));
  }

  @Test
  public void saveEntryTest() {
    Entry entry = service.getNewEntry();
    entry.setProperty(KEY, VALUE);
    service.saveEntry(entry);
    entry = service.getEntry(entry.getKey());
    Assert.assertEquals(VALUE, entry.getProperty(KEY));
  }
}
