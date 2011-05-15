package ca.cutterslade.edbafakac.db;

public final class Configuration {

  public static final String DFLT_MONGO_URI = "mongodb://127.0.0.1";

  public static final String DFLT_DB_NAME = "edbafakac";

  public static final String DFLT_ENTRIES_COLLECTION = "entries";

  public static final String DFLT_LOG_COLLECTION = "log";

  public static final class Builder {

    private String mongoUri;

    private String dbName;

    private String entriesCollection;

    private String logCollection;

    public Builder setMongoUri(final String mongoUri) {
      this.mongoUri = mongoUri;
      return this;
    }

    public Builder setDBName(final String dbName) {
      this.dbName = dbName;
      return this;
    }

    public Builder setItemsCollection(final String entriesCollection) {
      this.entriesCollection = entriesCollection;
      return this;
    }

    public Builder setLogCollection(final String logCollection) {
      this.logCollection = logCollection;
      return this;
    }

    public String getMongoUri() {
      return null == mongoUri ? DFLT_MONGO_URI : mongoUri;
    }

    public String getDBName() {
      return null == dbName ? DFLT_DB_NAME : dbName;
    }

    public String getItemsCollection() {
      return null == entriesCollection ? DFLT_ENTRIES_COLLECTION : entriesCollection;
    }

    public String getLogCollection() {
      return null == logCollection ? DFLT_LOG_COLLECTION : logCollection;
    }

    public Configuration build() {
      return new Configuration(this);
    }
  }

  private final String mongoUri;

  private final String dbName;

  private final String entriesCollection;

  private final String logCollection;

  public static Builder builder() {
    return new Builder();
  }

  Configuration(final Builder builder) {
    mongoUri = builder.getMongoUri();
    dbName = builder.getDBName();
    entriesCollection = builder.getItemsCollection();
    logCollection = builder.getLogCollection();
  }

  public String getMongoUri() {
    return mongoUri;
  }

  public String getDbName() {
    return dbName;
  }

  public String getEntriesCollection() {
    return entriesCollection;
  }

  public String getLogCollection() {
    return logCollection;
  }

}
