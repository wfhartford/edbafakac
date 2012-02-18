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

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import ca.cutterslade.edbafakac.db.Entry;
import ca.cutterslade.edbafakac.db.EntryAlreadyExistsException;
import ca.cutterslade.edbafakac.db.EntryNotFoundException;
import ca.cutterslade.edbafakac.db.EntryService;
import ca.cutterslade.edbafakac.db.EntryStoreException;
import ca.cutterslade.edbafakac.db.SearchService;
import ca.cutterslade.edbafakac.db.mem.MapEntry;
import ca.cutterslade.edbafakac.db.search.FieldValueSearchTerm;
import ca.cutterslade.utilities.PropertiesUtils;
import ca.cutterslade.utilities.ResultSetTransformer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class JdbcEntryService implements EntryService {

  private static final String DFLT_DATABASE_PROPERTIES_RESOURCE = "database.properties";

  private static final String JDBC_DRIVER_KEY = "edbafakac.jdbc.driver";

  private static final String JDBC_URL_KEY = "edbafakac.jdbc.url";

  private static final String JDBC_USER_KEY = "edbafakac.jdbc.user";

  private static final String JDBC_PASS_KEY = "edbafakac.jdbc.pass";

  private static final Set<JdbcEntryService> INSTANCES = Collections.synchronizedSet(Sets
      .<JdbcEntryService> newHashSet());

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {

      @Override
      public void run() {
        final RuntimeException exception = new RuntimeException();
        boolean sqlException = false;
        for (final JdbcEntryService service : Sets.newHashSet(INSTANCES)) {
          try {
            service.close();
          }
          catch (final SQLException e) {
            sqlException = true;
            exception.addSuppressed(e);
          }
        }
        if (sqlException) {
          throw exception;
        }
      }
    });
  }

  static final String PLACEHOLDER_STRING = "7575c422-d00b-468d-898b-0e6fa0200b32";

  private static final String PROPERTY_COUNT_QUERY = "select COUNT(1) from EDBAFAKAC.ENTRIES where ENTRY_KEY = ?";

  private static final String PROPERTIES_QUERY =
      "select PROPERTY_KEY,PROPERTY_VALUE from EDBAFAKAC.ENTRIES where ENTRY_KEY = ?";

  private static final String SEARCH_QUERY_FOR_KEYS_PREFIX =
      "select distinct ENTRY_KEY from EDBAFAKAC.ENTRIES where PROPERTY_KEY in ";

  private static final String SEARCH_QUERY_FOR_ENTRIES_PREFIX =
      "select ENTRY_KEY,PROPERTY_KEY,PROPERTY_VALUE from EDBAFAKAC.ENTRIES where ENTRY_KEY in (" +
          SEARCH_QUERY_FOR_KEYS_PREFIX;

  private static final String SEARCH_QUERY_MIDDLE = " and PROPERTY_VALUE in ";

  private static final String INSERT_PROPERTY =
      "insert into EDBAFAKAC.ENTRIES (ENTRY_KEY,PROPERTY_KEY,PROPERTY_VALUE) values (?, ?, ?)";

  private static final String DELETE_PROPERTIES = "delete from EDBAFAKAC.ENTRIES where ENTRY_KEY = ?";

  private static final ImmutableSet<String> RESERVED_KEYS = ImmutableSet.of(Entry.WRITE_TIME_KEY, PLACEHOLDER_STRING);

  private final DataSource dataSource;

  private final SearchService searchService = new JdbcSearchService(this);

  public JdbcEntryService() throws SQLException, IOException {
    this(DFLT_DATABASE_PROPERTIES_RESOURCE);
  }

  public JdbcEntryService(@Nonnull final String propertiesResource) throws SQLException, IOException {
    final ImmutableMap<String, String> props = PropertiesUtils.loadProperties(propertiesResource);
    final BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setDriverClassName(props.get(JDBC_DRIVER_KEY));
    basicDataSource.setUrl(props.get(JDBC_URL_KEY));
    basicDataSource.setUsername(props.get(JDBC_USER_KEY));
    basicDataSource.setPassword(props.get(JDBC_PASS_KEY));
    basicDataSource.getConnection().close();
    dataSource = basicDataSource;
  }

  @SuppressWarnings("PMD.ExcessiveParameterList")
  public JdbcEntryService(@Nonnull final String driver, @Nonnull final String url, @Nonnull final String user,
      @Nonnull final String pass)
      throws SQLException {
    final BasicDataSource basicDataSource = new BasicDataSource();
    basicDataSource.setDriverClassName(driver);
    basicDataSource.setUrl(url);
    basicDataSource.setUsername(user);
    basicDataSource.setPassword(pass);
    basicDataSource.getConnection().close();
    dataSource = basicDataSource;
  }

  public JdbcEntryService(@Nonnull final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public SearchService getSearchService() {
    return searchService;
  }

  @Override
  public Entry getNewEntry() {
    return new MapEntry(UUID.randomUUID().toString(), Maps.<String, String> newHashMap(), this, true);
  }

  @Override
  public Entry getNewEntry(@Nonnull final String key) {
    try {
      saveEntry(key, ImmutableMap.<String, String> of(), true);
      return new MapEntry(key, Maps.<String, String> newHashMap(), this, false);
    }
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  @Override
  public Entry getEntry(@Nonnull final String key) {
    try (final Connection connection = getConnection()) {
      final ImmutableList<Property> results =
          getQueryResult(connection, PROPERTIES_QUERY, ImmutableSet.of(key), PropertyTransformer.INSTANCE);
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
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  @VisibleForTesting
  Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  @Override
  public void saveEntry(@Nonnull final Entry entry) {
    ((MapEntry) entry).setWriteTime(System.currentTimeMillis());
    saveEntryWithoutUpdatingWriteTime(entry);
  }

  @Override
  public void saveEntryWithoutUpdatingWriteTime(@Nonnull final Entry entry) {
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
  public void removeEntry(@Nonnull final String key) {
    try (final Connection connection = getConnection()) {
      removeEntry(connection, key);
    }
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  private void saveEntry(@Nonnull final String key, @Nonnull final ImmutableMap<String, String> properties,
      final boolean newEntry) throws SQLException {
    try (final Connection connection = getConnection()) {
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
    }
  }

  private void insertProperties(@Nonnull final Connection connection, @Nonnull final String key,
      @Nonnull final ImmutableMap<String, String> properties) throws SQLException {
    try (final PreparedStatement statement = connection.prepareStatement(INSERT_PROPERTY)) {
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
  }

  private void removeEntry(@Nonnull final Connection connection, @Nonnull final String key) throws SQLException {
    try (final PreparedStatement statement = connection.prepareStatement(DELETE_PROPERTIES)) {
      statement.setString(1, key);
      statement.execute();
    }
  }

  @SuppressWarnings("PMD.ExcessiveParameterList")
  @edu.umd.cs.findbugs.annotations.SuppressWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
  private <T> ImmutableList<T> getQueryResult(@Nonnull final Connection connection, @Nonnull final String query,
      @Nonnull final Iterable<?> params, @Nonnull final Function<ResultSet, T> resultTransformer) throws SQLException {
    final ImmutableList<T> results;
    try (final PreparedStatement statement = connection.prepareStatement(query)) {
      int index = 0;
      for (final Object param : params) {
        statement.setObject(++index, param);
      }
      try (final ResultSet result = statement.executeQuery()) {
        results = ImmutableList.copyOf(new ResultSetTransformer<T>(result, resultTransformer));
      }
    }
    return results;
  }

  public void createTable() throws SQLException {
    try (final Connection connection = getConnection()) {
      createTable(connection);
    }
  }

  public static void createTable(@Nonnull final Connection connection) throws SQLException {
    boolean foundEdbafakac = false;
    final DatabaseMetaData metaData = connection.getMetaData();
    try (final ResultSet schemas = metaData.getSchemas()) {
      while (!foundEdbafakac && schemas.next()) {
        foundEdbafakac = "EDBAFAKAC".equals(schemas.getString("TABLE_SCHEM"));
      }
    }
    if (!foundEdbafakac) {
      try (final Statement statement = connection.createStatement()) {
        statement.execute("create schema EDBAFAKAC");
      }
    }
    boolean foundEntries = false;
    try (final ResultSet tables = metaData.getTables(null, "EDBAFAKAC", "ENTRIES", null)) {
      foundEntries = tables.next();
    }
    if (!foundEntries) {
      try (final Statement statement = connection.createStatement()) {
        statement.execute("create table EDBAFAKAC.ENTRIES(" +
            "ENTRY_KEY VARCHAR(500) not null," +
            "PROPERTY_KEY VARCHAR(500) not null," +
            "PROPERTY_VALUE VARCHAR(65535) not null," +
            "primary key(ENTRY_KEY,PROPERTY_KEY))");
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

  Iterable<Entry> searchForEntries(final FieldValueSearchTerm term) {
    try (Connection connection = getConnection()) {
      final ImmutableList<EntryProperty> entryProperties = getQueryResult(connection,
          getSearchQuery(term, SEARCH_QUERY_FOR_ENTRIES_PREFIX, ")"),
          Iterables.concat(term.getFieldKeys(), term.getValues()), EntryPropertyTransformer.INSTANCE);
      return createEntries(entryProperties);
    }
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  private Iterable<Entry> createEntries(final ImmutableList<EntryProperty> entryProperties) {
    final ImmutableList.Builder<Entry> builder = ImmutableList.builder();
    Map<String, String> propertyBuilder = null;
    String currentKey = null;
    for (final EntryProperty property : entryProperties) {
      if (!property.getEntryKey().equals(currentKey)) {
        currentKey = property.getEntryKey();
        propertyBuilder = Maps.newHashMap();
        builder.add(new MapEntry(currentKey, propertyBuilder, this, false));
      }
      if (!PLACEHOLDER_STRING.equals(property.getPropertyKey())) {
        propertyBuilder.put(property.getPropertyKey(), property.getPropertyValue());
      }
    }
    return builder.build();
  }

  Iterable<String> searchForKeys(final FieldValueSearchTerm term) {
    try (Connection connection = getConnection()) {
      return getQueryResult(connection, getSearchQuery(term, SEARCH_QUERY_FOR_KEYS_PREFIX, ""),
          Iterables.concat(term.getFieldKeys(), term.getValues()),
          ResultSetTransformer.STRING_TRANSFORMER);
    }
    catch (final SQLException e) {
      throw new EntryStoreException(e);
    }
  }

  private String getSearchQuery(final FieldValueSearchTerm term, final String prefix, final String suffix) {
    final StringBuilder builder = new StringBuilder(prefix.length() +
        (term.getFieldKeys().size() * 2) + 1 +
        SEARCH_QUERY_MIDDLE.length() +
        (term.getValues().size() * 2) + 1 +
        suffix.length());
    builder.append(prefix);
    appendParams(term.getFieldKeys().size(), builder);
    builder.append(SEARCH_QUERY_MIDDLE);
    appendParams(term.getValues().size(), builder);
    builder.append(suffix);
    return builder.toString();
  }

  private StringBuilder appendParams(final int count, final StringBuilder builder) {
    builder.append('(');
    for (int i = 0; i < count; i++) {
      builder.append("?,");
    }
    builder.setCharAt(builder.length() - 1, ')');
    return builder;
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
