package ca.cutterslade.edbafakac.db;

public final class Configuration {

  public static final String DFLT_MONGO_URI = "mongodb://127.0.0.1";

  public static final String DFLT_DB_NAME = "edbafakac";

  public static final String DFLT_USER_COLLECTION = "users";

  public static final String DFLT_CONFIG_COLLECTION = "config";

  public static final String DFLT_ITEMS_COLLECTION = "items";

  public static final String DFLT_LOG_COLLECTION = "log";

  public static final class Builder {

    private String mongoUri;

    private String dbName;

    private String usersCollection;

    private String configCollection;

    private String itemsCollection;

    private String logCollection;

    public Builder setMongoUri(final String mongoUri) {
      this.mongoUri = mongoUri;
      return this;
    }

    public Builder setDBName(final String dbName) {
      this.dbName = dbName;
      return this;
    }

    public Builder setUsersCollection(final String usersCollection) {
      this.usersCollection = usersCollection;
      return this;
    }

    public Builder setConfigCollection(final String configCollection) {
      this.configCollection = configCollection;
      return this;
    }

    public Builder setItemsCollection(final String itemsCollection) {
      this.itemsCollection = itemsCollection;
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

    public String getUsersCollection() {
      return null == usersCollection ? DFLT_USER_COLLECTION : usersCollection;
    }

    public String getConfigCollection() {
      return null == configCollection ? DFLT_CONFIG_COLLECTION : configCollection;
    }

    public String getItemsCollection() {
      return null == itemsCollection ? DFLT_ITEMS_COLLECTION : itemsCollection;
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

  private final String usersCollection;

  private final String configCollection;

  private final String itemsCollection;

  private final String logCollection;

  public static Builder builder() {
    return new Builder();
  }

  Configuration(final Builder builder) {
    mongoUri = builder.getMongoUri();
    dbName = builder.getDBName();
    usersCollection = builder.getUsersCollection();
    configCollection = builder.getConfigCollection();
    itemsCollection = builder.getItemsCollection();
    logCollection = builder.getLogCollection();
  }

  public String getMongoUri() {
    return mongoUri;
  }

  public String getDbName() {
    return dbName;
  }

  public String getUsersCollection() {
    return usersCollection;
  }

  public String getConfigCollection() {
    return configCollection;
  }

  public String getItemsCollection() {
    return itemsCollection;
  }

  public String getLogCollection() {
    return logCollection;
  }

}
