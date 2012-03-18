package ca.cutterslade.edbafakac.db.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ca.cutterslade.edbafakac.db.EntrySearchService;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.gae.EntityEntryService;
import ca.cutterslade.edbafakac.db.jdbc.JdbcEntryService;
import ca.cutterslade.edbafakac.db.mem.MapEntryService;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;

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

  protected EntrySearchService getSearchService() {
    return entryService.getSearchService();
  }

  @Parameters
  public static Collection<Object[]> getParameters() {
    final ImmutableList.Builder<Object[]> builder = ImmutableList.builder();
    for (final Iterator<EntryService> it = ServiceLoader.load(EntryService.class).iterator(); it.hasNext();) {
      builder.add(new Object[]{ it.next() });
    }
    return builder.build();
  }

  @Before
  public void setUp() {
    if (entryService instanceof EntityEntryService) {
      HELPER.setUp();
    }
  }

  @Before
  @edu.umd.cs.findbugs.annotations.SuppressWarnings("DMI_EMPTY_DB_PASSWORD")
  public void jdbcSetup() throws SQLException {
    if (entryService instanceof JdbcEntryService) {
      try (final Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:edbafakac", "sa", "");) {
        JdbcEntryService.createTable(connection);
      }
    }
  }

  @After
  public void tearDown() {
    if (entryService instanceof EntityEntryService) {
      HELPER.tearDown();
    }
  }

  @After
  @edu.umd.cs.findbugs.annotations.SuppressWarnings("DMI_EMPTY_DB_PASSWORD")
  public void jdbcTearDown() throws SQLException {
    if (entryService instanceof JdbcEntryService) {
      try (final Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:edbafakac", "sa", "")) {
        JdbcEntryService.dropTable(connection);
      }
    }
  }

  @After
  public void memTearDown() {
    if (entryService instanceof MapEntryService) {
      ((MapEntryService) entryService).clear();
    }
  }

}
