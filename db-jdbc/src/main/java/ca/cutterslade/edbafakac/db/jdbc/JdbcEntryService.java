package ca.cutterslade.edbafakac.db.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntryStoreException;
import ca.cutterslade.edbafakac.db.mem.MapEntry;
import ca.cutterslade.utilities.PropertiesUtils;
import ca.cutterslade.utilities.ResultSetTransformer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class JdbcEntryService implements EntryService {

  private static final Set<JdbcEntryService> INSTANCES = Collections.synchronizedSet(Sets
      .<JdbcEntryService> newHashSet());

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {

      @Override
      public void run() {
        for (final JdbcEntryService service : Sets.newHashSet(INSTANCES)) {
          try {
            service.close();
          }
          catch (final SQLException e) {
            throw Throwables.propagate(e);
          }
        }
      }
    });
  }

  private static final class Property {

    private final String key;

    private final String value;

    public Property(final String key, final String value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }

    public boolean isPlaceholder() {
      return PLACEHOLDER_STRING.equals(key);
    }
  }

  private static final Function<ResultSet, Property> PROPERTY_TRANSFORMER = new Function<ResultSet, Property>() {

    @Override
    public Property apply(final ResultSet input) {
      try {
        return new Property(input.getString("PROPERTY_KEY"), input.getString("PROPERTY_VALUE"));
      }
      catch (final SQLException e) {
        throw Throwables.propagate(e);
      }
    }
  };

  private static final String PLACEHOLDER_STRING = "7575c422-d00b-468d-898b-0e6fa0200b32";

  private static final String PROPERTY_COUNT_QUERY = "select COUNT(1) from EDBAFAKAC.ENTRIES where ENTRY_KEY = ?";

  private static final String PROPERTIES_QUERY =
      "select PROPERTY_KEY,PROPERTY_VALUE from EDBAFAKAC.ENTRIES where ENTRY_KEY = ?";

  private static final String INSERT_PROPERTY =
      "insert into EDBAFAKAC.ENTRIES (ENTRY_KEY,PROPERTY_KEY,PROPERTY_VALUE) values (?, ?, ?)";

  private static final String DELETE_PROPERTIES = "delete from EDBAFAKAC.ENTRIES where ENTRY_KEY = ?";

  private static final ImmutableSet<String> RESERVED_KEYS = ImmutableSet.of(Entry.WRITE_TIME_KEY, PLACEHOLDER_STRING);

  private final DataSource dataSource;

  public JdbcEntryService() throws SQLException, IOException {
    final ImmutableMap<String, String> props = PropertiesUtils.loadProperties("database.properties");
    final BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setDriverClassName(props.get("edbafakac.jdbc.driver"));
    basicDataSource.setUrl(props.get("edbafakac.jdbc.url"));
    basicDataSource.setUsername(props.get("edbafakac.jdbc.user"));
    basicDataSource.setPassword(props.get("edbafakac.jdbc.pass"));
    basicDataSource.getConnection().close();
    dataSource = basicDataSource;
  }

  public JdbcEntryService(final String propertiesResource) throws SQLException, IOException {
    final ImmutableMap<String, String> props = PropertiesUtils.loadProperties(propertiesResource);
    final BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setDriverClassName(props.get("edbafakac.jdbc.driver"));
    basicDataSource.setUrl(props.get("edbafakac.jdbc.url"));
    basicDataSource.setUsername(props.get("edbafakac.jdbc.user"));
    basicDataSource.setPassword(props.get("edbafakac.jdbc.pass"));
    basicDataSource.getConnection().close();
    dataSource = basicDataSource;
  }

  @SuppressWarnings("PMD.ExcessiveParameterList")
  public JdbcEntryService(final String driver, final String url, final String user, final String pass)
      throws SQLException {
    final BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setDriverClassName(driver);
    basicDataSource.setUrl(url);
    basicDataSource.setUsername(user);
    basicDataSource.setPassword(pass);
    basicDataSource.getConnection().close();
    dataSource = basicDataSource;
  }

  public JdbcEntryService(final DataSource dataSoruce) {
    this.dataSource = dataSoruce;
  }

  @Override
  public Entry getNewEntry() {
    return new MapEntry(UUID.randomUUID().toString(), Maps.<String, String> newHashMap(), this, true);
  }

  @Override
  public Entry getNewEntry(final String key) {
    Preconditions.checkArgument(null != key);
    try {
      saveEntry(key, ImmutableMap.<String, String> of(), true);
      return new MapEntry(key, Maps.<String, String> newHashMap(), this, false);
    }
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  @Override
  public Entry getEntry(final String key) {
    Preconditions.checkArgument(null != key);
    try {
      final Connection connection = getConnection();
      try {
        final ImmutableList<Property> results =
            getQueryResult(connection, PROPERTIES_QUERY, ImmutableSet.of(key), PROPERTY_TRANSFORMER);
        if (results.isEmpty()) {
          throw new EntryNotFoundException(key);
        }
        final Map<String, String> properties = Maps.newHashMap();
        for (final Property prop : results) {
          if (!prop.isPlaceholder()) {
            properties.put(prop.getKey(), prop.getValue());
          }
        }
        return new MapEntry(key, properties, this, false);
      }
      finally {
        connection.close();
      }
    }
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  @VisibleForTesting
  Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public void saveEntry(final Entry entry) {
    Preconditions.checkArgument(null != entry);
    ((MapEntry) entry).setWriteTime(System.currentTimeMillis());
    saveEntryWithoutUpdatingWriteTime(entry);
  }

  @Override
  public void saveEntryWithoutUpdatingWriteTime(final Entry entry) {
    Preconditions.checkArgument(null != entry);
    Preconditions.checkArgument(entry.hasProperty(Entry.WRITE_TIME_KEY));
    try {
      saveEntry(entry.getKey(), entry.getProperties(), false);
      ((MapEntry) entry).saved();
    }
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  @Override
  public void removeEntry(final String key) {
    Preconditions.checkArgument(null != key);
    try {
      final Connection connection = getConnection();
      try {
        removeEntry(connection, key);
      }
      finally {
        connection.close();
      }
    }
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  private void saveEntry(final String key, final ImmutableMap<String, String> properties,
      final boolean newEntry) throws SQLException {
    boolean success = false;
    final Connection connection = getConnection();
    try {
      connection.setAutoCommit(false);
      if (newEntry &&
          0 != Iterables.getOnlyElement(getQueryResult(connection, PROPERTY_COUNT_QUERY, ImmutableSet.of(key),
              ResultSetTransformer.INTEGER_TRANSFORMER))) {
        throw new EntryAlreadyExistsException(key);
      }
      if (!newEntry) {
        removeEntry(connection, key);
      }
      insertProperties(connection, key, properties);
      connection.commit();
      success = true;
    }
    finally {
      if (!success) {
        connection.rollback();
      }
      connection.close();
    }
  }

  private void insertProperties(final Connection connection, final String key,
      final ImmutableMap<String, String> properties) throws SQLException {
    final PreparedStatement statement = connection.prepareStatement(INSERT_PROPERTY);
    try {
      statement.setString(1, key);
      statement.setString(2, PLACEHOLDER_STRING);
      statement.setString(3, PLACEHOLDER_STRING);
      statement.addBatch();
      for (final Map.Entry<String, String> property : properties.entrySet()) {
        statement.setString(1, key);
        statement.setString(2, property.getKey());
        statement.setString(3, property.getValue());
        statement.addBatch();
      }
      statement.executeBatch();
    }
    finally {
      statement.close();
    }
  }

  private void removeEntry(final Connection connection, final String key) throws SQLException {
    final PreparedStatement statement = connection.prepareStatement(DELETE_PROPERTIES);
    try {
      statement.setString(1, key);
      statement.execute();
    }
    finally {
      statement.close();
    }
  }

  @SuppressWarnings("PMD.ExcessiveParameterList")
  @edu.umd.cs.findbugs.annotations.SuppressWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
  private <T> ImmutableList<T> getQueryResult(final Connection connection, final String query,
      final Iterable<?> params, final Function<ResultSet, T> resultTransformer) throws SQLException {
    final PreparedStatement statement = connection.prepareStatement(query);
    try {
      int index = 0;
      for (final Object param : params) {
        statement.setObject(++index, param);
      }
      statement.execute();
      final ResultSet result = statement.getResultSet();
      try {
        return ImmutableList.copyOf(new ResultSetTransformer<T>(result, resultTransformer));
      }
      catch (final RuntimeException e) {
        Throwables.propagateIfInstanceOf(e.getCause(), SQLException.class);
        throw e;
      }
      finally {
        result.close();
      }
    }
    finally {
      statement.close();
    }
  }

  public void createTable() throws SQLException {
    final Connection connection = getConnection();
    try {
      createTable(connection);
    }
    finally {
      connection.close();
    }
  }

  public static void createTable(final Connection connection) throws SQLException {
    final DatabaseMetaData metaData = connection.getMetaData();
    final ResultSet schemas = metaData.getSchemas();
    boolean foundEdbafakac = false;
    try {
      while (!foundEdbafakac && schemas.next()) {
        foundEdbafakac = "EDBAFAKAC".equals(schemas.getString("TABLE_SCHEM"));
      }
    }
    finally {
      schemas.close();
    }
    if (!foundEdbafakac) {
      final Statement statement = connection.createStatement();
      try {
        statement.execute("create schema EDBAFAKAC");
      }
      finally {
        statement.close();
      }
    }
    boolean foundEntries = false;
    final ResultSet tables = metaData.getTables(null, "EDBAFAKAC", "ENTRIES", null);
    try {
      foundEntries = tables.next();
    }
    finally {
      tables.close();
    }
    if (!foundEntries) {
      final Statement statement = connection.createStatement();
      try {
        statement.execute("create table EDBAFAKAC.ENTRIES(" +
                "ENTRY_KEY VARCHAR(500) not null," +
                "PROPERTY_KEY VARCHAR(500) not null," +
                "PROPERTY_VALUE VARCHAR(65535) not null," +
                "primary key(ENTRY_KEY,PROPERTY_KEY))");
      }
      finally {
        statement.close();
      }
    }
  }

  @Override
  public ImmutableSet<String> getReservedKeys() {
    return RESERVED_KEYS;
  }

  public void close() throws SQLException {
    if (INSTANCES.remove(this)) {
      ((BasicDataSource) dataSource).close();
    }
  }

  // CSOFF: NoFinalizer|IllegalThrows
  @Override
  protected void finalize() throws Throwable {
    try {
      close();
    }
    finally {
      super.finalize();
    }
  }
  // CSON: NoFinalizer|IllegalThrows
}
