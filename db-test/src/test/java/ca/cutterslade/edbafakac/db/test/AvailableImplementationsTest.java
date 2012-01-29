package ca.cutterslade.edbafakac.db.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.jdbc.JdbcEntryService;

import com.google.appengine.repackaged.com.google.common.collect.ImmutableList;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

@SuppressWarnings("PMD")
@RunWith(Parameterized.class)
public abstract class AvailableImplementationsTest {

  private static final LocalServiceTestHelper HELPER =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  private final EntryService entryService;

  public AvailableImplementationsTest(final EntryService entryService) {
    this.entryService = entryService;
  }

  protected EntryService getEntryService() {
    return entryService;
  }

  @Parameters
  public static Collection<Object[]> getParameters() {
    final ImmutableList.Builder<Object[]> builder = ImmutableList.builder();
    for (final Iterator<EntryService> it = ServiceLoader.load(EntryService.class).iterator(); it.hasNext();) {
      builder.add(new Object[]{ it.next() });
    }
    return builder.build();
  }

  @BeforeClass
  public static void setUp() {
    HELPER.setUp();
  }

  @BeforeClass
  @edu.umd.cs.findbugs.annotations.SuppressWarnings("DMI_EMPTY_DB_PASSWORD")
  public static void jdbcSetup() throws SQLException {
    try (final Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:edbafakac", "sa", "");) {
      JdbcEntryService.createTable(connection);
    }
  }

  @AfterClass
  public static void tearDown() {
    HELPER.tearDown();
  }

}
