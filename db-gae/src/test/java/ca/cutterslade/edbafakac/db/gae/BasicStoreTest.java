package ca.cutterslade.edbafakac.db.gae;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class BasicStoreTest {

  private static final String TEST_STRING = "yam";

  private static final int TEST_LIMIT = 10;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testSanity() {
    Assert.assertEquals(2, 1 + 1);
  }

  // run this test twice to prove we're not leaking any state across tests
  private void doTest() {
    final DatastoreService service = DatastoreServiceFactory.getDatastoreService();
    Assert.assertEquals(0,
        service.prepare(new Query(TEST_STRING)).countEntities(FetchOptions.Builder.withLimit(TEST_LIMIT)));
    service.put(new Entity(TEST_STRING));
    service.put(new Entity(TEST_STRING));
    Assert.assertEquals(2,
        service.prepare(new Query(TEST_STRING)).countEntities(FetchOptions.Builder.withLimit(TEST_LIMIT)));
  }

  @Test
  public void testInsert1() {
    doTest();
  }

  @Test
  public void testInsert2() {
    doTest();
  }
}
