package ca.cutterslade.edbafakac.db.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.cutterslade.edbafakac.db.Entry;

import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

// CSOFF: NestedTryDepth|MagicNumber
@SuppressWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class JdbcEntryServiceTest {

  private JdbcEntryService service;

  @Before
  public void setUp() throws SQLException, IOException {
    service = new JdbcEntryService();
    service.createTable();
  }

  @After
  public void tearDown() throws SQLException {
    try (final Connection connection = service.getConnection();
        final Statement statement = connection.createStatement();) {
      statement.execute("SHUTDOWN");
    }
    finally {
      service.close();
    }
  }

  @Test
  public void testSetUp() throws SQLException {
    try (final Connection connection = service.getConnection();
        final Statement statement = connection.createStatement();) {
      statement.execute("select * from edbafakac.entries");
      try (final ResultSet resultSet = statement.getResultSet();) {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        assertEquals(3, metaData.getColumnCount());
        assertEquals("ENTRY_KEY", metaData.getColumnName(1));
        assertEquals("PROPERTY_KEY", metaData.getColumnName(2));
        assertEquals("PROPERTY_VALUE", metaData.getColumnName(3));
        assertFalse(resultSet.next());
      }
    }
  }

  @Test
  public void testEmptyEntry() {
    Entry entry = service.getNewEntry();
    service.saveEntry(entry);
    entry = service.getEntry(entry.getKey());
    assertTrue(Sets.difference(entry.getPropertyKeys(), service.getReservedKeys()).isEmpty());
  }

}
