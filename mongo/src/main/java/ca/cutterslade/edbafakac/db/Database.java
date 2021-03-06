package ca.cutterslade.edbafakac.db;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bson.types.ObjectId;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public final class Database {

  private static final ConcurrentMap<Configuration, Database> INSTANCES = new MapMaker().weakKeys().makeMap();

  public static Database getInstance(final Configuration configuration) throws UnknownHostException {
    Database instance = INSTANCES.get(configuration);
    if (null == instance) {
      instance = new Database(configuration);
      final Database old = INSTANCES.putIfAbsent(configuration, instance);
      if (null != old) {
        instance = old;
      }
    }
    return instance;
  }

  public static Database getExistingInstance(final Configuration configuration) {
    final Database database = INSTANCES.get(configuration);
    Preconditions.checkArgument(null != database, "No database with specified configuration: %s", configuration);
    database.readLock.lock();
    try {
      Preconditions.checkArgument(!database.closed,
          "Databsae with specified configuration has been closed: %s", configuration);
    }
    finally {
      database.readLock.unlock();
    }
    return database;
  }

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private final Lock readLock = lock.readLock();

  private final Lock writeLock = lock.writeLock();

  private final Configuration configuration;

  private boolean closed;

  private final Mongo mongo;

  private final DB mongoDb;

  private final DBCollection entriesCollection;

  private final DBCollection logCollection;

  private Database(final Configuration configuration) throws UnknownHostException {
    this.configuration = configuration;
    mongo = new Mongo(new MongoURI(configuration.getMongoUri()));
    mongoDb = mongo.getDB(configuration.getDbName());
    entriesCollection = mongoDb.getCollection(configuration.getEntriesCollection());
    logCollection = mongoDb.getCollection(configuration.getLogCollection());
  }

  public void close() {
    writeLock.lock();
    try {
      if (!closed) {
        mongo.close();
        closed = true;
      }
    }
    finally {
      writeLock.unlock();
    }
  }

  private <F, T> T locked(final Function<F, T> function, final F input) {
    readLock.lock();
    try {
      Preconditions.checkState(!closed);
      return function.apply(input);
    }
    finally {
      readLock.unlock();
    }
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public DBCollection getEntriesCollection() {
    return entriesCollection;
  }

  private final Function<Entry, Void> saveFunction = new Function<Entry, Void>() {

    @Override
    public Void apply(final Entry entry) {
      Preconditions.checkArgument(!entry.isReadOnly());
      getEntriesCollection().save(entry.getObject());
      return null;
    }
  };

  public void save(final Entry entry) {
    locked(saveFunction, entry);
  }

  private final Function<DBObject, Entry> getEntryFunction = new Function<DBObject, Entry>() {

    @Override
    public Entry apply(final DBObject input) {
      final Type<? extends Entry> type =
          BasicField.getTypeField(getConfiguration()).getValue(input, true).asEntryType();
      return type.convertExternal(input, false);
    }
  };

  private final Function<DBObject, Entry> getReadOnlyEntryFunction = new Function<DBObject, Entry>() {

    @Override
    public Entry apply(final DBObject input) {
      final Type<? extends Entry> type =
          BasicField.getTypeField(getConfiguration()).getValue(input, true).asEntryType();
      return type.convertExternal(input, true);
    }
  };

  public Entry getEntry(final DBObject object, final boolean readOnly) {
    return locked(readOnly ? getReadOnlyEntryFunction : getEntryFunction, object);
  }

  private final Function<ObjectId, Entry> getFunction = new Function<ObjectId, Entry>() {

    @Override
    public Entry apply(final ObjectId input) {
      return getEntry(getEntriesCollection().findOne(input), false);
    }
  };

  public <T extends Entry> T get(final ObjectId objectId) {
    return (T) locked(getFunction, objectId);
  }

  private final Function<DBObject, Object> getQueryFunction = new Function<DBObject, Object>() {

    @Override
    public Object apply(final DBObject input) {
      return getEntry(getEntriesCollection().findOne(input), false);
    }
  };

  public <T extends Entry> T get(final DBObject query) {
    return (T) locked(getQueryFunction, query);
  }

  private BasicDBObject getTypeNameQuery(final BasicType type, final String name) {
    return new BasicDBObject()
        .append(BasicField.TYPE.getKey(), type.name())
        .append(BasicField.NAME.getKey(), name);
  }

  public Field<?> getField(final String name) {
    return get(getTypeNameQuery(BasicType.FIELD, name));
  }

  public Field<?> getField(final ObjectId id) {
    return get(id);
  }

  public Type<?> getType(final String name) {
    return get(getTypeNameQuery(BasicType.TYPE, name));
  }

  public Type<?> getType(final ObjectId id) {
    return get(id);
  }

}
